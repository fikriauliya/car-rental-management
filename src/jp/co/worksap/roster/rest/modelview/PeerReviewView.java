package jp.co.worksap.roster.rest.modelview;

public class PeerReviewView {
	private String to;
	private String message;
	private int point;
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	@Override
	public String toString() {
		return "PeerReviewView [to=" + to + ", message=" + message + ", point="
				+ point + "]";
	}
}
