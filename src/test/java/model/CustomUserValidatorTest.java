package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import services.UserService;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

 public class CustomUserValidatorTest {
	private static CustomUserValidator validator = new CustomUserValidator();
	private  static CBUser user1;
	private  static CBUser user2;
	private  static CBUser user3;
	
	@BeforeAll
	static void setUp() {
		//пустой пользователь
		user1 = new CBUser();
		
		//пользователь с ошибочными данными
		user2 = new CBUser();
		user2.setUserName("mName");
		user2.setPassword("123");
		user2.setConfirmPassword("321");
		user2.setFirstName("f");
		user2.setLastName("l");
		user2.setPhoneNumber("123456");
		user2.setEmail("email");
	
		//валидный пользователь
		user3 = new CBUser();
		user3.setUserName("username");
		user3.setPassword("password1");
		user3.setConfirmPassword("password1");
		user3.setFirstName("firstName");
		user3.setLastName("lastName");
		user3.setEmail("email@mail.ru");
		user3.setPhoneNumber("123-45-67");
		
		//mock UserService объект
		UserService mockUserService = mock(UserService.class);
		when(mockUserService.loadUserByName("username")).thenReturn(user3);
		validator.setUserService(mockUserService);
	}
	
	
	@Test
	void testSupports() {
		assertTrue( validator.supports(user1.getClass()));
	}

	@Test
	void testCustomUserValidatorsMethodValidateForEmptyUserObject() {
		Errors error = new BeanPropertyBindingResult(user1, "user1");
		validator.validate(user1, error);
		System.out.println("пустой обэкт валидация сколько ошибок " + error.getErrorCount());
		assertEquals(error.getErrorCount(), 7);
	}
	
	@Test
	void testCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		System.out.println("ошибочный объект валидация сколько ошибок" + error.getErrorCount());
		assertEquals(error.getErrorCount(), 7);
	}
	
	@Test
	void testCustomUserValidatorsMethodValidateForValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user3, "user3");
		validator.validate(user3, error);
		System.out.println("валидный объект валидация сколько ошибок" + error.getErrorCount());
		//ожидаем одну ошибку валидации в связи с "наличием"  уже потльзователя с таким логином в "БД"
		assertEquals(error.getErrorCount(), 1);
	}
	
	@Test
	void testUserNameFieldByTestingCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		FieldError er = error.getFieldError("userName");
		assertEquals(er.getCode(), "Username.Registration.Conditions");
	}
	
	@Test
	void testPasswordFieldByTestingCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		FieldError er = error.getFieldError("password");
		assertEquals(er.getCode(), "Password.Registration.Conditions");
	}
	
	@Test
	void testConfirmPasswordFieldByTestingCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		FieldError er = error.getFieldError("confirmPassword");
		assertEquals(er.getCode(), "Password.Not.Match");
	}
	
	@Test
	void testFirstNameFieldByTestingCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		FieldError er = error.getFieldError("firstName");
		assertEquals(er.getCode(), "FirstName.Registration.Conditions");
	}
	
	@Test
	void testLastNameFieldByTestingCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		FieldError er = error.getFieldError("lastName");
		assertEquals(er.getCode(), "LastName.Registration.Conditions");
	}
	
	@Test
	void testEmailFieldByTestingCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		FieldError er = error.getFieldError("email");
		assertEquals(er.getCode(), "Email.Not.Match");
	}
	
	@Test
	void testPhoneNumberFieldByTestingCustomUserValidatorsMethodValidateForNotValidUserObject() {
		Errors error = new BeanPropertyBindingResult(user2, "user2");
		validator.validate(user2, error);
		FieldError er = error.getFieldError("phoneNumber");
		assertEquals(er.getCode(), "Phone.Not.Match");
	}
	

}
