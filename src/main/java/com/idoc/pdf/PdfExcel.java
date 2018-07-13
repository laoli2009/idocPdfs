package com.idoc.pdf;

import com.idoc.utils.PropertiesUtils;
import com.idoc.utils.PropertiesUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PdfExcel {
	
	private static final String localJiesuanMakepdfFilesDirectory = PropertiesUtils.getProperty("local.jiesuan.makepdf.files.directory") ;
	
	public static File  makeResponseBeanFile(String fromDate , String toDate , String name) throws IOException{
		PdfDao pdfDao = new PdfDao() ;
		List<ResponseBean> responseBeans = pdfDao.getResponseBeans(fromDate, toDate, name) ; 
		HSSFWorkbook workbook = new HSSFWorkbook() ;  
		HSSFSheet sheet = workbook.createSheet(name) ;
		sheet.setColumnWidth(0,20*512);    
		sheet.setColumnWidth(1,12*512);    
		sheet.setColumnWidth(2,8*512);  
		sheet.setColumnWidth(3,8*512);  
		sheet.setColumnWidth(4,8*512);  
		sheet.setColumnWidth(5,12*512);  
		sheet.setColumnWidth(6,12*512);  
		HSSFRow head = sheet.createRow(0) ;
	    head.createCell(0).setCellValue("文件名称") ;
		head.createCell(1).setCellValue("账单带期") ; 
		head.createCell(2).setCellValue("国家") ;
		head.createCell(3).setCellValue("城市") ;
		head.createCell(4).setCellValue("币种") ; 
		head.createCell(5).setCellValue("Remittance") ; 
		head.createCell(6).setCellValue("CreditCard") ;  
		
		HSSFSheet sheetFailed = workbook.createSheet("待确认") ;
		sheetFailed.setColumnWidth(0,20*512);    
		sheetFailed.setColumnWidth(1,12*512);    
		sheetFailed.setColumnWidth(2,8*512);  
		sheetFailed.setColumnWidth(3,8*512);  
		sheetFailed.setColumnWidth(4,8*512);  
		sheetFailed.setColumnWidth(5,12*512);  
		sheetFailed.setColumnWidth(6,12*512);    
		HSSFRow headFailed = sheetFailed.createRow(0) ;
		headFailed.createCell(0).setCellValue("文件名称") ;
		headFailed.createCell(1).setCellValue("账单带期") ; 
		headFailed.createCell(2).setCellValue("国家") ;
		headFailed.createCell(3).setCellValue("城市") ;
		headFailed.createCell(4).setCellValue("币种") ; 
		headFailed.createCell(5).setCellValue("Remittance") ; 
		headFailed.createCell(6).setCellValue("CreditCard") ;  
		
		int sheetRow = 1  , sheetFailedRow = 1 ;
		for (ResponseBean responseBean : responseBeans) {
			if((responseBean.getRM() == null && responseBean.getCC() == null) || !responseBean.getCalcSuccess()){
				HSSFRow field = sheetFailed.createRow(sheetFailedRow++);
				field.setHeight((short) 412);
				field.createCell(0).setCellValue(responseBean.getName()) ; 
				field.createCell(1).setCellValue(responseBean.getFileDate()) ;  
				field.createCell(2).setCellValue(responseBean.getCountry()) ; 
				field.createCell(3).setCellValue(responseBean.getCity()) ; 
				field.createCell(4).setCellValue(responseBean.getCurrency()) ; 
				field.createCell(5).setCellValue(responseBean.getRM()) ; 
				field.createCell(6).setCellValue(responseBean.getCC()) ; 
				if(!responseBean.getCalcSuccess()){
					field.createCell(7).setCellValue("失败") ;
				}
			}
			else{
				HSSFRow field = sheet.createRow(sheetRow++);
				field.setHeight((short) 412);
				field.createCell(0).setCellValue(responseBean.getName()) ; 
				field.createCell(1).setCellValue(responseBean.getFileDate()) ;  
				field.createCell(2).setCellValue(responseBean.getCountry()) ; 
				field.createCell(3).setCellValue(responseBean.getCity()) ; 
				field.createCell(4).setCellValue(responseBean.getCurrency()) ; 
				field.createCell(5).setCellValue(responseBean.getRM()) ; 
				field.createCell(6).setCellValue(responseBean.getCC()) ; 
			} 
		} 
		String fileName = fromDate + "_" + toDate + "_" + name + "_" + getUUID() + ".xls" ;
 		File xlsFile = new File(localJiesuanMakepdfFilesDirectory + "/" + fileName) ;
 		System.out.println(xlsFile.getPath());
		FileOutputStream xlsStream = new FileOutputStream(xlsFile);
		workbook.write(xlsStream);
		workbook.close();
		return xlsFile ;
	}
	
	private static String getUUID(){ 
		String uuid = UUID.randomUUID().toString(); 
		return uuid.replaceAll("-", "");
	}
	
	public static void main(String[] args) throws IOException {
		PdfDao dao = new PdfDao() ;
		//System.out.println(dao.getCount("2017-10-01", "2017-11-01" , "all")) ; 
		//System.out.println(dao.getPageResponseBean("2017-06-01", "2017-07-01" , "all" , 1 , 15)) ; 
	//	System.out.println(dao.getResponseBeans("2015-10-01", "2018-11-01" , "AU")) ; 
		File makeFile = makeResponseBeanFile("2017-10-01", "2028-11-01" , "all") ;
		System.out.println(makeFile.getPath());
		System.out.println(makeFile.getName());
	}

}
