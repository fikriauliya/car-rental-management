package jp.co.worksap.roster.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import jp.co.worksap.roster.entity.TransferLog;

@Stateless
public class TransferLogEJB {
	@PersistenceContext(unitName="RosterManagement")
	private EntityManager em;

	public List<TransferLog> findAllTransferIns(int unitId, int page, int size) {
		TypedQuery<TransferLog> q = em.createNamedQuery("findAllTransferIns", TransferLog.class)
				.setParameter("unitId", unitId)
				.setFirstResult(page * size)
				.setMaxResults(size);
		return q.getResultList();
	}

	public List<TransferLog> findAllTransferOuts(int unitId, int page, int size) {
		TypedQuery<TransferLog> q = em.createNamedQuery("findAllTransferOuts", TransferLog.class)
				.setParameter("unitId", unitId)
				.setFirstResult(page * size)
				.setMaxResults(size);
		return q.getResultList();
	}

	public long countAllTransferIns(int unitId) {
		TypedQuery<Long> q = em.createNamedQuery("countAllTransferIns", Long.class)
				.setParameter("unitId", unitId);
		return q.getSingleResult();
	}

	public long countAllTransferOuts(int unitId) {
		TypedQuery<Long> q = em.createNamedQuery("countAllTransferOuts", Long.class)
				.setParameter("unitId", unitId);
		return q.getSingleResult();
	}

	public int deleteTransferLogs(int unitId) {
		TypedQuery<TransferLog> q = em.createNamedQuery("deleteTransferLogs", TransferLog.class)
				.setParameter("unitId", unitId);
		return q.executeUpdate();
	}

	public int deleteTransferLogsByUser(String userId) {
		TypedQuery<TransferLog> q = em.createNamedQuery("deleteTransferLogsByUser", TransferLog.class)
				.setParameter("userId", userId);
		return q.executeUpdate();
	}
}
