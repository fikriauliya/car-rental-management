package jp.co.worksap.roster.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
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

import jp.co.worksap.roster.ejb.CustomerEJB;
import jp.co.worksap.roster.ejb.InventoryEJB;
import jp.co.worksap.roster.ejb.ReservationEJB;
import jp.co.worksap.roster.entity.Customer;
import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.Reservation;
import jp.co.worksap.roster.rest.modelview.ReservationInfo;

@Stateless
@Path("/reservations/")
public class ReservationService {
	@EJB
	InventoryEJB inventoryEJB;

	@EJB
	ReservationEJB reservationEJB;

	@EJB
	CustomerEJB customerEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{branchId}")
	public List<Reservation> index(@PathParam("branchId") int branchId,
			@QueryParam("startTime") long startTime, @QueryParam("endTime") long endTime) {
		return reservationEJB.findReservations(branchId, timestampToDate(startTime), timestampToDate(endTime));
	}

	private Date timestampToDate(long timestamp) {
		return new Date(new Timestamp(timestamp).getTime());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(ReservationInfo reservationInfo, @Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		long timestamp = (new Date()).getTime();
		Customer customer = customerEJB.findCustomerByUserId(userId);

		for (int inventoryId : reservationInfo.getInventoryIds()) {
			Reservation reservation = new Reservation();
			reservation.setGroupId(timestamp);
			reservation.setCardCIV(reservationInfo.getCardCIV());
			reservation.setCardExpiryDate(reservationInfo.getCardExpiryDate());
			reservation.setCardName(reservationInfo.getCardName());
			reservation.setCardNumber(reservationInfo.getCardNumber());
			reservation.setCustomer(reservation.getCustomer());

			Inventory inventory = inventoryEJB.findInventory(inventoryId);
			reservation.setInventory(inventory);
			reservation.setInventoryVersion(inventory.getVersion());
			reservation.setStartTime(reservationInfo.getStartTime());
			reservation.setEndTime(reservationInfo.getEndTime());
			reservation.setCustomer(customer);

			reservationEJB.createReservation(reservation);
		}

		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
	}
}
