package com.csus.vault.web.controllers;

import java.io.IOException;
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
import com.csus.vault.web.model.VaultWillDetail;
import com.csus.vault.web.service.WillManagerService;

@Controller("mainController")
public class MainController {
	
	private WillManagerService willService = null;
	
	@RequestMapping(value = "/mainPage", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "mainPage";
	}
	
	@RequestMapping(value = "/modifyWill", method = RequestMethod.GET)
	public ModelAndView viewUploadedWill(HttpSession session, HttpServletResponse response) throws IOException {
		VaultUser user = (VaultUser) session.getAttribute("user");
		VaultWillDetail will = (VaultWillDetail) session.getAttribute("will");
		if(user.getUserId() == will.getVault_userId()) {
			willService = new WillManagerService();
			byte[] decryptedData = willService.decryptWillDataWithPrivateKey(will.getWillContent(),
					user.getUserEmail());
			String fileContent = new String(decryptedData, "UTF-8");
			return new ModelAndView("modifyWill", "fileContent", fileContent);
		} else {
			return new ModelAndView("notAuthorized");
		}
	}
	
	@RequestMapping(value = "/modifyWill", method = RequestMethod.POST)
	public ModelAndView saveUpdatedWill(HttpSession session, HttpServletResponse response, @RequestParam("updateWill") MultipartFile updateWill) {
		ModelAndView mv = new ModelAndView("mainPage");		
		VaultUser user = (VaultUser) session.getAttribute("user");
		if (!updateWill.isEmpty()) {
			willService = new WillManagerService();
			willService.uploadUpdatedWill(updateWill, user);
			VaultWillDetail updatedWill = willService.getWillDetailbyUserId(user);
			session.setAttribute("will", updatedWill);
		} else {
			mv = new ModelAndView("error", "error","File Upload failed. Try again.");
		}
		System.out.println("File uploaded: ");
		return mv;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/viewWill", method = RequestMethod.GET)
	public ModelAndView viewUploadFile(HttpSession session, HttpServletResponse response) {
		VaultUser user = (VaultUser) session.getAttribute("user");
		willService = new WillManagerService();
		ArrayList willList = willService.getListOfWillWithViewAccess(user);
		return new ModelAndView("selectWillToView", "willList", willList);
	}
	
	@RequestMapping(value = "/viewWill", method = RequestMethod.POST)
	public ModelAndView getPrivateKey(HttpSession session, HttpServletResponse response,
	  @RequestParam("file") MultipartFile privateKey) {
		ModelAndView mv = new ModelAndView("authorizeUserView");
		//VaultUser user = (VaultUser) session.getAttribute("user");
		if (!privateKey.isEmpty()) {
			willService = new WillManagerService();
			//String willData = willService.retrieveWillData(user);
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
		mv = new ModelAndView("notAuthorized");
		return mv;
	}
	
	
}
