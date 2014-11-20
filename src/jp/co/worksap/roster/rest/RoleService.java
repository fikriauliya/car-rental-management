package jp.co.worksap.roster.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.Role;

@Path("/roles")
@Stateless
public class RoleService {

	@EJB
	private UserEJB userEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({"admin" ,"hr"})
	public List<Role> index() {
		return userEJB.findAllRoles();
	}
}
