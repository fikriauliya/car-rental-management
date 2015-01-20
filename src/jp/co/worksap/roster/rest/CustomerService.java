package jp.co.worksap.roster.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.WebServiceException;

import jp.co.worksap.roster.ejb.CustomerEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.Customer;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.rest.modelview.CustomerInfo;
import jp.co.worksap.roster.utilities.EmailServices;

import org.apache.commons.codec.digest.DigestUtils;

@Path("/customers")
@Stateless
public class CustomerService {
	@EJB
	private CustomerEJB customerEJB;

	@EJB
	private UserEJB userEJB;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User create(CustomerInfo c, @Context HttpServletRequest request) {
		User u = new User();
		u.setAttached(false);
		u.setEmail(c.getEmail());
		u.setFirstName(c.getFirstName());
		u.setLastName(c.getLastName());
		if (c.getPassword() != null)
			u.setPassword(DigestUtils.md5Hex(c.getPassword()));
		u.setId(c.getId());
		u.setPhone(c.getPhone());

		userEJB.createUser(u, false);

		Customer cust = new Customer();
		cust.setAddress(c.getAddress());
		cust.setBirthDate(c.getBirthDate());
		cust.setCountry(c.getCountry());
		cust.setPostalCode(c.getPostalCode());
		cust.setUser(u);

		customerEJB.createCustomer(cust);
		try {
			if (request.getUserPrincipal() != null) {

			} else {
				request.login(c.getId(), c.getPassword());
			}
		} catch (ServletException e) {
			throw new WebServiceException("Can't login");
		}

		EmailServices.sendEmail(u.getEmail(), "Your log in information",
				"<h2>Your log in credential</h2>" +
				"<p>Thank you for your registering with us. </p>" +
				"<p>This is your login credential information. Please keep this information secured.</p>" +
				"<p>Username: " + c.getId() + "<br/>" +
				"Password: " + c.getPassword() + "</p>");

		return u;
	}
}