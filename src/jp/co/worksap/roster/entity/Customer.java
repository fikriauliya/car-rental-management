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

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="T_CUSTOMER")
@NamedQueries({
	@NamedQuery(name="findCustomer", query="SELECT u FROM Customer u WHERE u.id = :id"),
	@NamedQuery(name="findCustomerByUserId", query="SELECT u FROM Customer u WHERE u.user.id = :userId"),
})
public class Customer {
	@Id @GeneratedValue
	private int id;

	@NotNull
	private User user;

	@Length(max=100)
	private String address;

	@NotEmpty
	@Length(max=20)
	private String postalCode;

	@NotEmpty
	@Length(max=50)
	private String country;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date birthDate;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}


}
