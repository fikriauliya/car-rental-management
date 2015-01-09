package jp.co.worksap.roster.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.UserRole;

@Named
@RequestScoped
public class Auth implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
    private String password;

    @Inject
    private transient UserEJB userEJB;

    public void login() throws IOException {
    	login(null);
	}

    public void login(String returnPath) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        try {
        	if (username == null || password == null){
        		throw new ServletException();
        	}
        	if (username.length() == 0 || password.length() == 0) {
        		throw new ServletException();
        	}
            request.login(username, password);

            List<UserRole> roles = userEJB.findAllAssignedRoles(username);

            if (roles.isEmpty() || (roles.size() == 1 && roles.get(0).getRoleName().equals("customer"))) {
            	if (returnPath == null) {
            		externalContext.redirect(externalContext.getRequestContextPath() + "/customers/check-reservation.jsf");
            	} else {
            		externalContext.redirect(externalContext.getRequestContextPath() + returnPath);
            	}
            } else {
            	externalContext.redirect(externalContext.getRequestContextPath() + "/internal-index.jsf");
            }
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Your username/password is incorrect"));
        }
    }

    public void logout() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.invalidateSession();
        externalContext.redirect(externalContext.getRequestContextPath() + "/");
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}