package services;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import dao.UserDaoImpl;

@Service
public class SecurityServiceImpl implements SecurityService {
	private final static Logger logger = LogManager.getLogger(UserDaoImpl.class);

	private AuthenticationManager authenticationManager;
	private UserDetailsService userDetailsService;
	
	@Autowired
	public void setUserDetailsService(@Qualifier("daoUserDetailsService")UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public String findLoggedInUsername() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
		if(userDetails instanceof UserDetails) {
			return ((UserDetails)userDetails).getUsername();
		}
		return null;
	}

	@Override
	public void autoLogin(String username, String password) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken auth = 
				new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
		
		logger.info(userDetails.getUsername() + " " + userDetails.getPassword() + " " 
				+ userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" ")) 
				+ " в  секьюрити сервисе");
		
		authenticationManager.authenticate(auth);
		if(auth.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(auth);
 
		}else
			logger.error("Проблема с аутенфикацией пользователя");
	}

}
