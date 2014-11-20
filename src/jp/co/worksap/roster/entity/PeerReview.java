package jp.co.worksap.roster.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

@XmlRootElement
@Table(name="S_PEER_REVIEW")
@Entity
@NamedQueries({
	@NamedQuery(name="findAllPeerReviews", query="SELECT u FROM PeerReview u WHERE u.to.id = :userId")
})
public class PeerReview {
	@Id @GeneratedValue
	private int id;

	@NotNull
	private User from;

	@NotNull
	private User to;

	@NotNull
	@Range(min=0, max=10)
	private int point;

	@NotEmpty
	@Length(min=100, max=2000)
	private String message;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "PeerReview [id=" + id + ", from=" + from + ", to=" + to
				+ ", point=" + point + ", message=" + message + "]";
	}
}
