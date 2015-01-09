package jp.co.worksap.roster.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.UserRole;

@Named
@RequestScoped
public class UserSessionController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private transient UserEJB userEJB;

	public String logout() {
		String userId = getUserName();
		List<UserRole> roles = userEJB.findAllAssignedRoles(userId);
		FacesContext context = FacesContext.getCurrentInstance();

		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		try {
			request.logout();
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}

		try {
			if (roles.isEmpty() || (roles.size() == 1 && roles.get(0).getRoleName().equals("customer"))) {
				context.getExternalContext().redirect("/" + request.getContextPath());
			} else {
				context.getExternalContext().redirect("/" + request.getContextPath() + "/internal-index.jsf");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public boolean isUserLoggedIn() {
	    String user = this.getUserName();
	    boolean result = !((user == null)|| user.isEmpty());
	    return result;
	}

	public String getUserName() {
	    String user = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
	    return user;
	}
}
