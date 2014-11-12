package jp.co.worksap.roster.controller;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Stateless
@Named
public class UserSessionController implements Serializable {
	private static final long serialVersionUID = 1L;

	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		try {
			request.logout();
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}

		return "/index.jsf";
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
