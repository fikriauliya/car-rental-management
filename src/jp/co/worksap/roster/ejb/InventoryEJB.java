package jp.co.worksap.roster.ejb;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.sun.faces.facelets.tag.jsf.core.SetPropertyActionListenerHandler;

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

	@EJB
	BranchEJB branchEJB;

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
		b.setLatitude(inventory.getLatitude());
		b.setLongitude(inventory.getLongitude());

		if (inventory instanceof CarInventory) {
			((CarInventory) b).copyPropertiesFrom((CarInventory)inventory);
		} else if (inventory instanceof BabySeatInventory) {
			((BabySeatInventory) b).copyPropertiesFrom((BabySeatInventory)inventory);
		} else if (inventory instanceof GpsInventory) {
			((GpsInventory) b).copyPropertiesFrom((GpsInventory)inventory);
		}
		em.persist(b);
	}

	public void updatePrimaryImage(int id, int newImageId) {
		Inventory b = findInventory(id);
		b.setPrimaryImageId(newImageId);
		em.persist(b);
	}

	public Inventory findInventory(int id) {
		TypedQuery<Inventory> res = em.createNamedQuery("findInventory", Inventory.class)
				.setParameter("id", id);
		return res.getSingleResult();
	}

	public List<Inventory> findInventories(Inventory.InventoryType type, int branchId, Date startTime, Date endTime, int exemptedReservationGroupId) {
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

		Set<Integer> reservedInventoryIds = findReservedInventories(branchId, startTime, endTime, exemptedReservationGroupId);

		List<Inventory> res = new LinkedList<Inventory>();
		for (Inventory inventory : inventories) {
			if (!reservedInventoryIds.contains(inventory.getId())) {
				res.add(inventory);
			}
		}

		return res;
	}

	public Set<Integer> findReservedInventories(int branchId, Date startTime, Date endTime, long exemptedGroupId) {
		Branch branch = branchEJB.findBranch(branchId);
		TypedQuery<Integer> q2;

		if (exemptedGroupId == -1) {
			q2 = em.createNamedQuery("findReservedInventoriesByDate", Integer.class)
					.setParameter("startTime", timestampToDate(startTime.getTime() - branch.getBufferHour() * 60 * 60 * 1000))
					.setParameter("endTime", timestampToDate(endTime.getTime() + branch.getBufferHour() * 60 * 60 * 1000))
					.setParameter("branchId", branchId);
		} else {
			q2 = em.createNamedQuery("findReservedInventoriesByDateWithExemption", Integer.class)
					.setParameter("startTime", timestampToDate(startTime.getTime() - branch.getBufferHour() * 60 * 60 * 1000))
					.setParameter("endTime", timestampToDate(endTime.getTime() + branch.getBufferHour() * 60 * 60 * 1000))
					.setParameter("branchId", branchId)
					.setParameter("exemptedGroupId", exemptedGroupId);
		}

		Set<Integer> reservedInventoryIds = new HashSet<Integer>(q2.getResultList());
		return reservedInventoryIds;
	}

	private Date timestampToDate(long timestamp) {
		return new Date(new Timestamp(timestamp).getTime());
	}

	public void deleteInventory(int id) {
		Inventory i = findInventory(id);
		i.setStatus(InventoryStatus.DELETED);
		em.persist(i);
	}

	public void updateStatus(Inventory inventory, InventoryStatus status) {
		inventory.setStatus(status);
		em.persist(inventory);
	}
}
