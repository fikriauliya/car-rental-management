package jp.co.worksap.roster.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "T_ORGANIZATION_UNIT_TREE")

@NamedQueries({
	@NamedQuery(name="findOrganiaztionUnitTreeAncestors", query = "SELECT u from OrganizationUnitTree u where (u.descendant.id = :descendant_id) AND (u.isDeleted = FALSE)"),
	@NamedQuery(name="findAllOrganizationUnitTree", query = "SELECT u from OrganizationUnitTree u where (u.length = 1 or u.length = 0) and (u.isDeleted = FALSE)"),
	@NamedQuery(name="findSubTree", query = "SELECT u from OrganizationUnitTree u where (u.ancestor.id = :parentId) and (u.isDeleted = FALSE)"),
	@NamedQuery(name="markDeletedOrganizationUnitTree", query = "UPDATE OrganizationUnitTree u SET u.isDeleted = TRUE where u.descendant.id IN (SELECT w.descendant.id from OrganizationUnitTree w where w.ancestor.id = :id)")
})
public class OrganizationUnitTree {
	@Id @GeneratedValue
	private int Id;

	private OrganizationUnit ancestor;
	private OrganizationUnit descendant;
	private int length;
	private boolean isDeleted;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public OrganizationUnit getAncestor() {
		return ancestor;
	}

	public void setAncestor(OrganizationUnit ancestor) {
		this.ancestor = ancestor;
	}

	public OrganizationUnit getDescendant() {
		return descendant;
	}

	public void setDescendant(OrganizationUnit descendant) {
		this.descendant = descendant;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return getId() + " " + getAncestor() + " " + getDescendant() + " " + getLength();
	}
}
