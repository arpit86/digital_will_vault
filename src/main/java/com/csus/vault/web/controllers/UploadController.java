package com.csus.vault.web.controllers;

import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.csus.vault.web.model.VaultUser;
import com.csus.vault.web.model.VaultWillDetail;
import com.csus.vault.web.service.PeerConnectionService;
import com.csus.vault.web.service.WillManagerService;;

@Controller
public class UploadController {
	
	private WillManagerService willService = null;
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView viewUploadFile(Model model, HttpServletResponse response) {
		return new ModelAndView("uploadFile");
	}
	
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ModelAndView saveUploadFile(HttpSession session, HttpServletResponse response,
	  @RequestParam("file") MultipartFile file) {
		ModelAndView mv = new ModelAndView("authorizeUserView");
		VaultUser user = (VaultUser) session.getAttribute("user");
		PeerConnectionService peer = (PeerConnectionService) session.getAttribute("peer");
		if (!file.isEmpty()) {
			willService = new WillManagerService();
			willService.generateSecretKey(user.getUserEmail());
			willService.upload(file, user, peer);
			VaultWillDetail will = willService.getWillDetailbyUserId(user);
			session.setAttribute("will", will);
		} else {
			String error = null;
			mv = new ModelAndView("error", error,"File Upload failed. Try again.");
		}
		return mv;
	}
	
	@RequestMapping(value = "/authorizeUserView", method = RequestMethod.GET)
	public ModelAndView viewAuthorizeUsers(Model model, HttpServletResponse response) {
		return new ModelAndView("authorizeUserView");
	}
	
	@RequestMapping(value = "/authorizeUserProcess", method = RequestMethod.POST)
	public ModelAndView saveAuthorizedUsers(HttpSession session, HttpServletResponse response,
			@RequestParam("user_lastName") String user_lastName, @RequestParam("user_firstName") String user_firstName,
			@RequestParam("userEmail") String userEmail) throws SQLException {
		ModelAndView mv = new ModelAndView("authorizeUserView");
		VaultUser authorizeUser = new VaultUser();
		authorizeUser.setUser_firstName(user_firstName);
		authorizeUser.setUser_lastName(user_lastName);
		authorizeUser.setUserEmail(userEmail);
		VaultWillDetail will = (VaultWillDetail) session.getAttribute("will");
		willService = new WillManagerService();
		willService.addAuthorizedWillUser(authorizeUser, will);
		return mv;
	}

}
