package jp.co.worksap.roster.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.validator.FieldLessThan;

@XmlRootElement
@Entity
@FieldLessThan(first="startTime", second="endTime", message="start time must be less than end time")
@NamedQueries({
	@NamedQuery(name="findInventoryAgendasByDate", query="SELECT u from InventoryAgenda u WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime) " +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime)) " +
			"AND :inventoryId = u.inventory.id"),
	@NamedQuery(name="findReservedInventoriesByDate", query="SELECT DISTINCT(u.inventory.id) " +
			"FROM InventoryAgenda u " +
			"WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime) " +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime)) " +
			"AND (u.inventory.owner.id = :branchId)"),
	@NamedQuery(name="deleteInventoryAgenda", query = "DELETE from InventoryAgenda u where u.id = :id"),
	@NamedQuery(name="findInventoryAgenda", query = "SELECT u from InventoryAgenda u where u.id = :id"),
	@NamedQuery(name="deleteInventoryAgendaByInventory", query = "DELETE from InventoryAgenda u where u.inventory.id = :inventoryId"),
})
@Table(name = "T_INVENTORY_AGENDA")
public class InventoryAgenda {
	@Id @GeneratedValue
	private int id;

	@NotNull
	private Inventory inventory;

	@NotNull
	private Reservation reservation;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(name="start")
	private Date startTime;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(name="end")
	private Date endTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
}
