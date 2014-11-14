package jp.co.worksap.roster.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "S_TRANSFER_LOG")
public class TransferLog {
	@Id @GeneratedValue
	private int id;

	@NotNull
	private User user;

	@NotNull
	private OrganizationUnit fromUnit;

	@NotNull
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
