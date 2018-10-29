package dao;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import exceptions.RolesNotFoundException;
import model.CBLogin;
import model.CBUser;

@Repository
public interface UserDao {
		void register(CBUser user);
		CBUser validateUserForLogin(CBLogin login);
		String getRole(String username) throws RolesNotFoundException;
		CBUser loadUserByName(String username) throws UsernameNotFoundException;
}
