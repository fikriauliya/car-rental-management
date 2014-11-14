package jp.co.worksap.roster.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.OrganizationUnit;
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

	public List<User> findAllUsers(int unitId, boolean isAttached, int page, int size) {
		TypedQuery<User> q = em.createNamedQuery("findAllUsersInUnitByIsAttached", User.class)
				.setParameter("unitId", unitId)
				.setParameter("isAttached", isAttached)
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

	public void updateUser(User user) {
		TypedQuery<User> q = em.createNamedQuery("findUser", User.class);
		q.setParameter("id", user.getId());
		User o = q.getSingleResult();

		o.setEmail(user.getEmail());
		o.setFirstName(user.getFirstName());
		o.setLastName(user.getLastName());
		o.setAttached(user.isAttached());

		em.persist(o);
	}
}
