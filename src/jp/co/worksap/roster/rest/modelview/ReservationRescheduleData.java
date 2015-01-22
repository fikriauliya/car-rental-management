package jp.co.worksap.roster.rest.modelview;

import java.util.Date;

public class ReservationRescheduleData {
	private long groupId;
	private Date startTime;
	private Date endTime;

	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
