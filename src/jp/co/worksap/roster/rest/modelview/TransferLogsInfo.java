package jp.co.worksap.roster.rest.modelview;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.TransferLog;

@XmlRootElement
public class TransferLogsInfo {
	private List<TransferLog> transferLogs;
	private long totalPage;
	private long currentPage;

	public List<TransferLog> getTransferLogs() {
		return transferLogs;
	}
	public void setTransferLogs(List<TransferLog> transferLogs) {
		this.transferLogs = transferLogs;
	}
	public long getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
	}
	public long getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}
}
