package jp.co.worksap.roster.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

import jp.co.worksap.roster.entity.validator.FieldLessThan;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="T_RESERVATION")
@FieldLessThan(first="startTime", second="endTime", message="start time must be less than end time")
@NamedQueries({
	@NamedQuery(name="findReservationsById", query="SELECT u from Reservation u " +
			"WHERE (u.id = :id)"),
	@NamedQuery(name="findReservationsByCustomerId", query="SELECT u from Reservation u " +
			"WHERE (u.customer.user.id = :customerId)"),
	@NamedQuery(name="findReservationsByDate", query="SELECT u from Reservation u " +
			"WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime) " +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime)) " +
			"AND (u.inventory.owner.id = :branchId)"),
	@NamedQuery(name="findReservationsByStartDate", query="SELECT u from Reservation u " +
			"WHERE (u.startTime >= :startTime AND :startTime <= u.endTime) " +
			"AND (u.inventory.owner.id = :branchId)"),
	@NamedQuery(name="findReservedInventoriesByDate", query="SELECT DISTINCT(u.inventory.id) FROM Reservation u " +
			"WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime) " +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime)) " +
			"AND (u.inventory.owner.id = :branchId) " +
			"AND (u.status <> jp.co.worksap.roster.entity.ReservationStatus.CANCELED) " +
			"AND (u.status <> jp.co.worksap.roster.entity.ReservationStatus.FINISHED)"),
	@NamedQuery(name="findReservedInventoriesByDateWithExemption", query="SELECT DISTINCT(u.inventory.id) FROM Reservation u " +
			"WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime) " +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime)) " +
			"AND (u.inventory.owner.id = :branchId) " +
			"AND (u.status <> jp.co.worksap.roster.entity.ReservationStatus.CANCELED) " +
			"AND (u.status <> jp.co.worksap.roster.entity.ReservationStatus.FINISHED) " +
			"AND (u.groupId <> :exemptedGroupId)"),
	@NamedQuery(name="findReservationsByGroupId", query="SELECT u from Reservation u " +
			"WHERE u.groupId = :groupId"),
	@NamedQuery(name="findReservationsByInventoryId", query="SELECT u from Reservation u " +
			"WHERE u.inventory.id = :inventoryId ORDER BY u.startTime DESC")
})
public class Reservation {
	@GeneratedValue @Id
	private int id;

	@NotNull
	private long groupId;

	@NotNull
	private Customer customer;

	@NotNull
	private Inventory inventory;

	@NotNull
	private Timestamp inventoryVersion;

	@NotEmpty
	@Length(max=50)
	private String cardName;

	@NotEmpty
	@Length(max=20)
	private String cardNumber;

	@NotEmpty
	@Length(max=10)
	private String cardCIV;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date cardExpiryDate;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(name="start")
	private Date startTime;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(name="end")
	private Date endTime;

	@NotNull
	private ReservationStatus status = ReservationStatus.SCHEDULED;

	private BigDecimal driverFee;

	private User assignedDriver;

	private BigDecimal inventoryFee;


	@Temporal(TemporalType.TIMESTAMP)
	private Date returnedTime;

	private BigDecimal overdueFee = new BigDecimal(0);

	private BigDecimal penaltyFee = new BigDecimal(0);

	@NotNull
	private boolean isFullyPaid = false;

	@NotNull
	private BigDecimal paidAmount = new BigDecimal(0);

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Timestamp getInventoryVersion() {
		return inventoryVersion;
	}

	public void setInventoryVersion(Timestamp inventoryVersion) {
		this.inventoryVersion = inventoryVersion;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardCIV() {
		return cardCIV;
	}

	public void setCardCIV(String cardCIV) {
		this.cardCIV = cardCIV;
	}

	public Date getCardExpiryDate() {
		return cardExpiryDate;
	}

	public void setCardExpiryDate(Date cardExpiryDate) {
		this.cardExpiryDate = cardExpiryDate;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
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

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public User getAssignedDriver() {
		return assignedDriver;
	}

	public void setAssignedDriver(User assignedDriver) {
		this.assignedDriver = assignedDriver;
	}

	public BigDecimal getDriverFee() {
		return driverFee;
	}

	public void setDriverFee(BigDecimal driverFee) {
		this.driverFee = driverFee;
	}

	public BigDecimal getInventoryFee() {
		return inventoryFee;
	}

	public void setInventoryFee(BigDecimal inventoryFee) {
		this.inventoryFee = inventoryFee;
	}

	public Date getReturnedTime() {
		return returnedTime;
	}

	public void setReturnedTime(Date returnedTime) {
		this.returnedTime = returnedTime;
	}

	public BigDecimal getOverdueFee() {
		return overdueFee;
	}

	public void setOverdueFee(BigDecimal overdueFee) {
		this.overdueFee = overdueFee;
	}

	public boolean isFullyPaid() {
		return isFullyPaid;
	}

	public void setFullyPaid(boolean isFullyPaid) {
		this.isFullyPaid = isFullyPaid;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getPenaltyFee() {
		return penaltyFee;
	}

	public void setPenaltyFee(BigDecimal penaltyFee) {
		this.penaltyFee = penaltyFee;
	}


}
