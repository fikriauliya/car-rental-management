package jp.co.worksap.roster.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Entity
@Table(name = "S_USERS")
@NamedQueries({
	@NamedQuery(name="findAllUsers", query = "SELECT u from User u ORDER BY u.id"),
	@NamedQuery(name="countAllUsers", query = "SELECT COUNT(u) from User u")
})
public class User {
	@Id
	private String id;

	@NotEmpty
	private String firstName;
	@NotEmpty
	private String lastName;

	@Email @NotEmpty
	private String email;

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
}
