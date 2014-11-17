package jp.co.worksap.roster.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jp.co.worksap.roster.ejb.OrganizationEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.Role;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.entity.UserRole;
import jp.co.worksap.roster.rest.modelview.UserDetail;
import jp.co.worksap.roster.rest.modelview.UserWithUnit;
import jp.co.worksap.roster.rest.modelview.UsersInfo;

import org.apache.commons.codec.digest.DigestUtils;

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
	public UsersInfo index(@QueryParam("unitId") int unitId, @QueryParam("isAttached") boolean isAttached, @DefaultValue("0") @QueryParam("page") int page) {
		List<User> users = userEJB.findAllUsers(unitId, isAttached, page, SIZE);
		long totalUsers = userEJB.countAllUsers(unitId, isAttached);

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

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateUser(User user) {
		userEJB.updateUser(user);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public UserDetail detail(@PathParam("id") String userId){
		List<UserRole> assignedRoles = userEJB.findAllAssignedRoles(userId);
		List<Role> availableRoles = userEJB.findAllRoles();
		UserDetail res = new UserDetail();

		List<String> assignedRolesStr = new LinkedList<String>();
		List<String> availableRolesStr = new LinkedList<String>();
		for (UserRole r : assignedRoles) {
			assignedRolesStr.add(r.getRoleName());
		}

		for (Role r : availableRoles) {
			availableRolesStr.add(r.getRole());
		}

		res.setAssignedRoles(assignedRolesStr);
		res.setAvailableRoles(availableRolesStr);
		return res;
	}

	@PUT
	@Path("/{id}")
	public void update(@PathParam("id") String id, String[] roles) {
		userEJB.updateRoles(id, roles);
	}
}