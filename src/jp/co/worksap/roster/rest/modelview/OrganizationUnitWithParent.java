package jp.co.worksap.roster.rest.modelview;

import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.OrganizationUnit;

@XmlRootElement
public class OrganizationUnitWithParent {
	private int parentId;
	private String name;
	private String description;

	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
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

	public OrganizationUnit toOrganizationUnit() {
		OrganizationUnit res = new OrganizationUnit();
		res.setDescription(getDescription());
		res.setName(getName());
		return res;
	}
}
