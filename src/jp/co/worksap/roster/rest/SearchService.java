package jp.co.worksap.roster.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.User;
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
}
