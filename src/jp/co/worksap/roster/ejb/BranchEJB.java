package jp.co.worksap.roster.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.Branch;
import jp.co.worksap.roster.entity.User;

@Stateless
public class BranchEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public void createBranch(Branch branch) {
		em.persist(branch);
	}

	public void updateBranch(int id, Branch branch) {
		Branch b = findBranch(id);
		b.setAddress(branch.getAddress());
		b.setLatitude(branch.getLatitude());
		b.setLongitude(branch.getLongitude());
		b.setName(branch.getName());
		b.setTimezone(branch.getTimezone());
		b.setOpeningHour(branch.getOpeningHour());
		b.setClosingHour(branch.getClosingHour());
		b.setDriverFee(branch.getDriverFee());
		b.setBufferHour(branch.getBufferHour());
		b.setOverduePenaltyPercentage(branch.getOverduePenaltyPercentage());
		b.setCurrencySymbol(branch.getCurrencySymbol());
		em.persist(b);
	}

	public void addUser(int id, User user) {
		Branch b = findBranch(id);
		if (!b.getUsers().contains(user)) {
			b.getUsers().add(user);
			em.persist(b);
		}
	}

	public void removeUser(int id, User user) {
		Branch b = findBranch(id);
		if (b.getUsers().contains(user)) {
			b.getUsers().remove(user);
			em.persist(b);
		}
	}

	public Branch findBranch(int id) {
		TypedQuery<Branch> res = em.createNamedQuery("findBranch", Branch.class)
				.setParameter("id", id);
		return res.getSingleResult();
	}

	public List<Branch> findBranches() {
		System.out.println("Find branches");
		TypedQuery<Branch> res = em.createNamedQuery("findBranches", Branch.class);
		return res.getResultList();
	}

	public void deleteBranch(int id) {
		Branch b = findBranch(id);
		b.setDeleted(true);
		em.persist(b);
	}
}
