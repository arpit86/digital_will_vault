package com.csus.vault.web.controllers;

import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.service.WillManagerService;

@Controller("mainController")
public class MainController {
	
	private WillManagerService willService = null;
	
	@RequestMapping(value = "/mainPage", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "mainPage";
	}
	
	@RequestMapping(value = "/viewWill", method = RequestMethod.GET)
	public ModelAndView viewUploadFile(HttpSession session, HttpServletResponse response) {
		VaultUser user = (VaultUser) session.getAttribute("user");
		willService = new WillManagerService();
		if(willService.checkWillAuthorization(user)) {
			return new ModelAndView("requestKey", "name", user.getUser_firstName() + " " + user.getUser_lastName());
		} else {
			return new ModelAndView("requestWillView");
		}
	}
	
	@RequestMapping(value = "/requestKey", method = RequestMethod.GET)
	public ModelAndView getPrivateKey(Model model, HttpServletResponse response) {
		return new ModelAndView("authorizeUserView", "authorizedUserList", new ArrayList<VaultUser>());
	}
	
	@RequestMapping(value = "/requestKey", method = RequestMethod.POST)
	public ModelAndView getPrivateKey(HttpSession session, HttpServletResponse response,
	  @RequestParam("file") MultipartFile privateKey) {
		ModelAndView mv = new ModelAndView("authorizeUserView");
		VaultUser user = (VaultUser) session.getAttribute("user");
		if (!privateKey.isEmpty()) {
			willService = new WillManagerService();
			String willData = willService.retrieveWillData(privateKey, user);
		} else {
			mv = new ModelAndView("requestKey");
		}
		return mv;
	}
	
	@RequestMapping(value = "/requestWillView", method = RequestMethod.GET)
	public ModelAndView requestOwnerToViewWill(Model model, HttpServletResponse response) {
		return new ModelAndView("authorizeUserView", "authorizedUserList", new ArrayList<VaultUser>());
	}
	
	@RequestMapping(value = "/requestWillView", method = RequestMethod.POST)
	public ModelAndView requestOwnerToViewWill(HttpSession session, HttpServletResponse response,
	  @RequestParam("file") MultipartFile file, @RequestParam("privKey") String privateKey) {
		ModelAndView mv = new ModelAndView("authorizeUserView");
		
		// Depending on the will selected send email to will owner requesting him to upload his priv key and allow authUser to view his will
		// This will be encrypted by symmetric key and this key will be encrypted by auth user public key and he has to upload his private key to view the will.
		
		return mv;
	}
	
	@RequestMapping(value = "/modifyWill", method = RequestMethod.GET)
	public ModelAndView viewAuthorizeUsers(Model model, HttpServletResponse response) {
		return new ModelAndView("authorizeUserView", "authorizedUserList", new ArrayList<VaultUser>());
	}
	
	@RequestMapping(value = "/modifyWill", method = RequestMethod.POST)
	public ModelAndView saveAuthorizedUsers(HttpSession session, HttpServletResponse response,
			 @RequestParam("file") MultipartFile privateKeyFile) {
		ModelAndView mv = new ModelAndView("mainPage");		
		willService = new WillManagerService();
		//willService.addAuthorizedWillUser(authorizedUserList);
		System.out.println("File uploaded: ");
		return mv;
	}
}
