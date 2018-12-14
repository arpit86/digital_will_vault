package com.csus.vault.web.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
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
import com.csus.vault.web.service.PDFWatermarkService;
import com.csus.vault.web.service.PeerConnectionService;
import com.csus.vault.web.service.WillManagerService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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
		willService = new WillManagerService();
		VaultWillDetail will = willService.getWillDetailbyUserId(user);
		if(user.getUserId() == will.getVault_userId()) {
			byte[] decryptedData = willService.decryptWillDataWithSymKey(will.getWillContent(), user.getUserEmail());
			String fileContent = new String(decryptedData, "UTF-8");
			session.setAttribute("willBytes", fileContent);
			return new ModelAndView("modifyWill", "willContent", fileContent);
		} else {
			return new ModelAndView("notAuthorized");
		}
	}
	
	@RequestMapping(value = "/modifyWill", method = RequestMethod.POST)
	public ModelAndView saveUpdatedWill(HttpSession session, HttpServletResponse response, @RequestParam("updateWill") MultipartFile updateWill) {
		ModelAndView mv = new ModelAndView("mainPage");		
		VaultUser user = (VaultUser) session.getAttribute("user");
		PeerConnectionService peer = (PeerConnectionService) session.getAttribute("peer");
		if (!updateWill.isEmpty()) {
			willService = new WillManagerService();
			willService.uploadUpdatedWill(updateWill, user, peer);
			VaultWillDetail updatedWill = willService.getWillDetailbyUserId(user);
			session.setAttribute("will", updatedWill);
		} else {
			mv = new ModelAndView("error", "error","File Upload failed. Try again.");
		}
		System.out.println("File uploaded: ");
		return mv;
	}
	
	@RequestMapping(value = "/viewWill", method = RequestMethod.GET)
	public ModelAndView viewUploadFile(HttpSession session, HttpServletResponse response) {
		VaultUser user = (VaultUser) session.getAttribute("user");
		willService = new WillManagerService();
		ArrayList<String> willList = willService.getListOfWillWithViewAccess(user);
		return new ModelAndView("selectWillToView", "willList", willList);
	}
	
	@RequestMapping(value = "/viewWill", method = RequestMethod.POST)
	public ModelAndView getPrivateKey(HttpSession session, HttpServletResponse response,
	  @RequestParam("willOwnerName") String willOwnerName) {
		ModelAndView mv = new ModelAndView("viewWill");
		VaultUser user = (VaultUser) session.getAttribute("user");
		if (!willOwnerName.isEmpty()) {
			willService = new WillManagerService();
			willService.requestOwnerForWill(user, willOwnerName);
		} else {
			mv = new ModelAndView("error", "error", "The Will ID selected is invalid.");
		}
		return mv;
	}
	
	@RequestMapping(value = "/getPDFdisplay", method = RequestMethod.GET)
	public void viewPDFForOwner(HttpSession session, HttpServletResponse response)
			throws IOException, DocumentException {
		String fileName = "SystemToken/tempWill.pdf";
		String willContent = (String) session.getAttribute("willBytes");
		File file = new File(fileName);
		if(file.exists()) {
			file.delete();
		}
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(fileName));
		document.open();
		Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);		
		Paragraph para = new Paragraph(willContent, font);
		 
		document.add(para);
		document.close();
		
		Desktop desktop = Desktop.getDesktop();
        if(file.exists()) {
        	desktop.open(file);
        }
	}
	
	@RequestMapping(value = "/getPDFView", method = RequestMethod.GET)
	public void viewPDFForAuthorizedUser(HttpSession session, HttpServletResponse response) throws DocumentException, IOException {
		String fileName = "SystemToken/tempWillVoid.pdf";
		String willData = (String) session.getAttribute("willData");
		File file = new File(fileName);
		if(file.exists()) {
			file.delete();
		}
		Document document = new Document();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(fileName));
		pdfWriter.setPageEvent(new PDFWatermarkService());
		document.open();
		Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);		
		Paragraph para = new Paragraph(willData, font);
		document.add(para);
		document.close();
		
		Desktop desktop = Desktop.getDesktop();
        if(file.exists()) {
        	desktop.open(file);
        }
	}
	
	/*@RequestMapping(value = "/requestPubKey", method = RequestMethod.POST)
	public ModelAndView requestPublicKey(HttpSession session, HttpServletResponse response,
	  @RequestParam("email") String email) {
		ModelAndView mv = new ModelAndView("mainPage");
		VaultUser user = (VaultUser) session.getAttribute("user");
		if (!email.isEmpty()) {
			willService = new WillManagerService();
			willService.requestPublicKey(user.getUserEmail(), email);
		} else {
			String error = null;
			mv = new ModelAndView("error", error,"Invalid Input. Try again.");
		}
		return mv;
	}*/
	
	@RequestMapping(value = "/generateToken", method = RequestMethod.GET)
	public ModelAndView viewGenerateToken(HttpSession session, HttpServletResponse response) throws IOException {
		return new ModelAndView("generateToken");
	}
	
	@RequestMapping(value = "/generateTokenrequest", method = RequestMethod.POST)
	public ModelAndView generateTokenRequest(HttpSession session, HttpServletResponse response,
	  @RequestParam("requestorEmail") String requestorEmail,  @RequestParam("willNo") String willNo) {
		ModelAndView mv = new ModelAndView("mainPage");
		VaultUser user = (VaultUser) session.getAttribute("user");
		if (!requestorEmail.isEmpty() && !willNo.isEmpty()) {
			willService = new WillManagerService();
			willService.generateSystemToken(user.getUserEmail(), requestorEmail, willNo);
		} else {
			String error = null;
			mv = new ModelAndView("error", error,"Invalid Input. Try again.");
		}
		return mv;
	}
	
	@RequestMapping(value = "/verifyToken", method = RequestMethod.GET)
	public ModelAndView viewVerifyToken(HttpSession session, HttpServletResponse response) throws IOException {
		return new ModelAndView("verifyToken");
	}
	
	@RequestMapping(value = "/processVerifyToken", method = RequestMethod.POST)
	public ModelAndView verifyUploadedToken(HttpSession session, HttpServletResponse response,
			@RequestParam("file") MultipartFile file) {
		ModelAndView mv = new ModelAndView("renderWill");
		VaultUser user = (VaultUser) session.getAttribute("user");
		PeerConnectionService peer = (PeerConnectionService) session.getAttribute("peer");
		willService = new WillManagerService();
		String isValid = null;
		if (!file.isEmpty()) {
			isValid = willService.verifySystemToken(file, user);
		} else {
			String error = null;
			mv = new ModelAndView("error", error,"Invalid Input. Try again.");
		}
		if(isValid.contains("success")) {
			String[] resultData = isValid.split(":");
			String willData = willService.getWillDetailbyWillId(resultData[1], resultData[2], user, peer);
			mv = new ModelAndView("renderWill", "willData", willData);
			session.setAttribute("willData", willData);
		} else {
			mv = new ModelAndView("error", "error", "Error while reading the file");
		}
		return mv;
	}
}