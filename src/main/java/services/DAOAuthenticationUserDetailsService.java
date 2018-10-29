package services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dao.UserDao;
import exceptions.RolesNotFoundException;
import model.CBUser;


@Service
@Qualifier("daoUserDetailsService")
public class DAOAuthenticationUserDetailsService implements UserDetailsService {
	
	private UserDao userDao;
	
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}


	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CBUser user = userDao.loadUserByName(username);
		GrantedAuthority authority = null;
		try {
			 authority = new SimpleGrantedAuthority(userDao.getRole(username));
		}catch(RolesNotFoundException e) {
			throw new UsernameNotFoundException("Пользователь не определён", e);
		}
		UserDetails userDetails = (UserDetails) new User(user.getUserName(), user.getPassword(), Arrays.asList(authority));
		return userDetails;
	}

}
