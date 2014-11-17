package jp.co.worksap.roster.rest.modelview;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.Role;
import jp.co.worksap.roster.entity.UserRole;

@XmlRootElement
public class UserDetail {
	private List<String> assignedRoles;
	private List<String> availableRoles;

	public List<String> getAssignedRoles() {
		return assignedRoles;
	}
	public void setAssignedRoles(List<String> assignedRoles) {
		this.assignedRoles = assignedRoles;
	}
	public List<String> getAvailableRoles() {
		return availableRoles;
	}
	public void setAvailableRoles(List<String> availableRoles) {
		this.availableRoles = availableRoles;
	}
}
