package jp.co.worksap.roster.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceException;

import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.entity.UserRole;
import jp.co.worksap.roster.rest.modelview.PaginatedData;

import org.apache.commons.lang3.tuple.Pair;

@Path("/search")
@Stateless
public class SearchService {

	@EJB
	private UserEJB userEJB;

	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedData<User> user(
			@QueryParam("token") @DefaultValue("") String token,
			@QueryParam("page") int page,
			@QueryParam("size") int size) {
		Pair<List<User>, Long> res = userEJB.findAllUsers(token, page, size);
		return new PaginatedData<User>(res.getLeft(), page, res.getRight());
	}

	@GET
	@Path("/customer")
	public User customer(@QueryParam("id") String userId) {
		User u = userEJB.findUser(userId);
		if (u == null) {
			throw new WebServiceException("User not found");
		}

		List<UserRole> roles = userEJB.findAllAssignedRoles(u.getId());
		for (UserRole role:roles) {
			if (role.getRoleName().equals("customer")) {
				return u;
			}
		}

		throw new WebServiceException("User not found");
	}
}
