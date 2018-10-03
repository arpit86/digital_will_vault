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

import com.csus.vault.web.model.DigitalWillBlock;
import com.csus.vault.web.model.UserKey;
import com.csus.vault.web.service.EncryptDecryptService;;

@Controller
public class UploadController {
	
	public EncryptDecryptService uploadService;
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView viewUploadFile(Model model, HttpServletResponse response) {
		return new ModelAndView("uploadFile");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ModelAndView saveUploadFile(HttpSession session, HttpServletResponse response,
	  @RequestParam("file") MultipartFile file, @RequestParam("privKey") String privateKey) {
				
		if (!file.isEmpty()) {
			uploadService = new EncryptDecryptService();
			//uploadService.upload(file, privateKey);
			UserKey user = (UserKey) session.getAttribute("user");
			
			ArrayList<DigitalWillBlock> blockchain = (ArrayList<DigitalWillBlock>) session.getServletContext().getAttribute("blockchain");
			uploadService.upload(file, user, blockchain);
			System.out.println("File uploaded: " + file.getOriginalFilename());
			/*PeerClient peer = new PeerClient(user.getEmail(), blockchain.get(blockchain.size()-1));
			peer.run();*/
			return new ModelAndView("uploadFile");
		} else {
			String error = null;
			return new ModelAndView("error", error,"File Upload failed. Try again.");
		}
	}

}
