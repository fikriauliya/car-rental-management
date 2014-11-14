package jp.co.worksap.roster.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.OrganizationUnit;
import jp.co.worksap.roster.entity.TransferLog;
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

	public long countAllUsers(int unitId, boolean isAttached) {
		TypedQuery<Long> q = em.createNamedQuery("countAllUsers", Long.class)
				.setParameter("unitId", unitId)
				.setParameter("isAttached", isAttached);
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

		if (o.getUnit().getId() != user.getUnit().getId()) {
			TypedQuery<OrganizationUnit> q3 = em.createNamedQuery("findOrganizationUnit", OrganizationUnit.class);
			q3.setParameter("id", o.getUnit().getId());
			OrganizationUnit fromUnit = q3.getSingleResult();

			TypedQuery<OrganizationUnit> q2 = em.createNamedQuery("findOrganizationUnit", OrganizationUnit.class);
			q2.setParameter("id", user.getUnit().getId());
			OrganizationUnit toUnit = q2.getSingleResult();
			o.setUnit(toUnit);

			TransferLog log = new TransferLog();
			log.setFromUnit(fromUnit);
			log.setToUnit(user.getUnit());
			log.setTimestamp(new Date());
			log.setUser(o);

			em.persist(log);
		}
		em.persist(o);
	}
}
