package jp.co.worksap.roster.rest;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import jp.co.worksap.roster.ejb.InventoryEJB;
import jp.co.worksap.roster.ejb.ReservationEJB;
import jp.co.worksap.roster.ejb.UserAgendaEJB;
import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.Reservation;
import jp.co.worksap.roster.rest.modelview.ReservationRescheduleData;

@Stateless
@Path("/reservation_reschedules/")
public class ReservationRescheduleService {
	@EJB
	private InventoryEJB inventoryEJB;

	@EJB
	private ReservationEJB reservationEJB;

	@EJB
	private UserAgendaEJB userAgendaEJB;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Inventory> update(ReservationRescheduleData data, @Context HttpServletRequest request) {
		List<Reservation> reservations = reservationEJB.findReservations(data.getGroupId());
		Set<Integer> reservedInventory = inventoryEJB.findReservedInventories(reservations.get(0).getInventory().getOwner().getId(), data.getStartTime(), data.getEndTime(), data.getGroupId());

		List<Inventory> alreadyReservedInventory = new LinkedList<Inventory>();

		for (Reservation reservation : reservations) {
			Inventory inventory = reservation.getInventory();
			if (reservedInventory.contains(inventory.getId())) {
				alreadyReservedInventory.add(inventory);
			}
		}

		if (alreadyReservedInventory.isEmpty()) {
			//proceed with the reschedule
			for (Reservation reservation : reservations) {
				reservation.setStartTime(data.getStartTime());
				reservation.setEndTime(data.getEndTime());
				reservation.setInventoryFee(reservation.getInventory().getPrice().multiply(new BigDecimal((reservation.getEndTime().getTime() - reservation.getStartTime().getTime() + 1) / (60.0 * 60 * 1000))));
				reservation.setFullyPaid(false);

				reservationEJB.update(reservation);
			}
			userAgendaEJB.changeUserAgendaByTitle(String.valueOf(reservations.get(0).getInventory().getOwner().getId()) + "-" + String.valueOf(data.getGroupId()) + "-" + reservations.get(0).getCustomer().getUser().getFirstName() + " " + reservations.get(0).getCustomer().getUser().getLastName(), data.getStartTime(), data.getEndTime());
		}
		return alreadyReservedInventory;
	}
}
