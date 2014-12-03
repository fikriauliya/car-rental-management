package jp.co.worksap.roster.ejb;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.Reservation;

@Stateless
public class ReservationEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public void createReservation(Reservation r) {
		em.persist(r);
	}

	public List<Reservation> findReservations(int branchId, Date startTime, Date endTime) {
		TypedQuery<Reservation> q = em.createNamedQuery("findReservations", Reservation.class)
				.setParameter("startTime", startTime)
				.setParameter("endTime", endTime)
				.setParameter("branchId", branchId);

		return q.getResultList();
	}
}
