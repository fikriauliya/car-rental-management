package jp.co.worksap.roster.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Entity
@Table(name="T_ORGANIZATION_UNIT")
@NamedQueries({
	@NamedQuery(name="findOrganizationUnit", query = "SELECT u from OrganizationUnit u where u.id = :id"),
})
public class OrganizationUnit {
	@Id @GeneratedValue
	private int id;

	@NotEmpty
	@Length(min=1, max=200)
	private String name;

	@NotEmpty
	@Length(min=1, max=200)
	private String description;

	@NotNull
	private boolean isDeleted;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return getId() + " " + getName() + " " + getDescription();
	}
}
