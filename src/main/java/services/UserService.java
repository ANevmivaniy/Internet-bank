package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dao.UserDao;
import dao.UserDaoImpl;
import exceptions.RolesNotFoundException;
import model.CBLogin;
import model.CBUser;


@Service
public class UserService {
	
	private UserDao userDao;
	
	public UserService() {
		System.out.println("------------------UserService()--------------------");
	}	
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public String getRole(String role) throws RolesNotFoundException{
		return userDao.getRole(role);
	}
	
	public void register(CBUser user) {
		userDao.register(user);	
	}

	public CBUser validateUserForLogin(CBLogin login) throws UsernameNotFoundException {
		return userDao.validateUserForLogin(login);
	}
	
	public CBUser loadUserByName(String username) throws UsernameNotFoundException{
		return userDao.loadUserByName(username);
	}

}
