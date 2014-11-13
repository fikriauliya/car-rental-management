package jp.co.worksap.roster.rest.modelview;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.OrganizationUnit;

@XmlRootElement
public class OrganizationHierarchy {
	private OrganizationUnit data;
	private List<OrganizationHierarchy> children = new LinkedList<OrganizationHierarchy>();

	public OrganizationUnit getData() {
		return data;
	}

	public void setData(OrganizationUnit data) {
		this.data = data;
	}

	public String getLabel() {
		return getData().getName();
	}

	public void setLabel(String label) {

	}

	public List<OrganizationHierarchy> getChildren() {
		return children;
	}

	public void setChildren(List<OrganizationHierarchy> children) {
		this.children = children;
	}
}
