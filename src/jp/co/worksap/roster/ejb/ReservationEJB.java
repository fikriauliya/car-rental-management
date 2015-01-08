package jp.co.worksap.roster.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.InventoryStatus;
import jp.co.worksap.roster.entity.Reservation;
import jp.co.worksap.roster.entity.ReservationStatus;

@Stateless
public class ReservationEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public void createReservation(Reservation r) {
		em.persist(r);
	}

	public List<Reservation> findReservations(int branchId, Date startTime) {
		TypedQuery<Reservation> q = em.createNamedQuery("findReservationsByStartDate", Reservation.class)
				.setParameter("startTime", startTime)
				.setParameter("branchId", branchId);
		return q.getResultList();
	}
	public List<Reservation> findReservations(int branchId, Date startTime, Date endTime) {
		TypedQuery<Reservation> q = em.createNamedQuery("findReservationsByDate", Reservation.class)
				.setParameter("startTime", startTime)
				.setParameter("endTime", endTime)
				.setParameter("branchId", branchId);

		return q.getResultList();
	}

	public List<Reservation> findReservations(long groupId) {
		TypedQuery<Reservation> q = em.createNamedQuery("findReservationsByGroupId", Reservation.class)
				.setParameter("groupId", groupId);
		return q.getResultList();
	}

	public List<Reservation> findReservationsByInventoryId(int inventoryId) {
		TypedQuery<Reservation> q = em.createNamedQuery("findReservationsByInventoryId", Reservation.class)
				.setParameter("inventoryId", inventoryId);
		return q.getResultList();
	}

	public void updateStatus(List<Reservation> reservations, ReservationStatus status) {
		for (Reservation reservation : reservations) {
			reservation.setStatus(status);
			em.persist(reservation);
		}
	}

	public void updateInventories(List<Reservation> reservations, InventoryStatus status) {
		for (Reservation reservation : reservations) {
			Inventory inventory = reservation.getInventory();
			inventory.setStatus(status);
			em.persist(inventory);
		}
	}

	public boolean isEligibleForRent(List<Reservation> reservations) {
		for (Reservation reservation: reservations) {
			if (reservation.getInventory().getStatus() != InventoryStatus.AVAILABLE) {
				return false;
			}
		}
		return true;
	}

	public void markAsPaid(List<Reservation> reservations) {
		for (Reservation reservation : reservations) {
			reservation.setPaid(true);
			em.persist(reservation);
		}
	}
}
