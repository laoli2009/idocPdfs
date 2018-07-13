package com.idoc.pdf;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.List;

public class FileDateMapping {
	
	public static String getFileDate(String path) {
		String fileDate = null ;
		try{
			String fileName = JsPdfsUtils.getFileName(path) ;
			if("BS".equals(fileName.substring(0, 2).toUpperCase())){
				String targetText = null ;
				try {
					PDDocument doc = PDDocument.load(new File(path));
					Splitter splitter = new Splitter() ;
					List<PDDocument> pages = splitter.split(doc); 
					PDFTextStripper stripper = new PDFTextStripper(); 
					stripper.setSortByPosition(true); 
					int pageSize = pages.size() ;
					targetText = null;
					for(int it = 0 ; it < pageSize ; it++){
						 String text = stripper.getText(pages.get(it)) ;
						 if(text.replace(" ", "").contains("PERIOD")){
							 targetText = text ; 
							 break ; 
						 }
					}
					for(PDDocument pdDocument : pages){
						if(pdDocument != null){
							pdDocument.close(); 
						}
					}
					if(doc != null){
					    doc.close();
					}
				} catch (Exception e) {
				}  
		        String targetLine = null ;
		        String[] lines = targetText.split("\\n") ;
	        	for(int i = 0 ; i < lines.length ; i++){
	        		if(lines[i].contains("PERIOD")){
	        			targetLine = lines[i] ; 
	        			break ;
	        		}
	        	}
	        	targetLine = targetLine.substring(targetLine.indexOf(":")+1 , targetLine.lastIndexOf("|")-1)  ;
	        	targetLine = targetLine.replace(" ", "") ; 
	        	targetLine = JsPdfsUtils.replaceWithBlank(targetLine) ;
	        	String[] cols = targetLine.split("/") ;
	        	fileDate = String.format(cols[2]) + "-" + String.format("%02d", Integer.valueOf(cols[1])) + "-" +String.format("%02d", Integer.valueOf(cols[0])) ;
			}
			else{
				fileDate = path.substring(21, 25) + "-" + path.substring(26, 28) + "-" + FileNameFilter.getDayByFileName(fileName) ;
			}
		}catch(Exception e){
			fileDate = null ; 
		}
		return fileDate  ; 
	}
	
	public static String getFileDateByFileName(String path) {
		String fileDate = null ;
		try{
			String fileName = JsPdfsUtils.getFileName(path) ;
			fileDate = path.substring(21, 25) + "-" + path.substring(26, 28) + "-" + FileNameFilter.getDayByFileName(fileName) ;
		}catch(Exception e){
		}
		return fileDate  ; 
	}

}
