package jp.co.worksap.roster.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.User;

@Stateless
public class UserEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public List<User> findAllUsers(int page, int size) {
		TypedQuery<User> q = em.createNamedQuery("findAllUsers", User.class)
				.setFirstResult(page * size)
				.setMaxResults(size);
		return q.getResultList();
	}

	public List<User> findAllUsers(int unitId, int page, int size) {
		TypedQuery<User> q = em.createNamedQuery("findAllUsersInUnit", User.class)
				.setParameter("unitId", unitId)
				.setFirstResult(page * size)
				.setMaxResults(size);
		return q.getResultList();
	}

	public long countAllUsers() {
		TypedQuery<Long> q = em.createNamedQuery("countAllUsers", Long.class);
		return q.getSingleResult();
	}

	public void createUser(User user) {
		em.persist(user);
	}
}
