package jp.co.worksap.roster.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;

@XmlRootElement
@Entity
@Table(name = "S_USERS")
@NamedQuery(name="findAllUsers", query = "SELECT u from User u ORDER BY u.id")
public class User {
	@Id
	private String id;

	@NotNull
	private String firstName;
	@NotNull
	private String lastName;

	@Email @NotNull
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
