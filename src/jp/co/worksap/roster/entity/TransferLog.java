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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "T_TRANSFER_LOG")
@NamedQueries({
	@NamedQuery(name="findAllTransferIns", query = "SELECT u from TransferLog u WHERE u.toUnit.id = :unitId ORDER BY u.timestamp DESC"),
	@NamedQuery(name="findAllTransferOuts", query = "SELECT u from TransferLog u WHERE u.fromUnit.id = :unitId ORDER BY u.timestamp DESC"),
	@NamedQuery(name="countAllTransferIns", query = "SELECT COUNT(u) from TransferLog u WHERE u.toUnit.id = :unitId"),
	@NamedQuery(name="countAllTransferOuts", query = "SELECT COUNT(u) from TransferLog u WHERE u.fromUnit.id = :unitId"),
	@NamedQuery(name="deleteTransferLogs", query = "DELETE from TransferLog u where u.fromUnit.id = :unitId OR u.toUnit.id = :unitId"),
	@NamedQuery(name="deleteTransferLogsByUser", query = "DELETE from TransferLog u where u.user.id = :userId")
})
public class TransferLog {
	@Id @GeneratedValue
	private int id;

	@NotNull
	private User user;

	private OrganizationUnit fromUnit;

	private OrganizationUnit toUnit;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OrganizationUnit getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(OrganizationUnit fromUnit) {
		this.fromUnit = fromUnit;
	}

	public OrganizationUnit getToUnit() {
		return toUnit;
	}

	public void setToUnit(OrganizationUnit toUnit) {
		this.toUnit = toUnit;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
