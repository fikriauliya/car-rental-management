package jp.co.worksap.roster.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.WebServiceException;

import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.rest.modelview.ChangePasswordData;

import org.apache.commons.codec.digest.DigestUtils;

@Path("/passwords")
@Stateless
public class PasswordService {
	@EJB
	private UserEJB userEJB;

	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response change(ChangePasswordData data, @Context HttpServletRequest request) {
		User user = userEJB.findUser(request.getUserPrincipal().getName());
		if (user.getPassword().equals(DigestUtils.md5Hex(data.getOldPassword()))) {
			user.setPassword(DigestUtils.md5Hex(data.getNewPassword()));
			em.persist(user);

			return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
		} else {
			throw new WebServiceException("Wrong old password");
		}
	}
}