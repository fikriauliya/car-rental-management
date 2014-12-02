package jp.co.worksap.roster.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jp.co.worksap.roster.ejb.CustomerEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.Customer;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.rest.modelview.CustomerInfo;

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
	public Response create(CustomerInfo c) {
		User u = new User();
		u.setAttached(false);
		u.setEmail(c.getEmail());
		u.setFirstName(c.getFirstName());
		u.setLastName(c.getLastName());
		if (c.getPassword() != null)
			u.setPassword(DigestUtils.md5Hex(c.getPassword()));
		u.setId(c.getId());
		u.setPhone(c.getPhone());

		userEJB.createUser(u);

		Customer cust = new Customer();
		cust.setAddress(c.getAddress());
		cust.setBirthDate(c.getBirthDate());
		cust.setCountry(c.getCountry());
		cust.setPostalCode(c.getPostalCode());
		cust.setUser(u);

		customerEJB.createCustomer(cust);
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}
}