package com.idoc.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebContorller {

	@RequestMapping(value = "home")
	public String homeWeb(HttpServletRequest request) {
		if (null == request.getSession().getAttribute("sessionUser")) {
			return "redirect:/login" ;
		}
		return "home" ;
	}

	@RequestMapping(value = "upload")
	public String uploadWeb() {
		return "upload";
	}
	
	@RequestMapping(value = "operate")
	public String operateWeb() {
		return "operate";
	}
	
	@RequestMapping(value = "login")
	public String loginWeb(HttpServletRequest request) {
		request.getSession().invalidate();	//清除SESSION
		request.getSession().removeAttribute("sessionUser");
	//	清除cookie
		if(request.getCookies() != null){
			for (int i = 0; i < request.getCookies().length; i++) {
				request.getCookies()[i].setMaxAge(0);
			}
		} 
		return "login";
	}
	
	@RequestMapping(value = "ftpfiles")
	public String ftpfilesWeb() {
		return "ftpfiles";
	}

}