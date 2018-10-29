package config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import services.DAOAuthenticationUserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	

	private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationProvider authentikationProvider;
	
    @Autowired
    public void setAuthentikationProvider(AuthenticationProvider authentikationProvider) {
		this.authentikationProvider = authentikationProvider;
	}

	@Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void setUserDetailsService(@Qualifier("daoUserDetailsService") UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
		
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authentikationProvider);
		super.configure(auth);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/js/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/resources/**", "/").permitAll()
			.antMatchers("/main").authenticated()
			.and()
			.formLogin().loginPage("/login").permitAll()
			.and()
			.logout().logoutSuccessUrl("/login").permitAll()
			.and()
			.csrf().disable();			 	
	}
	 	@Bean
	    public PasswordEncoder passwordEncoder() {
	        PasswordEncoder encoder = new BCryptPasswordEncoder();
	        return encoder;
	    }
	 	
	 	@Bean
	    @Override
		public AuthenticationManager authenticationManagerBean() throws Exception {	
			return super.authenticationManagerBean();
		}	
}
