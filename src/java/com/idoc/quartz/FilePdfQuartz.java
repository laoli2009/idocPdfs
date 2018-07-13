package com.idoc.quartz;

import com.idoc.files.FtpUtil;
import com.idoc.files.ZipUtils;
import com.idoc.pdf.CurrencyMapping;
import com.idoc.pdf.FileDateMapping;
import com.idoc.pdf.FileNameFilter;
import com.idoc.pdf.JsPdfsUtils;
import com.idoc.pdf.Loader;
import com.idoc.pdf.LoaderMapping;
import com.idoc.pdf.PdfDao;
import com.idoc.pdf.ResponseBean;
import com.idoc.utils.PropertiesUtils;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.idoc.files.FtpUtil;
import com.idoc.files.ZipUtils;
import com.idoc.pdf.CurrencyMapping;
import com.idoc.pdf.FileDateMapping;
import com.idoc.pdf.ResponseBean;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FilePdfQuartz {
	private final Logger LOG = LoggerFactory.getLogger(FilePdfQuartz.class) ;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;
	private final String localJiesuanFtpFilesDirectory = PropertiesUtils.getProperty("local.jiesuan.ftp.files.directory") ; 
	
	public void ftpDownload() throws Exception{
		 String localFilePath = "e:/ly/jsuan"  ;
    	 FtpUtil srcFftpUtil=new FtpUtil();
	   	 srcFftpUtil.connectServer("172.28.35.25", FTPClient.DEFAULT_PORT, "bsp", "bsp_2016", null) ;  
		 List<String> srcPaths = srcFftpUtil.getFileList("/BSP") ;
		 for(String srcPath : srcPaths){
			 if(srcFftpUtil.download(srcPath , localFilePath)){
				 LOG.info("had download : " + srcPath); 
			 }  
		 }
	}
	
	public void refrechFileYearRecoderAndLatestFileDate(){
		AppCache.setFileYearRecoder(FilesUtils.getFileYearRecoder(localJiesuanFtpFilesDirectory)) ;
		PdfDao pdfDao = new PdfDao() ;
		String latestFileDate = pdfDao.getLatestFileDate() ;
		AppCache.setLatestFileDate(latestFileDate);
	}
	
	public void filesUnzip(boolean unzipOnlyLast70days) throws IOException {	    
		List<String> listname = Lists.newArrayList() ;
		String zipdir = "E:/ly/jsuan" ;  
		String unzipdir =  "E:/ly/unzipjsuan"; 
		ZipUtils.readAllFile(zipdir, listname);
		long todayToEpochDay = LocalDate.now().toEpochDay() ;
	    for(String name : listname){
	    	name = name.replace("\\", "/") ;
	    	if(unzipOnlyLast70days){
	    		long betweenDays = 0 ;
	    		try{
					String fileDateStr = name.substring(16, 20) + "-" + name.substring(21, 23) + "-01" ;
					if(fileDateStr != null){
						 LocalDate fileDate = LocalDate.parse(fileDateStr , formatter) ;
						 betweenDays = todayToEpochDay - fileDate.toEpochDay() ;
					}
	    		}catch(Exception e){
	    			LOG.error(e.getMessage()) ;
	    		}
	    		if(betweenDays >= 70){
					continue ; 
				}
			}
			if(name.endsWith(".zip")){
				ZipUtils.unzip(name, unzipdir + "/" + name.substring(zipdir.length()+1)) ;
			}
			else{
				File file = new File(name) ;
				int st = zipdir.length()+1 ;
				int ed = name.lastIndexOf("/") ;
				File dst = null ;
				if(st < ed){
				    dst = new File(unzipdir + "/" + name.substring(st , ed) + "/" + file.getName()) ;
				}
				else{
					dst = new File(unzipdir + "/" + file.getName()) ;
				}
				
				File parrntDir = new File(dst.getParent()) ;
			    if(! parrntDir.exists()){
			    	parrntDir.mkdirs() ;
				}
			    if(! dst.exists()){ 
			    	LOG.info("had copy : " + name) ;
			    	Files.copy(file, dst);
				}
			}
		}
	} 
	
	private ResponseBean calcSigle(String path , String loadCurrency) throws IOException{
		String fileName = JsPdfsUtils.getFileName(path) ;
		if(! FileNameFilter.passByFileName(fileName)){
			return null ;
		}
		Loader loader = LoaderMapping.getLoader(path) ; 
		if(loader != null){
			ResponseBean responseBean = null ;
			try{
			    responseBean = loader.load(path , loadCurrency) ;
			    if(responseBean == null){
					responseBean = new ResponseBean() ;
					responseBean.setName(fileName) ;
				}
				responseBean.setFileDate(FileDateMapping.getFileDate(path));
				responseBean.setCalcSuccess(Boolean.TRUE) ;
				responseBean.setPath(path);
			}catch(Exception e){
				responseBean = new ResponseBean() ;
				responseBean.setName(fileName) ;
				responseBean.setFileDate(FileDateMapping.getFileDate(path));
				responseBean.setCalcSuccess(Boolean.FALSE) ;
				responseBean.setPath(path);
			}
			return responseBean ; 
		}
		ResponseBean responseBean = new ResponseBean() ;
		responseBean.setFileDate(FileDateMapping.getFileDate(path));
		responseBean.setName(fileName) ;
		responseBean.setCalcSuccess(Boolean.FALSE) ;
		responseBean.setPath(path);
		return responseBean ;
	}
	
	private List<ResponseBean> calc(String path) throws Exception{
		String fileName = JsPdfsUtils.getFileName(path) ;
		if(! FileNameFilter.passByFileName(fileName)){
			return null ;
		}
		List<ResponseBean> responseBeans = Lists.newArrayList() ;
		List<String> currencys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
		if(currencys != null && !currencys.isEmpty()){ 
			for(String currency : currencys){
				responseBeans.add(calcSigle(path, currency)) ;
			}
		} 
		return responseBeans ;
	}
	
	
	public void calcPdfAndSave(boolean calcOnlyLast40days) throws Exception {
		String unzipdir = "E:/ly/unzipjsuan" ;
		List<String> listname = Lists.newArrayList() ;
		ZipUtils.readAllFile(unzipdir, listname); 
		PdfDao pdfDao = new PdfDao() ;
		long todayToEpochDay = LocalDate.now().toEpochDay() ;
	    for(String filePath : listname){
	    	String path = filePath.replace("\\" , "/") ; 
	    	String fileName = JsPdfsUtils.getFileName(path) ;
	    	//过滤方式1： 文件名称过滤
			if(! FileNameFilter.passByFileName(fileName)){
				continue ;
			}
			
			//过滤方式2： 文件日期过滤，40天，可修改
			if(calcOnlyLast40days){
				String fileDateStr = FileDateMapping.getFileDateByFileName(path) ;
				if(fileDateStr != null){
					 LocalDate fileDate = LocalDate.parse(fileDateStr , formatter) ;
					 long betweenDays = todayToEpochDay - fileDate.toEpochDay() ;
					 if(betweenDays >= 40){
						continue ; 
					 }
				}
			}
			
			//过滤方式3： 已经入库
	    	if(pdfDao.isCalcFilePath(path)){
	    		continue ;
	    	}
	    	
	    	LOG.info("to calc.." + path) ;
	    	List<ResponseBean> responseBeans = null ;
	    	try{
	    		responseBeans = calc(path) ;
	    	}catch(Exception e){
	    		LOG.error(path + "to calc error = "  + e.getMessage()) ;
	    	}
		    if(responseBeans != null && !responseBeans.isEmpty()){
		    	for(ResponseBean responseBean : responseBeans){
		    		if(responseBean == null){
		    			continue ;
		    		} 
			    	if(! pdfDao.isExistResponseBean(responseBean)){ 
			    		pdfDao.addresponseBean(responseBean) ; 
			    	}
			    	else{
			    		LOG.info("had insert " + responseBean.getName()) ;
			    	}
		    	}
		    }
	    }
	}  
	
	public void refreshIdocPdfs() throws Exception {
		FilePdfQuartz filePdfQuartz = new FilePdfQuartz()  ;
		LOG.info("step1 begin : download pdfs from ftp") ;
		filePdfQuartz.ftpDownload() ; 
		LOG.info("step1 end : download pdfs from ftp") ;
		LOG.info("step2 begin : files unzip") ;
		filePdfQuartz.filesUnzip(true) ; 
		LOG.info("step2 end : files unzip") ;
		LOG.info("step3 begin : files calculate rm and cc") ;
		filePdfQuartz.calcPdfAndSave(true);
		LOG.info("step3 end : files calculate rm and cc") ;
		LOG.info("step4 begin : refrechFileYearRecoderAndLatestFileDate") ;
		filePdfQuartz.refrechFileYearRecoderAndLatestFileDate() ; 
		LOG.info("step4 end : refrechFileYearRecoderAndLatestFileDate") ;
	}
	
	public static void main(String[] args) throws Exception {
		FilePdfQuartz filePdfQuartz = new FilePdfQuartz()  ;
		filePdfQuartz.LOG.info("step1 begin : download pdfs from ftp") ;
		filePdfQuartz.ftpDownload() ; 
		filePdfQuartz.LOG.info("step1 end : download pdfs from ftp") ;
		filePdfQuartz.LOG.info("step2 begin : files unzip") ;
		filePdfQuartz.filesUnzip(false) ;  //true:只解压最近70天
		filePdfQuartz.LOG.info("step2 end : files unzip") ;
		filePdfQuartz.LOG.info("step3 begin : files calculate rm and cc") ;
		filePdfQuartz.calcPdfAndSave(false); //true:只计算最近40天
		filePdfQuartz.LOG.info("step3 end : files calculate rm and cc") ; 
	}

}
