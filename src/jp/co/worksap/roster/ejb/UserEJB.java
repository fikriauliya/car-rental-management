package jp.co.worksap.roster.ejb;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.OrganizationUnit;
import jp.co.worksap.roster.entity.Role;
import jp.co.worksap.roster.entity.TransferLog;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.entity.UserRole;

import org.apache.commons.lang3.tuple.Pair;

@Stateless
public class UserEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public User findUser(String userId) {
		TypedQuery<User> q = em.createNamedQuery("findUser", User.class)
				.setParameter("id", userId);
		return q.getSingleResult();
	}

	public List<User> findAllUsers(int page, int size) {
		TypedQuery<User> q = em.createNamedQuery("findAllUsers", User.class)
				.setFirstResult(page * size)
				.setMaxResults(size);
		return q.getResultList();
	}

	public List<User> findAllUsers(int unitId, boolean isAttached, boolean isOrderedByLeaveTimestamp, int page, int size) {
		String queryName = isOrderedByLeaveTimestamp ? "findAllUsersInUnitByIsAttachedOrderByLeaveTimestamp" : "findAllUsersInUnitByIsAttached";

		TypedQuery<User> q = em.createNamedQuery(queryName, User.class)
				.setParameter("unitId", unitId)
				.setParameter("isAttached", isAttached)
				.setFirstResult(page * size)
				.setMaxResults(size);
		return q.getResultList();
	}

	public Pair<List<User>, Long> findAllUsers(String token, int page, int size) {
		TypedQuery<User> q = em.createNamedQuery("findAllEmployeesByIdNameAndEmail", User.class)
				.setParameter("token", "%" + token.toLowerCase() + "%")
				.setFirstResult(page * size)
				.setMaxResults(size);
		TypedQuery<Long> q1 = em.createNamedQuery("countAllEmployeesByIdNameAndEmail", Long.class)
				.setParameter("token", "%" + token.toLowerCase() + "%");
		return Pair.of(q.getResultList(), q1.getSingleResult());
	}

	public long countAllUsers(int unitId, boolean isAttached) {
		TypedQuery<Long> q = em.createNamedQuery("countAllUsers", Long.class)
				.setParameter("unitId", unitId)
				.setParameter("isAttached", isAttached);
		return q.getSingleResult();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createUser(User user, boolean isEmployee) {
		if (isEmployee) {
			UserRole ur = new UserRole();
			ur.setId(user.getId());
			ur.setRoleName("employee");
			em.persist(ur);
		} else {
			UserRole ur = new UserRole();
			ur.setId(user.getId());
			ur.setRoleName("customer");
			em.persist(ur);
		}

		em.persist(user);
	}

	public int deleteAllAssignedRoles(String userId) {
		TypedQuery<UserRole> q = em.createNamedQuery("deleteAllAssignedRoles", UserRole.class)
				.setParameter("id", userId);
		return q.executeUpdate();
	}

	public void updateUser(User user) {
		TypedQuery<User> q = em.createNamedQuery("findUser", User.class);
		q.setParameter("id", user.getId());
		User o = q.getSingleResult();

		o.setEmail(user.getEmail());
		o.setFirstName(user.getFirstName());
		o.setLastName(user.getLastName());
		o.setPhone(user.getPhone());

		if (o.isAttached() != user.isAttached()) {
			if (!user.isAttached()){
				o.setLeaveTimestamp(new Date());
				deleteAllAssignedRoles(user.getId());
			}
		}
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

	public List<Role> findAllRoles() {
		TypedQuery<Role> q = em.createNamedQuery("findAllRoles", Role.class);
		return q.getResultList();
	}

	public List<UserRole> findAllAssignedRoles(String userId) {
		TypedQuery<UserRole> q = em.createNamedQuery("findAllAssignedRoles", UserRole.class)
				.setParameter("id", userId);
		return q.getResultList();
	}

	public void updateRoles(String userId, String[] roles) {
		Set<String> sRoles = new HashSet<String>();
		for (String r : roles) sRoles.add(r);

		List<UserRole> assignedRoles = findAllAssignedRoles(userId);
		for (UserRole r : assignedRoles) {
			if (sRoles.contains(r.getRoleName())) {
				sRoles.remove(r.getRoleName());
			} else {
				em.remove(r);
			}
		}

		for (String r: sRoles) {
			UserRole ur = new UserRole();
			ur.setId(userId);
			ur.setRoleName(r);
			em.persist(ur);
		}
	}

	public int deleteUsersByUnit(int unitId) {
		TypedQuery<User> q = em.createNamedQuery("deleteUsersByUnit", User.class)
				.setParameter("unitId", unitId);
		return q.executeUpdate();
	}

	public int deleteUser(String userId) {
		TypedQuery<User> q = em.createNamedQuery("deleteUser", User.class)
				.setParameter("id", userId);
		return q.executeUpdate();
	}
}
