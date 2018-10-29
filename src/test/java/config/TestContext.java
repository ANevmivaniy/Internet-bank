package config;

import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import model.CustomUserValidator;
import services.SecurityService;
import services.UserService;

@Configuration
public class TestContext {
	private static final String MESSAGE_SOURCE_BASE_NAME = "classpath:properties/validation_ru";
	
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(MESSAGE_SOURCE_BASE_NAME);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
    
    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
    
    @Bean
    public SecurityService securityService() {
        return Mockito.mock(SecurityService.class);
    }
    
    @Bean("customUserValidator")
    public CustomUserValidator validator() {
    	return Mockito.mock(CustomUserValidator.class);
    }
	
}
