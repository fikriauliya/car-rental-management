package jp.co.worksap.roster.entity;

import java.sql.Time;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Table(name="T_BRANCH")
@Entity
@NamedQueries({
	@NamedQuery(name="findBranches", query="SELECT u FROM Branch u ORDER BY u.name"),
	@NamedQuery(name="findBranch", query="SELECT u FROM Branch u WHERE u.id = :id"),
	@NamedQuery(name="deleteBranch", query="DELETE FROM Branch u WHERE u.id = :id"),
})
public class Branch {
	@Id @GeneratedValue
	private int id;

	@NotEmpty
	@Length(min=4, max=100)
	private String name;

	@NotEmpty
	@Length(min=4, max=100)
	private String address;

	private Float latitude;

	private Float longitude;

	@NotEmpty
	private String timezone;

	@NotEmpty
	private String openingHour;

	@NotEmpty
	private String closingHour;

	@ManyToMany
	@JoinTable(name = "T_BRANCH_USER")
	private List<User> users;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getOpeningHour() {
		return openingHour;
	}

	public void setOpeningHour(String openingHour) {
		this.openingHour = openingHour;
	}

	public String getClosingHour() {
		return closingHour;
	}

	public void setClosingHour(String closingHour) {
		this.closingHour = closingHour;
	}

}
