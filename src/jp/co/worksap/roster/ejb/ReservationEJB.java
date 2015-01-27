package jp.co.worksap.roster.ejb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
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

	@EJB
	InventoryEJB inventoryEJB;

	public void createReservation(Reservation r) {
		em.persist(r);
	}

	public List<Reservation> findReservations(String customerId) {
		TypedQuery<Reservation> q = em.createNamedQuery("findReservationsByCustomerId", Reservation.class)
				.setParameter("customerId", customerId);
		return q.getResultList();
	}

	public Reservation findReservation(int id) {
		TypedQuery<Reservation> q = em.createNamedQuery("findReservationsById", Reservation.class)
				.setParameter("id", id);
		return q.getSingleResult();
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

			if (status == ReservationStatus.FINISHED) {
				Date now = new Date();
				reservation.setReturnedTime(now);
				if (now.getTime() > reservation.getEndTime().getTime()) {
					// Overdue
					BigDecimal overduePenaltyPercentage = reservation.getInventory().getOwner().getOverduePenaltyPercentage();
					double elapsedHour = (now.getTime() - reservation.getEndTime().getTime()) / (60.0 * 60 * 1000);
					reservation.setOverdueFee(reservation.getInventory().getPrice().multiply(new BigDecimal(elapsedHour)).multiply(overduePenaltyPercentage).divide(new BigDecimal(100), RoundingMode.HALF_UP));
					reservation.setFullyPaid(false);
				}
			}

			em.persist(reservation);
		}
	}

	public void update(Reservation reservation) {
		em.persist(reservation);
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

	public void markAsFullyPaid(List<Reservation> reservations) {
		for (Reservation reservation : reservations) {
			reservation.setFullyPaid(true);
			em.persist(reservation);
		}
	}

	public void markAsFullyUnpaid(List<Reservation> reservations) {
		for (Reservation reservation : reservations) {
			reservation.setFullyPaid(false);
			em.persist(reservation);
		}
	}

	public void updatePaidAmount(List<Reservation> reservations, BigDecimal paidAmount) {
		for (Reservation reservation : reservations) {
			reservation.setPaidAmount(paidAmount);
			em.persist(reservation);
		}
	}

	public void updatePenaltyFee(List<Reservation> reservations, BigDecimal penaltyFee) {
		for (Reservation reservation : reservations) {
			reservation.setPenaltyFee(penaltyFee);
			em.persist(reservation);
		}
	}

	private Date timestampToDate(long timestamp) {
		return new Date(new Timestamp(timestamp).getTime());
	}


	public boolean addReservation(long groupId, Inventory inventory) {
		Reservation other = this.findReservations(groupId).get(0);
		Set<Integer> reservedInventories = inventoryEJB.findReservedInventories(other.getInventory().getOwner().getId(), timestampToDate(other.getStartTime().getTime()), timestampToDate(other.getEndTime().getTime()), -1);

		if (reservedInventories.contains(inventory)) {
			return false;
		};
		Reservation reservation = new Reservation();
		reservation.setGroupId(groupId);
		reservation.setCardCIV(other.getCardCIV());
		reservation.setCardExpiryDate(other.getCardExpiryDate());
		reservation.setCardName(other.getCardName());
		reservation.setCardNumber(other.getCardNumber());

		reservation.setInventory(inventory);
		reservation.setInventoryVersion(inventory.getVersion());
		reservation.setStartTime(other.getStartTime());
		reservation.setEndTime(other.getEndTime());
		reservation.setCustomer(other.getCustomer());
		reservation.setInventoryFee(inventory.getPrice().multiply(new BigDecimal((other.getEndTime().getTime() - other.getStartTime().getTime() + 1) / (60.0 * 60 * 1000))));
		reservation.setAssignedDriver(other.getAssignedDriver());
		reservation.setDriverFee(other.getDriverFee());
		reservation.setPaidAmount(other.getPaidAmount());
		reservation.setStatus(other.getStatus());
		reservation.setFullyPaid(other.isFullyPaid());

		em.persist(reservation);
		return true;
	}

	public void delete(Reservation reservation) {
		em.remove(reservation);
	}
}
