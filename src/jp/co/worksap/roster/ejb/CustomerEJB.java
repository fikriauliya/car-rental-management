package jp.co.worksap.roster.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.Customer;

@Stateless
public class CustomerEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createCustomer(Customer c) {
		em.persist(c);
	}

	public Customer findCustomer(int id) {
		TypedQuery<Customer> res = em.createNamedQuery("findCustomer", Customer.class)
				.setParameter("id", id);
		return res.getSingleResult();
	}

	public Customer findCustomerByUserId(String userId) {
		TypedQuery<Customer> res = em.createNamedQuery("findCustomerByUserId", Customer.class)
				.setParameter("userId", userId);
		return res.getSingleResult();
	}
}
