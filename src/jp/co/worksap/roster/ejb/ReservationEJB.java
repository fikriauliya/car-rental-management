package jp.co.worksap.roster.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jp.co.worksap.roster.entity.Reservation;

@Stateless
public class ReservationEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public void createReservation(Reservation r) {
		em.persist(r);
	}
}
