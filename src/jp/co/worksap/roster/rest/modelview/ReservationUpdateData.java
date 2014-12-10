package jp.co.worksap.roster.rest.modelview;

public class ReservationUpdateData {
	private long groupId;
	private String operation;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}

}
