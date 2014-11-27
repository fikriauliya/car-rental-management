package jp.co.worksap.roster.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.Branch;

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
		em.persist(b);
	}

	public Branch findBranch(int id) {
		TypedQuery<Branch> res = em.createNamedQuery("findBranch", Branch.class)
				.setParameter("id", id);
		return res.getSingleResult();
	}

	public List<Branch> findBranches() {
		TypedQuery<Branch> res = em.createNamedQuery("findBranches", Branch.class);
		return res.getResultList();
	}

	public int deleteBranch(int id) {
		TypedQuery<Branch> res = em.createNamedQuery("deleteBranch", Branch.class)
				.setParameter("id", id);
		return res.executeUpdate();
	}
}
