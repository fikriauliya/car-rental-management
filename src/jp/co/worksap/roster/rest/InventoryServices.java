package jp.co.worksap.roster.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import jp.co.worksap.roster.ejb.InventoryEJB;
import jp.co.worksap.roster.entity.BabySeatInventory;
import jp.co.worksap.roster.entity.Branch;
import jp.co.worksap.roster.entity.CarInventory;
import jp.co.worksap.roster.entity.GpsInventory;
import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.Inventory.InventoryType;

@Path("/inventories/{branchId}")
@Stateless
@RolesAllowed({"admin" ,"hr"})
public class InventoryServices {
	@EJB
	private InventoryEJB inventoryEJB;

	@EJB
	private BranchEJB branchEJB;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/car")
	public Response create(CarInventory inventory, @PathParam("branchId") int branchId) {
		Branch branch = branchEJB.findBranch(branchId);
		inventoryEJB.createInventory(inventory, branch);
		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/baby_seat")
	public Response create(BabySeatInventory inventory, @PathParam("branchId") int branchId) {
		Branch branch = branchEJB.findBranch(branchId);
		inventoryEJB.createInventory(inventory, branch);
		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/gps")
	public Response create(GpsInventory inventory, @PathParam("branchId") int branchId) {
		Branch branch = branchEJB.findBranch(branchId);
		inventoryEJB.createInventory(inventory, branch);
		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/car")
	public List<Inventory> indexCar() {
		return inventoryEJB.findInventories(InventoryType.CAR);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/baby_seat")
	public List<Inventory> indexBabySeat() {
		return inventoryEJB.findInventories(InventoryType.BABY_SEAT);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/gps")
	public List<Inventory> indexGps() {
		return inventoryEJB.findInventories(InventoryType.GPS);
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response destroy(@PathParam("id") int id) {
		inventoryEJB.deleteInventory(id);
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") int id, Inventory inventory) {
		inventoryEJB.updateInventory(id, inventory);
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory show(@PathParam("id") int id) {
		Inventory b = inventoryEJB.findInventory(id);
		return b;
	}
}