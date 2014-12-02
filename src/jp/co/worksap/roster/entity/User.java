package jp.co.worksap.roster.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Entity
@Table(name = "T_USERS")
@NamedQueries({
	@NamedQuery(name="findAllUsers", query = "SELECT u from User u ORDER BY u.id"),
	@NamedQuery(name="findAllUsersByIdNameAndEmail", query = "SELECT u from User u WHERE (LOWER(u.id) LIKE :token) OR (LOWER(u.firstName) LIKE :token) " +
			"OR (LOWER(u.lastName) LIKE :token) OR (LOWER(u.email) LIKE :token) OR (LOWER(u.phone) LIKE :token) ORDER BY u.id"),
	@NamedQuery(name="countAllUsersByIdNameAndEmail", query = "SELECT COUNT(u) from User u WHERE (LOWER(u.id) LIKE :token) OR (LOWER(u.firstName) LIKE :token) " +
			"OR (LOWER(u.lastName) LIKE :token) OR (LOWER(u.email) LIKE :token) OR (LOWER(u.phone) LIKE :token)"),
	@NamedQuery(name="findAllUsersInUnitByIsAttachedOrderByLeaveTimestamp", query = "SELECT u from User u WHERE u.unit.id = :unitId AND u.isAttached = :isAttached ORDER BY u.leaveTimestamp"),
	@NamedQuery(name="findAllUsersInUnit", query = "SELECT u from User u WHERE u.unit.id = :unitId ORDER BY u.id"),
	@NamedQuery(name="findAllUsersInUnitByIsAttached", query = "SELECT u from User u WHERE u.unit.id = :unitId AND u.isAttached = :isAttached ORDER BY u.id"),
	@NamedQuery(name="countAllUsers", query = "SELECT COUNT(u) from User u WHERE u.unit.id = :unitId AND u.isAttached = :isAttached"),
	@NamedQuery(name="findUser", query = "SELECT u from User u where u.id = :id"),
	@NamedQuery(name="deleteUsersByUnit", query = "DELETE from User u where u.unit.id = :unitId"),
	@NamedQuery(name="deleteUser", query = "DELETE from User u where u.id = :id")
})
public class User {
	@Id @NotEmpty
	@Pattern(regexp="^[a-zA-Z0-9]*$", message="may not contain character other than letters and numbers")
	@Length(min=2, max=200)
	private String id;

	@NotEmpty
	@Length(min=1, max=200)
	private String firstName;

	@NotEmpty
	@Length(min=1, max=200)
	private String lastName;

	@Email @NotEmpty
	@Length(min=5, max=200)
	private String email;

	@NotEmpty
	@Length(min=4, max=30)
	@Pattern(regexp="^[0-9\\*\\+#]*$", message="may not contain character other than numbers and *, +, #")
	private String phone;

	private OrganizationUnit unit;

	private boolean isAttached;

	@NotEmpty
	@Length(min=5, max=200)
	private String password;

	@Temporal(TemporalType.TIMESTAMP)
	private Date leaveTimestamp;

	@ManyToMany(mappedBy = "users")
	private List<Branch> branches;

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

	@XmlTransient
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


	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@XmlTransient
	public List<Branch> getBranches() {
		return branches;
	}

	public void setBranches(List<Branch> branches) {
		this.branches = branches;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", unit=" + unit
				+ ", isAttached=" + isAttached + ", password=" + password
				+ ", leaveTimestamp=" + leaveTimestamp + "]";
	}
}
