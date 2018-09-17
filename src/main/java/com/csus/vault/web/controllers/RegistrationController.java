package com.csus.vault.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.csus.vault.web.model.UserKey;
import com.csus.vault.web.service.UserService;

@Controller("registrationController")
public class RegistrationController {
	
	public UserService userService;
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView showRegistration(Model model) {
		ModelAndView mv = new ModelAndView("register");
		mv.addObject("user", new UserKey());
		return mv;
	}
	
	@RequestMapping(value = "/registerProcess", method = RequestMethod.POST)
	public ModelAndView addUser(HttpServletRequest request, HttpServletResponse response,
	  @ModelAttribute("user") UserKey user) {
		
		System.out.println("Register User: "+ user.getFirstName());
		userService = new UserService();
		request.getSession().setAttribute("user",user);
		userService.register(user);
		return new ModelAndView("main", "email", user.getEmail());
	}
}