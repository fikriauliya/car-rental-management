package jp.co.worksap.roster.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jp.co.worksap.roster.ejb.BranchEJB;
import jp.co.worksap.roster.entity.Branch;

@Path("/branches")
@Stateless
//@RolesAllowed({"employee", "admin" ,"hr"})
public class BranchServices {
	@EJB
	private BranchEJB branchEJB;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("")
	public Response create(Branch branch) {
		branchEJB.createBranch(branch);
		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("")
	public List<Branch> index() {
		return branchEJB.findBranches();
	}

	@DELETE
	@Path("/{id}")
	public Response destroy(@PathParam("id") int id) {
		branchEJB.deleteBranch(id);
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") int id, Branch branch) {
		branchEJB.updateBranch(id, branch);
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}
}