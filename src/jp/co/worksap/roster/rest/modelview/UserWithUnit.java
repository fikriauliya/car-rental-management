package jp.co.worksap.roster.rest.modelview;


public class UserWithUnit {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private int unitId;
	private boolean isAttached;
	private String password;

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public boolean isAttached() {
		return isAttached;
	}
	public void setAttached(boolean isAttached) {
		this.isAttached = isAttached;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "UserWithUnit [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", unitId="
				+ unitId + ", isAttached=" + isAttached + ", password="
				+ password + "]";
	}
}
