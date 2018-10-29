package controllers;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import model.CBLogin;
import model.CBUser;
import services.SecurityService;
import services.UserService;

// доделать Welcome-блок

@Controller
@SessionAttributes({"login", "CBUser"})
public class CBUserController {
	private static final Logger LOGGER = LogManager.getLogger(CBUserController.class);
	private UserService userService;
	private SecurityService securityService;
	private Validator validator;
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		model.addAttribute("CBUser", new CBUser());
		return "registration";
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registration(@ModelAttribute("CBUser") CBUser user, BindingResult bindingResult, RedirectAttributes redAt) {
		
		validator.validate(user, bindingResult);
		
		if(bindingResult.hasErrors()) {
			LOGGER.info("Ошибка при регистрации нового пользователя. Причина : " 
						+ bindingResult.getAllErrors().stream()
										.map(obError -> obError.toString())
										.collect(Collectors.joining("/n ")));
			return "registration";
		}
		
		userService.register(user);
		securityService.autoLogin(user.getUserName(), user.getPassword());
		redAt.addFlashAttribute("firstname", user.getFirstName()); 

		
		return "redirect:/welcome";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("login", new CBLogin());
		return "login";
	}
	
	@RequestMapping(value = "/logining", method = RequestMethod.POST)
	public String login(@ModelAttribute("login") @Valid CBLogin login, BindingResult bindingResult,  Model model) {
		CBUser user = null;
		
		if(bindingResult.hasErrors()) {
			LOGGER.info("Ошибка при попытке входа. Причина : " 
					+ bindingResult.getAllErrors().stream()
									.map(obError -> obError.toString())
									.collect(Collectors.joining(", ")));
		
			return "login";
		}
		try {
			user = userService.validateUserForLogin(login);
			LOGGER.trace("Пользователь " + user.getUserName() + " обнаружен в базе данных");
		
		}catch(UsernameNotFoundException e) {
			model.addAttribute("error", "Некорректный логин или пароль");
			LOGGER.info("Ошибка при попытке входа. Причина : Некорректный логин или пароль");
			return "login";
		}
		
		
		securityService.autoLogin(user.getUserName(), user.getPassword());
		
		return "redirect:/main";
	}
	
	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String welcomePage(Model model) {
		return "welcome";
	}
	
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String mainPage(Model model) {
		return "main";
	}
	
	
	@Autowired
	public void setValidator(@Qualifier("customUserValidator") Validator validator) {
		this.validator = validator;
	}
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@Autowired
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
}
