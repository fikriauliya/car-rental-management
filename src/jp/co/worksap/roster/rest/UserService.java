package jp.co.worksap.roster.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXB;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.sun.jersey.json.impl.provider.entity.JSONArrayProvider;

import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.User;

@Path("/users")
@Stateless
public class UserService {
	@EJB
	private UserEJB userEJB;

	@Context
	private UriInfo uriInfo;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> index() {
		List<User> users = userEJB.findAllUsers();
		return users;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Valid User user) {
		Set<ConstraintViolation<?>> errors = userEJB.createUser(user);
		if (errors == null) {
			return Response.created(uriInfo.getAbsolutePathBuilder().path(user.getId().toString()).build()).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(toResponses(errors)).type(MediaType.APPLICATION_JSON).build();
		}
	}

	public String toResponses(Set<ConstraintViolation<?>> errors) {
		List<String> res = new LinkedList<String>();
		for (ConstraintViolation<?> error : errors) {
			res.add(error.getPropertyPath().toString() + ": " + error.getMessage());
		}
		return (new Gson()).toJson(res);
	}
}