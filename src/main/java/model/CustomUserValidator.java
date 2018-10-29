 package model;

import java.util.stream.Collectors;

import org.postgresql.shaded.com.ongres.scram.common.message.ServerFinalMessage.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import services.UserService;

@Component
public class CustomUserValidator implements Validator{
	
	private UserService userService;
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return CBUser.class.equals(arg0);
	}

	@Override
	public void validate(Object arg0, Errors errors) {
		
		CBUser user = (CBUser) arg0;
		System.out.println("user - " + user + "/n" );
		////////////////////////////////Check the username for errors///////////////////////////////////////////
		ValidationUtils.rejectIfEmpty(errors, "userName", "Required");
		if(user.getUserName() != null && (user.getUserName().length() < 6 || user.getUserName().length() > 38)) {
			errors.rejectValue("userName", "Username.Registration.Conditions" );
		}
		
		if(user.getUserName() != null && userService.loadUserByName(user.getUserName()) != null){
			errors.rejectValue("userName", "Username.Duplicate");
		}
		
		////////////////////////////////Check the password for errors///////////////////////////////////////////
		ValidationUtils.rejectIfEmpty(errors, "password", "Required");
		if(user.getPassword() != null &&(user.getPassword().length() < 8 || user.getPassword().length() > 38)) {
			errors.rejectValue("password", "Password.Registration.Conditions" );
		}
		ValidationUtils.rejectIfEmpty(errors, "confirmPassword", "Required");
		if(user.getConfirmPassword()!= null && (!user.getConfirmPassword().equals(user.getPassword()))){
			errors.rejectValue("confirmPassword", "Password.Not.Match");
		}
		/////////////////////////////////Check the firstname for errors//////////////////////////////////////////
		ValidationUtils.rejectIfEmpty(errors, "firstName", "Required");
		if(user.getFirstName() != null && (user.getFirstName().length() < 2 || user.getFirstName().length() > 38)) {
			errors.rejectValue("firstName", "FirstName.Registration.Conditions" );
		}
		/////////////////////////////////Check the lastname for errors//////////////////////////////////////////
		ValidationUtils.rejectIfEmpty(errors, "lastName", "Required");
		if(user.getLastName() != null && (user.getLastName().length() < 2 || user.getLastName().length() > 38)) {
			errors.rejectValue("lastName", "LastName.Registration.Conditions" );
		}
		/////////////////////////////////Check email for errors//////////////////////////////////////////
		ValidationUtils.rejectIfEmpty(errors, "email", "Required");
		if(user.getEmail()!=null && (!user.getEmail().matches("\\A[^@]+@([^@\\.]+\\.)+[^@\\.]+\\z"))) {
			errors.rejectValue("email", "Email.Not.Match" );
		}
		/////////////////////////////////Check phone number for errors//////////////////////////////////////////
		ValidationUtils.rejectIfEmpty(errors, "phoneNumber", "Required");
		if(user.getPhoneNumber()!= null && (!user.getPhoneNumber().matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$"))) {
			errors.rejectValue("phoneNumber", "Phone.Not.Match" );
		}
		
		
	}

}
