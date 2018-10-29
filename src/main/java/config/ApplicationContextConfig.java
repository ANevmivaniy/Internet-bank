package config;

import javax.sql.DataSource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.web.filter.DelegatingFilterProxy;

import dao.UserDao;
import dao.UserDaoImpl;
import services.UserService;

@Configuration
@ComponentScan(basePackages = {"dao", "services", "model"})
//@PropertySource("jdbc.properties")
public class ApplicationContextConfig {
	
	
	
	//@Bean(name="userService")
	public UserService getUserService() {
		return new UserService();
	}
	
	
	@Bean(name="userDao")
	public UserDao getUserDao() {
		return new UserDaoImpl();
	}
	
	@Bean(name="jdbcTemplete")
	public JdbcTemplate getJdbcTemplete() {
        return new JdbcTemplate(getDataSource());
	}
	
	@Bean(name="dataSourse")
	public DataSource getDataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.postgresql.Driver.class);
        dataSource.setUsername("postgres");
        dataSource.setUrl("jdbc:postgresql://localhost/myusers");
        dataSource.setPassword("root");
        
		return dataSource;
	}
	
	 @Bean
	 public MessageSource messageSource() {
	     ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	     // Load file: validation.properties
	     messageSource.setBasename("classpath:properties/validation_ru");
	     messageSource.setDefaultEncoding("UTF-8");
	     return messageSource;
	 }
		
}
