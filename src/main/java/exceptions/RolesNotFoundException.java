package exceptions;

import org.springframework.security.core.AuthenticationException;

public class RolesNotFoundException extends AuthenticationException {
	private static final long serialVersionUID = 7686543437L;
	
	public RolesNotFoundException(String string) {
		super(string);
	}

	

}
