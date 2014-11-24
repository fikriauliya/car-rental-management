package jp.co.worksap.roster.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import jp.co.worksap.roster.ejb.UserAgendaEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.entity.UserAgenda;

@Path("/useragendas")
@Stateless
@RolesAllowed({"employee", "admin" ,"hr"})
public class UserAgendaService {
	@EJB
	private UserAgendaEJB userAgendaEJB;

	@EJB
	private UserEJB userEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/")
	@RolesAllowed({"admin" ,"hr", "employee"})
	public List<UserAgenda> detail(@PathParam("id") String userId, @QueryParam("start") long start, @QueryParam("end") long end){
	    Date startTime = new Date(new Timestamp(start).getTime());
	    Date endTime = new Date(new Timestamp(end).getTime());

		System.out.println("Detail");
		return userAgendaEJB.findUserAgendas(userId, startTime, endTime);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/")
	public Response create(@PathParam("id") String userId, UserAgenda userAgenda, @Context SecurityContext context) {
		String assignedByStr = context.getUserPrincipal().getName();

		User user = userEJB.findUser(userId);
		User assignedBy = userEJB.findUser(assignedByStr);

		userAgenda.setUser(user);
		userAgenda.setAssignedBy(assignedBy);
		userAgendaEJB.createUserAgenda(userAgenda);
		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response destroy(@QueryParam("eventId") int eventId, @Context SecurityContext context) {
		String currentUser = context.getUserPrincipal().getName();
		UserAgenda ua = userAgendaEJB.findUserAgenda(eventId);
		if (currentUser.equals(ua.getAssignedBy().getId()) || currentUser.equals(ua.getUser().getId())) {
			userAgendaEJB.deleteUserAgenda(eventId);
			return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
		}
		return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).build();
	}
}