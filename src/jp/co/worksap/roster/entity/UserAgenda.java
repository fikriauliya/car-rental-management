package jp.co.worksap.roster.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.validator.FieldLessThan;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@Entity
@FieldLessThan(first="startTime", second="endTime", message="start time must be less than end time")
@NamedQueries({
	@NamedQuery(name="findUserAgendasByDate", query="SELECT u from UserAgenda u WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime)" +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime))" +
			"AND :userId = u.user.id"),
	@NamedQuery(name="delet	eUserAgenda", query = "DELETE from UserAgenda u where u.id = :id"),
	@NamedQuery(name="findUserAgenda", query = "SELECT u from UserAgenda u where u.id = :id"),
	@NamedQuery(name="deleteUserAgendaByUser", query = "DELETE from UserAgenda u where u.user.id = :userId OR u.assignedBy.id = :userId"),
	@NamedQuery(name="deleteUserAgendaByTitle", query = "DELETE from UserAgenda u where u.title = :title"),
	@NamedQuery(name="findReservedUsersByDate", query="SELECT DISTINCT(u.user.id) FROM UserAgenda u " +
			"LEFT OUTER JOIN u.user v " +
			"LEFT OUTER JOIN v.branches w " +
			"WHERE ((:startTime <= u.startTime AND :endTime >= u.startTime) " +
			"OR (:startTime <= u.endTime AND :endTime >= u.endTime) " +
			"OR (:startTime >= u.startTime AND :endTime <= u.endTime)) " +
			"AND (:branchId = w.id)"),
})
@Table(name = "T_USER_AGENDA")
public class UserAgenda {
	@Id @GeneratedValue
	private int id;

	@NotNull
	@OneToOne
	private User user;

	@NotNull
	private User assignedBy;

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

	private String timezone;

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

	public User getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(User assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
