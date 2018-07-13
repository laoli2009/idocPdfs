package com.idoc.pdf;

import com.google.common.base.Strings;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AULoader implements Loader{
	private static final Logger LOG = LoggerFactory.getLogger(AULoader.class) ;
	
	public ResponseBean load(String path,String loadCurrency) throws IOException{
		ResponseBean responseBean = null ;
		PDDocument doc = null ;
		List<PDDocument> pages = null ;
		String targetText = null ;
		try{
			doc = PDDocument.load(new File(path));
	        Splitter splitter = new Splitter() ;
	        pages = splitter.split(doc); 
	        PDFTextStripper stripper=new PDFTextStripper(); 
	        stripper.setSortByPosition(true); 
	        int pageSize = pages.size() ;
	        for(int it = pageSize-1 ; it >= 0 ; it--){
	        	 String text = stripper.getText(pages.get(it)) ;
	        	 if(text.contains("COMBINED")){
	        		 targetText = text ; 
	        		 break ; 
	        	 } 
	        }
        }catch(Exception e){
        	LOG.error(path + "    error=" + e.getMessage())  ; 
        }
		finally{
			if(pages != null && !pages.isEmpty()){
			    for(PDDocument pdDocument : pages){
		        	if(pdDocument != null){
		        		pdDocument.close(); 
		        	}
		        }
		    }
	        if(doc != null){
	            doc.close();
	        } 
		}
     
        if(targetText != null){
        	int lastTolal = -1 ;
        	String[] lines = targetText.split("\\n") ;
        	for(int i = 0 ; i < lines.length ; i++){
        		if(lines[i].contains("TOTAL")){
        			lastTolal = i ; 
        		}
        	}
        	if(lastTolal != -1){
        		String cc = null  , rm = null ; 
        		for(int i = lastTolal ; i < lines.length-1 ; i++){
        			String row = lines[i]  ;
        			if(row.contains("CC")){
        				String[] cols = row.split(" ") ;
        				for(String col : cols){
        					col = col.replace(" ", "") ; 
        					col = JsPdfsUtils.replaceWithBlank(col) ;
        					if(! Strings.isNullOrEmpty(col)){
        						cc = col ; 
        					}
        				}
        				cc = JsPdfsUtils.filterNum(cc)  ;
        			}
        			else if(row.contains("TOTAL") || (row.contains("**") && !row.contains("END"))){
        				String[] cols = row.split(" ") ;
        				for(String col : cols){
        					col = JsPdfsUtils.replaceWithBlank(col) ;
        					if(! Strings.isNullOrEmpty(col)){
        						rm = col ; 
        					}
        				}
        				rm = JsPdfsUtils.filterNum(rm)  ;
        			}
        		}
        		
        		String currency = null ;
        		String fileName = JsPdfsUtils.getFileName(path)  ;
        		List<String> cys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
        		if(cys!= null && cys.size() == 1){
        			currency = cys.get(0) ;
        		}
        		responseBean = new ResponseBean(fileName , cc , rm , currency) ;
        	}
        }
        return responseBean ; 
	}
	
	@Override
	public String descrition() {
		return "AU" ;
	}

}
