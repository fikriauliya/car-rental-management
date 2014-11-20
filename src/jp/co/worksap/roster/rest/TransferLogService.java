package jp.co.worksap.roster.rest;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.co.worksap.roster.ejb.TransferLogEJB;
import jp.co.worksap.roster.entity.TransferLog;
import jp.co.worksap.roster.rest.modelview.TransferLogsInfo;

@Path("/logs/transfers")
@Stateless
public class TransferLogService {
	private final int SIZE = 10;

	@EJB
	TransferLogEJB transferLogEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({"admin" ,"hr"})
	public TransferLogsInfo index(@QueryParam("unitId") int unitId, @QueryParam("type") String type, @DefaultValue("0") @QueryParam("page") int page) {
		TransferLogsInfo res = new TransferLogsInfo();
		List<TransferLog> tl = new LinkedList<TransferLog>();
		long totalTransfers;

		if (type.equals("in")) {
			tl = transferLogEJB.findAllTransferIns(unitId, page, SIZE);
			totalTransfers = transferLogEJB.countAllTransferIns(unitId);
		} else {
			tl = transferLogEJB.findAllTransferOuts(unitId, page, SIZE);
			totalTransfers = transferLogEJB.countAllTransferOuts(unitId);
		}
		res.setTransferLogs(tl);
		res.setCurrentPage(page);
		res.setTotalPage((totalTransfers - 1) / SIZE + 1);
		return res;
	}
}
