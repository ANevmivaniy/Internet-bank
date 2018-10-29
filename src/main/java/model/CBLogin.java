package model;



import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CBLogin {
	@NotNull(message = "Введите имя Пользователя")
	@Size(min=4, max = 25, message = "Пользовательское имя должно быть больше 3 знаков")
	private String userName;
	
	@NotNull(message = "Введите пароль")
	@Size(min=4, max = 25, message = "Пароль должен быть больше 3 знаков")
	private String password;
	
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

	@Override
	public String toString() {
		return "CBLogin [userName=" + userName + ", password=" + password + "]";
	}
	 
	 
}
