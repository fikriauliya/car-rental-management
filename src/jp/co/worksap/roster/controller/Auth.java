package jp.co.worksap.roster.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

@ManagedBean
@ViewScoped
public class Auth implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
    private String password;

    public void login() throws IOException {
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
            externalContext.redirect(externalContext.getRequestContextPath() + "/customers/addonselection.jsf");
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