package jp.co.worksap.roster.rest.modelview;

import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.OrganizationUnit;

@XmlRootElement
public class OrganizationUnitWithParent extends OrganizationUnit {
	private int parentId;

	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public OrganizationUnit toOrganizationUnit() {
		OrganizationUnit res = new OrganizationUnit();
		res.setDescription(getDescription());
		res.setName(getName());
		return res;
	}
}
