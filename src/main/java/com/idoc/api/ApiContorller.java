package com.idoc.api;

import com.idoc.model.ConstModel;
import com.idoc.pdf.PageResponseBean;
import com.idoc.pdf.PdfDao;
import com.idoc.pdf.PdfExcel;
import com.idoc.quartz.AppCache;
import com.idoc.quartz.FileYearRecoder;
import com.idoc.utils.PropertiesUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.idoc.model.ConstModel;
import com.idoc.pdf.PageResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Controller
public class ApiContorller {
	private Logger LOG = LoggerFactory.getLogger(ApiContorller.class) ;
	private static final String localJiesuanFtpFilesDirectory = PropertiesUtils.getProperty("local.jiesuan.ftp.files.directory") ; 
	
	@RequestMapping(value = "/getFileYearRecoder",  produces = "text/html;charset=UTF-8" )
	@ResponseBody
	public  List<FileYearRecoder> getFileYearRecoder() {
		 return AppCache.getFileYearRecoder() ;
	}  
	
	@RequestMapping(value = "/getLatestFileDate",  produces = "text/html;charset=UTF-8" )
	@ResponseBody
	public  Map<String , String> getLatestFileDate() {
		 Map<String, String> reslut = Maps.newHashMap() ;
		 reslut.put("latestFileDate" , AppCache.getLatestFileDate()) ;
		 return reslut ; 
	}  
	
	@RequestMapping(value = "login.do")
	@ResponseBody
	public Map<String, String> login(
			@RequestParam(value = "userName") String userName ,
			@RequestParam(value = "password") String password ,
			HttpServletRequest request) {
		LOG.info(userName) ;
		Map<String, String> result = Maps.newHashMap();
		try {
			//String reponse = OKHttpUtil.httpGet("http://id.ceair.com:7777/idmsso/ssologin.do?method=doLogin&username="+userName + "&password=" + password + "&code=&flag=0&showflag=1") ;
			//JSONObject jsonObject = JSONObject.parseObject(reponse) ;
			//String resultStr = jsonObject.getString("result") ;
			
			String resultStr = "pass";
			if("pass".equals(resultStr)){
				result.put(ConstModel.STATUS, ConstModel.YES) ;
				request.getSession().setAttribute("sessionUser", userName) ;
			}
			else{
				result.put(ConstModel.STATUS, ConstModel.NO) ;
			}
		} catch (Exception e){
			result.put(ConstModel.STATUS, ConstModel.NO) ;
		} 
		return result;
	}
	
	@RequestMapping(value="/downloadFileByName" )
	public void downloadFileByName(
			       @RequestParam("fileName") String fileName, 
			       @RequestParam("select_year") String select_year, 
			       @RequestParam("select_month") String select_month,  
			       HttpServletResponse response) throws SocketException, IOException  {
			File file = new File(localJiesuanFtpFilesDirectory+"/"+select_year+"/"+select_month+"/"+fileName) ;
			if(!file.exists()){
				String errorMessage = "Sorry. The file you are looking for does not exist";
				OutputStream outputStream = response.getOutputStream();
				outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
				outputStream.close();
				return;
			}
	
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (Strings.isNullOrEmpty(mimeType)) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
			response.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());
	}  
	
	@RequestMapping(value="/getPageResponseBean" )
	@ResponseBody
	public PageResponseBean getPageResponseBean(
		   @RequestParam("fromDate") String fromDate ,
		   @RequestParam("toDate")String toDate , 
		   @RequestParam("name")String name , 
		   @RequestParam("pageNow")int pageNow , 
		   @RequestParam("pageSize")int pageSize){
		PdfDao pdfDao = new PdfDao() ;
		return pdfDao.getPageResponseBean(fromDate, toDate, name, pageNow, pageSize) ; 
	}
	
	@RequestMapping(value="/makeResponseBeanFile" )
	@ResponseBody
	public void makeResponseBeanFile(
		   @RequestParam("fromDate") String fromDate ,
		   @RequestParam("toDate")String toDate , 
		   @RequestParam("name")String name ,
		   HttpServletResponse response) throws IOException{
		File file = null ;
		try{
			file =PdfExcel.makeResponseBeanFile(fromDate, toDate, name) ;
		}catch(Exception e){ 
			LOG.error(e.getMessage()) ;
		}
		if(file == null || (!file.exists())){
			String errorMessage = "Sorry. The file you are looking for does not exist";
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
			outputStream.close();
			return ;
		} 
		String fileName = fromDate + "_" + toDate + "_" + name  + ".xls" ;
		String mimeType = URLConnection.guessContentTypeFromName(fileName);
		if (Strings.isNullOrEmpty(mimeType)) {
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + fileName + "\""));
		response.setContentLength((int) file.length());
		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	} 
	
	
	@RequestMapping(value="/downloadFileByresponseBean" )
	@ResponseBody
	public void downloadFileByresponseBean( 
		   @RequestParam("path")String path  ,
		   HttpServletResponse response) throws IOException{
		File file = new File(path) ; 
		if(file == null || (!file.exists())){
			String errorMessage = "Sorry. The file you are looking for does not exist";
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
			outputStream.close();
			return ;
		} 
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (Strings.isNullOrEmpty(mimeType)) {
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) file.length());
		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	} 
	
}