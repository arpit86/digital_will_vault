package com.csus.vault.web.controllers;

import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.service.UserService;

@Controller("homeController")
public class HomeController {
	
	public UserService userService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "home";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView showRegistration(Model model) {
		ModelAndView mv = new ModelAndView("register");
		mv.addObject("user", new VaultUser());
		return mv;
	}
	
	@RequestMapping(value = "/registerProcess", method = RequestMethod.POST)
	public ModelAndView addUser(HttpServletRequest request, HttpServletResponse response,
	  @ModelAttribute("user") VaultUser user) {
		
		System.out.println("Register User: "+ user.getUser_firstName() +" "+ user.getUser_lastName());
		userService = new UserService();
		request.getSession().setAttribute("user",user);
		if(userService.verify(user)) {
			userService.register(user);
			return new ModelAndView("mainPage", "name", user.getUser_firstName() + " " + user.getUser_lastName());
		} else {
			return new ModelAndView("login", "user", new VaultUser());
		}
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView showLogin(Model model) {
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("user", new VaultUser());
		return mv;
	}
	
	@RequestMapping(value = "/loginProcess", method = RequestMethod.POST)
	public ModelAndView loginUser(HttpServletRequest request, HttpServletResponse response,
	  @ModelAttribute("user") VaultUser user) {
		
		ModelAndView mv = new ModelAndView("login");
		System.out.println("Login User: "+ user.getUserEmail());
		userService = new UserService();
		request.getSession().setAttribute("user",user);
		if(!userService.verify(user)) {
			VaultUser dbUser = userService.getUserDetailByEmail(user.getUserEmail());
			if(userService.isPasswordValid(user.getUserPassword().toCharArray(), dbUser.getPasswordSalt().getBytes(Charset.forName("UTF-8")), 
					dbUser.getUserPassword().getBytes(Charset.forName("UTF-8")))){
				System.out.println("User is verified");
				mv = new ModelAndView("mainPage", "name", user.getUser_firstName() + " " + user.getUser_lastName());
			} 
		}
		return mv;
	}
}