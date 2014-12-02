package jp.co.worksap.roster.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.reflect.FieldUtils;

import sun.reflect.misc.FieldUtil;

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

	public List<Inventory> findInventories(Inventory.InventoryType type, int branchId) {
		TypedQuery<Inventory> res = null;
		switch (type) {
			case CAR:
				res = em.createNamedQuery("findCarInventories", Inventory.class);
				break;
			case BABY_SEAT:
				res = em.createNamedQuery("findBabySeatInventories", Inventory.class);
				break;
			case GPS:
				res = em.createNamedQuery("findGpsInventories", Inventory.class);
				break;
		}
		res.setParameter("ownerId", branchId);
		return res.getResultList();
	}

	public void deleteInventory(int id) {
		Inventory i = findInventory(id);
		i.setStatus(InventoryStatus.DELETED);
		em.persist(i);
	}
}
