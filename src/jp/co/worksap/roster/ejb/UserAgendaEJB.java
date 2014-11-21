package jp.co.worksap.roster.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.UserAgenda;

@Stateless
public class UserAgendaEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public List<UserAgenda> findUserAgendas(String userId, Date startTime, Date endTime) {
		TypedQuery<UserAgenda> q = em.createNamedQuery("findUserAgendasByDate", UserAgenda.class)
				.setParameter("startTime", startTime)
				.setParameter("endTime", endTime)
				.setParameter("userId", userId);
		return q.getResultList();
	}

	public void createUserAgenda(UserAgenda userAgenda) {
		em.persist(userAgenda);
	}

	public int deleteUserAgenda(int id) {
		TypedQuery<UserAgenda> q = em.createNamedQuery("deleteUserAgenda", UserAgenda.class)
				.setParameter("id", id);
		return q.executeUpdate();
	}
}
