package jp.co.worksap.roster.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import jp.co.worksap.roster.ejb.ReservationEJB;
import jp.co.worksap.roster.entity.Reservation;

@Stateless
@Path("/reservation_histories/")
public class ReservationHistoryService {
	@EJB
	ReservationEJB reservationEJB;

	@GET
	@Path("/{inventoryId}")
	public List<Reservation> show(@PathParam("inventoryId") int inventoryId) {
		return reservationEJB.findReservationsByInventoryId(inventoryId);
	}
}
