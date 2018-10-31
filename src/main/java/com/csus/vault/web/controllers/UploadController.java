package com.csus.vault.web.controllers;

import java.util.ArrayList;

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
import com.csus.vault.web.model.VaultWillDetail;
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
	  @RequestParam("file") MultipartFile file, @RequestParam("privKey") String privateKey) {
		ModelAndView mv = new ModelAndView("authorizeUserView");
		VaultUser user = (VaultUser) session.getAttribute("user");
		if (!file.isEmpty()) {
			willService = new WillManagerService();
			willService.upload(file, user);
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
		return new ModelAndView("authorizeUserView", "authorizedUserList", new ArrayList<VaultUser>());
	}
	
	@RequestMapping(value = "/authorizeUserView", method = RequestMethod.POST)
	public ModelAndView saveAuthorizedUsers(HttpSession session, HttpServletResponse response,
	  @RequestParam("authorizedUserList") ArrayList<VaultUser> authorizedUserList) {
		ModelAndView mv = new ModelAndView("mainPage");
		VaultWillDetail will = (VaultWillDetail) session.getAttribute("will");
		willService = new WillManagerService();
		willService.addAuthorizedWillUser(authorizedUserList, will);
		return mv;
	}

}
