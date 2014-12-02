package jp.co.worksap.roster.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jp.co.worksap.roster.entity.Customer;

@Stateless
public class CustomerEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public void createCustomer(Customer c) {
		em.persist(c);
	}
}
