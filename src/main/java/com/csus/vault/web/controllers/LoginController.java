package com.csus.vault.web.controllers;

import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.servlet.ServletContext;
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

@Controller("loginController")
public class LoginController {
	
	public UserService userService;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView showLogin(Model model) {
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("user", new VaultUser());
		return mv;
	}
	
	@RequestMapping(value = "/loginProcess", method = RequestMethod.POST)
	public ModelAndView lginUser(HttpServletRequest request, HttpServletResponse response,
	  @ModelAttribute("user") VaultUser user) {
		
		ModelAndView mv = new ModelAndView("login");
		System.out.println("Login User: "+ user.getUserEmail());
		userService = new UserService();
		request.getSession().setAttribute("user",user);
		if(!userService.verify(user)) {
			VaultUser dbUser = userService.verifyUser(user.getUserEmail());
			if(userService.isPasswordValid(user.getUserPassword().toCharArray(), dbUser.getPasswordSalt().getBytes(Charset.forName("UTF-8")), 
					dbUser.getUserPassword().getBytes(Charset.forName("UTF-8")))){
				System.out.println("User is verified");
				ServletContext context = request.getSession().getServletContext();
				context.setAttribute("blockchain", new ArrayList<>());
				mv = new ModelAndView("uploadFile", "name", user.getUser_firstName() + " " + user.getUser_lastName());
			} 
		}
		return mv;
	}

}
