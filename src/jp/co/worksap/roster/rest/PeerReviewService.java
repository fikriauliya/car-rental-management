package jp.co.worksap.roster.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.AccessLocalException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.xml.ws.WebServiceException;

import com.google.gson.Gson;

import jp.co.worksap.roster.ejb.PeerReviewEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.PeerReview;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.rest.modelview.PeerReviewView;

@Path("/reviews")
@Stateless
public class PeerReviewService {
	@EJB
	PeerReviewEJB peerReviewEJB;

	@EJB
	UserEJB userEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({"admin" ,"hr"})
	public List<PeerReview> index(@QueryParam("userId") String userId, @Context SecurityContext context) {
		if (userId.equals(context.getUserPrincipal().getName())) {
			throw new AccessLocalException();
		}
		List<PeerReview> res = peerReviewEJB.findAllPeerReview(userId);
		return res;
	}

	@POST
	@RolesAllowed({"admin" ,"hr", "employee"})
	public Response createPeerReview(PeerReviewView peerReview, @Context SecurityContext context) {
		String from = context.getUserPrincipal().getName();

		User fromUser = userEJB.findUser(from);
		User toUser = userEJB.findUser(peerReview.getTo());

		if (fromUser.getId().equals(toUser.getId())) {
			throw new WebServiceException("You can't review yourself");
		} else {
			PeerReview pr = new PeerReview();
			pr.setFrom(fromUser);
			pr.setMessage(peerReview.getMessage());
			pr.setPoint(peerReview.getPoint());
			pr.setTo(toUser);
			pr.setTimestamp(new Date());
			peerReviewEJB.createPeerReview(pr);

			return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
		}
	}
}
