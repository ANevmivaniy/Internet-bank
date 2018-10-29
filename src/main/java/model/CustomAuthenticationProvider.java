package model;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import dao.UserDaoImpl;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(CustomAuthenticationProvider.class);
	
	private UserDetailsService userService;
	 
	@Autowired
	public void setUserService(UserDetailsService userService) {
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        
         User user = (User)userService.loadUserByUsername(username);
         if (user == null || !user.getUsername().equalsIgnoreCase(username)) {
             throw new BadCredentialsException("Username not found.");
         }
  
         if (!password.equals(user.getPassword())) {
             throw new BadCredentialsException("Wrong password.");
         }
  
         Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
  
         return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
