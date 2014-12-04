package jp.co.worksap.roster.ejb;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.BabySeatInventory;
import jp.co.worksap.roster.entity.Branch;
import jp.co.worksap.roster.entity.CarInventory;
import jp.co.worksap.roster.entity.GpsInventory;
import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.InventoryStatus;

@Stateless
public class InventoryEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public void createInventory(Inventory inventory, Branch branch) {
		inventory.setStatus(InventoryStatus.AVAILABLE);
		inventory.setOwner(branch);
		inventory.setCreatedAt(new Date());
		inventory.setUpdatedAt(inventory.getCreatedAt());

		if (inventory instanceof CarInventory) {
			em.persist((CarInventory)inventory);
		} else if (inventory instanceof BabySeatInventory) {
			em.persist((BabySeatInventory)inventory);
		} else if (inventory instanceof GpsInventory) {
			em.persist((GpsInventory)inventory);
		}
	}

	public void updateInventory(int id, Inventory inventory) {
		Inventory b = findInventory(id);
		b.setName(inventory.getName());
		b.setStatus(inventory.getStatus());
		b.setPrice(inventory.getPrice());
		b.setUpdatedAt(new Date());

		if (inventory instanceof CarInventory) {
			((CarInventory) b).copyPropertiesFrom((CarInventory)inventory);
		} else if (inventory instanceof BabySeatInventory) {
			((BabySeatInventory) b).copyPropertiesFrom((BabySeatInventory)inventory);
		} else if (inventory instanceof GpsInventory) {
			((GpsInventory) b).copyPropertiesFrom((GpsInventory)inventory);
		}
		em.persist(b);
	}

	public Inventory findInventory(int id) {
		TypedQuery<Inventory> res = em.createNamedQuery("findInventory", Inventory.class)
				.setParameter("id", id);
		return res.getSingleResult();
	}

	public List<Inventory> findInventories(Inventory.InventoryType type, int branchId, Date startTime, Date endTime) {
		TypedQuery<Inventory> q1 = null;
		switch (type) {
			case CAR:
				q1 = em.createNamedQuery("findCarInventories", Inventory.class);
				break;
			case BABY_SEAT:
				q1 = em.createNamedQuery("findBabySeatInventories", Inventory.class);
				break;
			case GPS:
				q1 = em.createNamedQuery("findGpsInventories", Inventory.class);
				break;
		}

		q1.setParameter("ownerId", branchId);
		List<Inventory> inventories = q1.getResultList();

		TypedQuery<Integer> q2 = em.createNamedQuery("findReservedInventoriesByDate", Integer.class)
				.setParameter("startTime", startTime)
				.setParameter("endTime", endTime)
				.setParameter("branchId", branchId);

		Set<Integer> reservedInventoryIds = new HashSet<Integer>(q2.getResultList());

		List<Inventory> res = new LinkedList<Inventory>();

		for (Inventory inventory : inventories) {
			if (!reservedInventoryIds.contains(inventory.getId())) {
				res.add(inventory);
			}
		}

		return res;
	}

	public void deleteInventory(int id) {
		Inventory i = findInventory(id);
		i.setStatus(InventoryStatus.DELETED);
		em.persist(i);
	}
}