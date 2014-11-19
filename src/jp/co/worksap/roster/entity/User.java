package jp.co.worksap.roster.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Entity
@Table(name = "S_USERS")
@NamedQueries({
	@NamedQuery(name="findAllUsers", query = "SELECT u from User u ORDER BY u.id"),
	@NamedQuery(name="findAllUsersInUnitByIsAttachedOrderByLeaveTimestamp", query = "SELECT u from User u WHERE u.unit.id = :unitId AND u.isAttached = :isAttached ORDER BY u.leaveTimestamp"),
	@NamedQuery(name="findAllUsersInUnit", query = "SELECT u from User u WHERE u.unit.id = :unitId ORDER BY u.id"),
	@NamedQuery(name="findAllUsersInUnitByIsAttached", query = "SELECT u from User u WHERE u.unit.id = :unitId AND u.isAttached = :isAttached ORDER BY u.id"),
	@NamedQuery(name="countAllUsers", query = "SELECT COUNT(u) from User u WHERE u.unit.id = :unitId AND u.isAttached = :isAttached"),
	@NamedQuery(name="findUser", query = "SELECT u from User u where u.id = :id"),
	@NamedQuery(name="deleteUsersByUnit", query = "DELETE from User u where u.unit.id = :unitId")
})
public class User {
	@Id @NotEmpty
	private String id;

	@NotEmpty
	private String firstName;
	@NotEmpty
	private String lastName;

	@Email @NotEmpty
	private String email;

	@NotNull
	private OrganizationUnit unit;

	private boolean isAttached;

	@NotEmpty
	private String password;

	@Temporal(TemporalType.TIMESTAMP)
	private Date leaveTimestamp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public OrganizationUnit getUnit() {
		return unit;
	}

	public void setUnit(OrganizationUnit unit) {
		this.unit = unit;
	}

	public boolean isAttached() {
		return isAttached;
	}

	public void setAttached(boolean isAttached) {
		this.isAttached = isAttached;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLeaveTimestamp() {
		return leaveTimestamp;
	}

	public void setLeaveTimestamp(Date leaveTimestamp) {
		this.leaveTimestamp = leaveTimestamp;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", unit=" + unit
				+ ", isAttached=" + isAttached + ", password=" + password
				+ ", leaveTimestamp=" + leaveTimestamp + "]";
	}
}
