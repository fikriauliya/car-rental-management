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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Entity
@NamedQueries({
	@NamedQuery(name="findUserAgendasByDate", query="SELECT u from UserAgenda u WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime)" +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime))" +
			"AND :userId = u.user.id"),
	@NamedQuery(name="deleteUserAgenda", query = "DELETE from UserAgenda u where u.id = :id")
})
@Table(name = "S_USER_AGENDA")
public class UserAgenda {
	@Id @GeneratedValue
	private int id;

	@NotNull
	private User user;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(name="start")
	private Date startTime;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(name="end")
	private Date endTime;

	@NotEmpty
	@Length(max=200)
	private String title;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
