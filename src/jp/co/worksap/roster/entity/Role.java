package jp.co.worksap.roster.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name="T_ROLE")
@Entity
@NamedQueries({
	@NamedQuery(name="findAllRoles", query = "SELECT u from Role u")
})
public class Role {
	@Id
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
