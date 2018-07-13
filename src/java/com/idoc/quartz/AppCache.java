package com.idoc.quartz;

import com.idoc.pdf.PdfDao;
import com.idoc.utils.PropertiesUtils;
import com.idoc.pdf.PdfDao;

import java.util.List;

public class AppCache {
	private static final String localJiesuanFtpFilesDirectory = PropertiesUtils.getProperty("local.jiesuan.ftp.files.directory") ; 
	
	private static List<FileYearRecoder> fileYearRecoder = null ;
	private static String latestFileDate = null ;

	public static List<FileYearRecoder> getFileYearRecoder(){
		if(fileYearRecoder == null){
			fileYearRecoder = FilesUtils.getFileYearRecoder(localJiesuanFtpFilesDirectory) ;
		}
		return fileYearRecoder;
	}

	public static void setFileYearRecoder(List<FileYearRecoder> fileYearRecoder){
		AppCache.fileYearRecoder = fileYearRecoder;
	}

	public static String getLatestFileDate() {
		if(latestFileDate == null){
			PdfDao pdfDao = new PdfDao() ;
			latestFileDate = pdfDao.getLatestFileDate() ;
		}
		return latestFileDate;
	}

	public static void setLatestFileDate(String latestFileDate) {
		AppCache.latestFileDate = latestFileDate;
	}

	public static String getLocaljiesuanftpfilesdirectory() {
		return localJiesuanFtpFilesDirectory;
	} 
	
}
