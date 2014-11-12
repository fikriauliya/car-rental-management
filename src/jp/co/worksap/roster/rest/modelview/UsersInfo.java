package jp.co.worksap.roster.rest.modelview;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.User;

@XmlRootElement
public class UsersInfo {
	private List<User> users;
	private long totalPage;
	private long currentPage;

	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
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