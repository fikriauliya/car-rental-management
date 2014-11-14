package jp.co.worksap.roster.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jp.co.worksap.roster.ejb.OrganizationEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.rest.modelview.UserWithUnit;
import jp.co.worksap.roster.rest.modelview.UsersInfo;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;

@Path("/users")
@Stateless
public class UserService {
	private final int SIZE = 10;

	@EJB
	private UserEJB userEJB;

	@EJB
	private OrganizationEJB orgEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UsersInfo index(@DefaultValue("0") @QueryParam("page") int page) {
		List<User> users = userEJB.findAllUsers(page, SIZE);
		long totalUsers = userEJB.countAllUsers();

		UsersInfo res = new UsersInfo();
		res.setUsers(users);
		res.setCurrentPage(page);
		res.setTotalPage(totalUsers / SIZE + 1);
		return res;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(UserWithUnit userWithUnit) {
		User u = new User();
		u.setAttached(true);
		u.setEmail(userWithUnit.getEmail());
		u.setFirstName(userWithUnit.getEmail());
		u.setLastName(userWithUnit.getLastName());
		if (userWithUnit.getPassword() != null)
			u.setPassword(DigestUtils.md5Hex(userWithUnit.getPassword()));
		u.setId(userWithUnit.getId());
		u.setUnit(orgEJB.getOrganizationUnit(userWithUnit.getUnitId()));

		userEJB.createUser(u);
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}

	public String toResponses(Set<ConstraintViolation<?>> errors) {
		List<String> res = new LinkedList<String>();
		for (ConstraintViolation<?> error : errors) {
			if (error.getPropertyPath() != null) {
				res.add(error.getPropertyPath().toString() + ": " + error.getMessage());
			} else {
				res.add(error.getMessage());
			}
		}
		return (new Gson()).toJson(res);
	}
}