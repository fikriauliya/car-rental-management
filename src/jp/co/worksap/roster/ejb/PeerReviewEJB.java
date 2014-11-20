package jp.co.worksap.roster.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

import jp.co.worksap.roster.controller.UserSessionController;
import jp.co.worksap.roster.entity.PeerReview;
import jp.co.worksap.roster.entity.User;

@Stateless
public class PeerReviewEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public List<PeerReview> findAllPeerReview(String userId) {
		TypedQuery<PeerReview> res = em.createNamedQuery("findAllPeerReviews", PeerReview.class)
				.setParameter("userId", userId);
		return res.getResultList();
	}

	public void createPeerReview(PeerReview peerReview) {
		em.persist(peerReview);
	}

	public int deletePeerReviewByUser(String userId) {
		TypedQuery<PeerReview> q = em.createNamedQuery("deletePeerReviewByUser", PeerReview.class)
				.setParameter("userId", userId);
		return q.executeUpdate();
	}
}
