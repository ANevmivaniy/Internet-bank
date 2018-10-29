package controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.isA;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import config.MvcWebConfig;
import config.TestContext;
import model.CBLogin;
import model.CBUser;
import model.CustomUserValidator;
import services.SecurityService;
import services.UserService;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestContext.class, MvcWebConfig.class})
class CBUserControllerTest {
	
    private MockMvc mockMvc;

	@Autowired
    private UserService userServiceMock;
	
	@Autowired
	private SecurityService securityServiceMock;
	
	@Autowired 
	private CustomUserValidator validatorMock;
	
    @Autowired
    private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		//сбрасываем настройки и состояния в заглушках перед каждым тестом.
		reset(userServiceMock);
		reset(securityServiceMock);
		reset(validatorMock);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	
	@Test
	void test_showLoginForm_CreateLoginFormObjectAndRenderIt() throws Exception{
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(model().attribute("login", hasProperty("userName", isEmptyOrNullString())))
				.andExpect(model().attribute("login", hasProperty("password", isEmptyOrNullString())));
		
		verifyZeroInteractions(userServiceMock);
		verifyZeroInteractions(securityServiceMock);
	}
	
	@Test
	void test_ShouldRenderLoginViewAndReturnValidationErrorForEmptyLoginEntry() throws Exception{
		mockMvc.perform(post("/logining").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.sessionAttr("login", new CBLogin()))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(model().attributeHasFieldErrors("login", "userName", "password"))
				.andExpect(model().attribute("login", hasProperty("userName", isEmptyOrNullString())))
				.andExpect(model().attribute("login", hasProperty("password", isEmptyOrNullString())));
		//важно
		verifyZeroInteractions(userServiceMock);
		verifyZeroInteractions(securityServiceMock);
				
	}
	
	@Test
	void test_ShouldRenderLoginViewAndReturnValidationErrorForLoginEntryWithEmptyUsernameProperty() throws Exception{
		CBLogin mockLogin = mock(CBLogin.class);
		when(mockLogin.getPassword()).thenReturn("password");
		
		
		CBLogin login = new CBLogin();
		login.setPassword("password");
		
		mockMvc.perform(post("/logining").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.sessionAttr("login", login))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(model().attributeHasFieldErrors("login", "userName"))
				.andExpect(model().attribute("login", hasProperty("userName", isEmptyOrNullString())))
				.andExpect(model().attribute("login", hasProperty("password", notNullValue())));
				
	
		verifyZeroInteractions(userServiceMock);
		verifyZeroInteractions(securityServiceMock);
				
	}
	
	@Test
	void test_ShouldRenderLoginViewAndReturnValidationErrorForLoginEntryWithEmptyPasswordProperty() throws Exception{
		CBLogin mockLogin = mock(CBLogin.class);
		when(mockLogin.getUserName()).thenReturn("username");
		mockMvc.perform(post("/logining").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.sessionAttr("login", mockLogin))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(model().attributeHasFieldErrors("login", "password"))
				.andExpect(model().attribute("login", hasProperty("password", isEmptyOrNullString())))
				.andExpect(model().attribute("login", hasProperty("userName", notNullValue())));
				
	
		verifyZeroInteractions(userServiceMock);
		verifyZeroInteractions(securityServiceMock);
				
	}
	
	@Test
	void test_ShouldRenderLoginViewAndReturnValidationErrorForLoginEntryWithNotValidProperties() throws Exception{
		
		mockMvc.perform(post("/logining").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("userName", "usr")
				.param("password", "pas")
				.sessionAttr("login", new CBLogin()))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(model().attributeHasFieldErrors("login", "userName"))
				.andExpect(model().attributeHasFieldErrors("login", "password"))
				.andExpect(model().attribute("login", hasProperty("password", is("usr"))))
				.andExpect(model().attribute("login", hasProperty("userName", is("pas"))));
				
	
		verifyZeroInteractions(userServiceMock);
		verifyZeroInteractions(securityServiceMock);
	}
	
	@Test
	void test_ShouldRenderLoginViewAndLogin() throws Exception{
		
		CBUser user = new CBUser();
		user.setUserName("username");
		user.setPassword("password1");
		user.setConfirmPassword("password1");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("email@mail.ru");
		user.setPhoneNumber("123-45-67");
		
		when(userServiceMock.validateUserForLogin(isA(CBLogin.class))).thenReturn(user);
		doNothing().when(securityServiceMock).autoLogin(isA(user.getUserName().getClass()),
											isA(user.getPassword().getClass()));
		
		mockMvc.perform(post("/logining").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("userName", "username")
				.param("password", "password1")
				.sessionAttr("login", new CBLogin()))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/main"))
				.andExpect(redirectedUrl("/main"))
				.andExpect(model().attributeHasNoErrors("login"));
		
		verify(userServiceMock, times(1)).validateUserForLogin(isA(CBLogin.class));
		verifyNoMoreInteractions(userServiceMock);
		
		verify(securityServiceMock,times(1)).autoLogin(user.getUserName(), user.getPassword());
		verifyNoMoreInteractions(securityServiceMock);		
		
	}
	
	@Test
	void test_UserNotFound_ShouldRenderLoginViewAndAddErrorAtribute() throws Exception{
		
		CBLogin login = new CBLogin();
		login.setUserName("username1");
		login.setPassword("password2");
		
		when(userServiceMock.validateUserForLogin(isA(CBLogin.class))).thenThrow(new UsernameNotFoundException("Такого пользователя в системе нет"));
			
		mockMvc.perform(post("/logining").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("userName", login.getUserName())
				.param("password",  login.getPassword())
				.sessionAttr("login", new CBLogin()))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(model().attribute("error", notNullValue()));
		
		verify(userServiceMock, times(1)).validateUserForLogin(isA(CBLogin.class));
		verifyNoMoreInteractions(userServiceMock);
		
		verifyZeroInteractions(securityServiceMock);	
	}
	 @Test
	 void test_postLoginingTest() throws Exception {
		 
		 CBLogin login = new CBLogin();
		login.setPassword("password");
			
		 MvcResult mr = mockMvc.perform(post("/logining")
				 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
				 .sessionAttr("login", login))
		 		 .andReturn();
		 
		 
		 mr.getModelAndView().getModelMap().forEach((k,v) ->{
			 assertNotNull(v,"");
			 //System.out.println( "ключ - " + k + " значение " + v);
		 });
	 }
	 
	 @Test
	 void test_showRegistrationForm_CreateRegistrationFormObjectAndRenderIt() throws Exception{
			mockMvc.perform(get("/registration")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(status().isOk())
					.andExpect(view().name("registration"))
					.andExpect(model().attribute("CBUser", hasProperty("userName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("password", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("confirmPassword", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("firstName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("lastName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("email", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("phoneNumber", isEmptyOrNullString())));
					
			verifyZeroInteractions(userServiceMock);
			verifyZeroInteractions(securityServiceMock);
			verifyZeroInteractions(validatorMock);
		}
	 
	 	@Test
	 	void test_ShouldRenderRegistrationViewAndReturnValidationErrorForEmptyCBUserObject() throws Exception{
	 		doAnswer((Answer<Void>)invocation ->{
	 			Object[] args = invocation.getArguments();
	 			if(args[1] instanceof Errors) {
	 				Errors error = (Errors)args[1];
	 				error.rejectValue("userName", "Required");
	 				error.rejectValue("password", "Required");
	 				error.rejectValue("confirmPassword", "Required");
	 				error.rejectValue("firstName", "Required");
	 				error.rejectValue("lastName", "Required");
	 				error.rejectValue("email", "Required");
	 				error.rejectValue("phoneNumber", "Required");
	 			}
	 			return null;
	 		}).when(validatorMock).validate(isA(CBUser.class), isA(Errors.class));
	 		
	 		mockMvc.perform(post("/registration")
	 				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	 				.sessionAttr("CBUser", new CBUser()))
					.andExpect(status().isOk())
					.andExpect(view().name("registration"))
					.andExpect(model().attributeErrorCount("CBUser", 7))
					.andExpect(model().attribute("CBUser", hasProperty("userName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("password", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("confirmPassword", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("firstName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("lastName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("email", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("phoneNumber", isEmptyOrNullString())));
	 		
	 		verify(validatorMock, times(1)).validate(isA(CBUser.class), isA(Errors.class));
	 		verifyZeroInteractions(userServiceMock);
			verifyZeroInteractions(securityServiceMock);
			
	 	}
	 	
	 	@Test
	 	void test_ShouldRenderRegistrationViewAndReturnValidationErrorForNotValidUsernameFieldInCBUserObject() throws Exception{
	 		doAnswer((Answer<Void>)invocation ->{
	 			Object[] args = invocation.getArguments();
	 			if(args[1] instanceof Errors) {
	 				Errors error = (Errors)args[1];
	 				error.rejectValue("userName", "Username.Registration.Conditions");
	 			}
	 			return null;
	 		}).when(validatorMock).validate(isA(CBUser.class), isA(Errors.class));
	 		
	 		mockMvc.perform(post("/registration")
	 				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	 				.param("userName", "userName1")
	 				.sessionAttr("CBUser", new CBUser()))
					.andExpect(status().isOk())
					.andExpect(view().name("registration"))
					.andExpect(model().attributeErrorCount("CBUser", 1))
					.andExpect(model().attribute("CBUser", hasProperty("userName", is("userName1"))))
					.andExpect(model().attribute("CBUser", hasProperty("password", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("confirmPassword", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("firstName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("lastName", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("email", isEmptyOrNullString())))
					.andExpect(model().attribute("CBUser", hasProperty("phoneNumber", isEmptyOrNullString())));
	 		
	 		verify(validatorMock, times(1)).validate(isA(CBUser.class), isA(Errors.class));
	 		verifyZeroInteractions(userServiceMock);
			verifyZeroInteractions(securityServiceMock);
			
	 	}
	 	
	 	@Test
	 	void test_ShouldRegisterNewUserAndLoginAuthenticationAndRenderWelcomeView() throws Exception{
	 		CBUser user = new CBUser();
			user.setUserName("username");
			user.setPassword("password1");
			user.setConfirmPassword("password1");
			user.setFirstName("firstName");
			user.setLastName("lastName");
			user.setEmail("email@mail.ru");
			user.setPhoneNumber("123-45-67");
			
			mockMvc.perform(post("/registration")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param("userName", "username")
					.param("password", "password1")
					.param("confirmPassword", "password1")
					.param("firstName", "firstName")
					.param("lastName", "lastName")
					.param("email", "email@mail.ru")
					.param("phoneNumber", "123-45-67")	
					.sessionAttr("CBUser", new CBUser()))
					.andExpect(status().is3xxRedirection())
					.andExpect(view().name("redirect:/welcome"))
					.andExpect(redirectedUrl("/welcome"))
					.andExpect(model().hasNoErrors())
					.andExpect(flash().attributeExists("firstname"))
					.andExpect(model().attribute("CBUser", hasProperty("userName", is(user.getUserName()))))
					.andExpect(model().attribute("CBUser", hasProperty("password", is(user.getPassword()))))
					.andExpect(model().attribute("CBUser", hasProperty("confirmPassword", is(user.getConfirmPassword()))))
					.andExpect(model().attribute("CBUser", hasProperty("firstName", is(user.getFirstName()))))
					.andExpect(model().attribute("CBUser", hasProperty("lastName", is(user.getLastName()))))
					.andExpect(model().attribute("CBUser", hasProperty("email", is(user.getEmail()))))
					.andExpect(model().attribute("CBUser", hasProperty("phoneNumber", is(user.getPhoneNumber()))))
					.andExpect(flash().attribute("firstname", is(user.getFirstName())));
					
			verify(validatorMock, times(1)).validate(isA(CBUser.class), isA(Errors.class));
			verifyNoMoreInteractions(validatorMock);
			
			verify(userServiceMock, times(1)).register(isA(CBUser.class));
			verifyNoMoreInteractions(userServiceMock);
			
			verify(securityServiceMock, times(1)).autoLogin(anyString(), anyString());
			verifyNoMoreInteractions(securityServiceMock);
	 		
	 	}

}
