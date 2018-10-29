package model;

import java.beans.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CBUser {
	
	private String userName;
	private String password;
	private String confirmPassword;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String phoneNumber;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	@Override
	public String toString() {
		return "CBUser [userName=" + userName + ", password=" + password + ", confirmPassword=" + confirmPassword
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", address=" + address
				+ ", phoneNumber=" + phoneNumber + "]";
	}
	
	
	
}
