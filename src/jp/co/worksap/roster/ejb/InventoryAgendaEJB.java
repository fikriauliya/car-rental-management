package jp.co.worksap.roster.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jp.co.worksap.roster.entity.InventoryAgenda;

@Stateless
public class InventoryAgendaEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public void createInventoryAgenda(InventoryAgenda inventoryAgenda) {
		em.persist(inventoryAgenda);
	}
}
