package com.csus.vault.web.controllers;

import java.security.PrivateKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.csus.vault.web.service.EncryptDecryptService;;

@Controller
public class UploadController {
	
	public EncryptDecryptService uploadService;
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView viewUploadFile(Model model, HttpServletResponse response) {
		return new ModelAndView("main");
	}
	
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ModelAndView saveUploadFile(HttpServletRequest request, HttpServletResponse response,
	  @RequestParam("file") MultipartFile file, @RequestParam("privKey") PrivateKey privateKey) {
				
		if (!file.isEmpty()) {
			uploadService = new EncryptDecryptService();
			uploadService.upload(file, privateKey);
			System.out.println("File uploaded: " + file.getOriginalFilename());
			return new ModelAndView("main");
		} else {
			String error = "File Upload failed";
			return new ModelAndView("error");
		}
	}

}
