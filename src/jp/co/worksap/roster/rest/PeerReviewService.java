package jp.co.worksap.roster.rest;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import jp.co.worksap.roster.controller.UserSessionController;
import jp.co.worksap.roster.ejb.PeerReviewEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.PeerReview;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.rest.modelview.PeerReviewView;

@Path("/reviews")
@Stateless
public class PeerReviewService {
	private final int SIZE = 10;

	@EJB
	PeerReviewEJB peerReviewEJB;

	@EJB
	UserEJB userEJB;

	@EJB
	private UserSessionController session;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PeerReview> index(@QueryParam("userId") String userId) {
		List<PeerReview> res = peerReviewEJB.findAllPeerReview(userId);
		return res;
	}

	@POST
	public void createPeerReview(PeerReviewView peerReview, @Context SecurityContext context) {
		String from = context.getUserPrincipal().getName();

		User fromUser = userEJB.findUser(from);
		User toUser = userEJB.findUser(peerReview.getTo());

		PeerReview pr = new PeerReview();
		pr.setFrom(fromUser);
		pr.setMessage(peerReview.getMessage());
		pr.setPoint(peerReview.getPoint());
		pr.setTo(toUser);
		pr.setTimestamp(new Date());
		peerReviewEJB.createPeerReview(pr);
	}
}
