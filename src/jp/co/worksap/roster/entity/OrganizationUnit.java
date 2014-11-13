package jp.co.worksap.roster.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Entity
@Table(name="S_ORGANIZATION_UNIT")
@NamedQueries({
	@NamedQuery(name="findOrganizationUnit", query = "SELECT u from OrganizationUnit u where u.id = :id"),
})
public class OrganizationUnit {
	@Id @GeneratedValue
	private int id;

	@NotEmpty
	private String name;

	@NotEmpty
	private String description;

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

	@Override
	public String toString() {
		return getId() + " " + getName() + " " + getDescription();
	}
}
