package com.idoc.pdf;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class THLoader implements Loader{
	private static final Logger LOG = LoggerFactory.getLogger(THLoader.class) ;
	
	public ResponseBean load(String path,String loadCurrency) throws IOException{
		ResponseBean responseBean = null ;
		PDDocument doc = null ; 
		List<PDDocument> pages = null ; 
		List<String> targetTexts = Lists.newArrayList() ;
		try{
			doc = PDDocument.load(new File(path));
	        Splitter splitter = new Splitter() ;
	        pages = splitter.split(doc); 
	        PDFTextStripper stripper=new PDFTextStripper(); 
	        stripper.setSortByPosition(true); 
	        int pageSize = pages.size() ;
	        for(int it = pageSize-1 ; it >= 0 ; it--){
	        	 String text = stripper.getText(pages.get(it)) ;
	        	 if(text.replace(" ", "").contains("COMBINEDTOTALS")){
	        		 targetTexts.add(text) ; 
	        	 } 
	        }
		}catch(Exception e){
			LOG.error(path + "    error=" + e.getMessage())  ; 
		}finally{
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
    	String resCC = null , resRm = null   ;
    	BigDecimal rmBig = BigDecimal.ZERO ;
    	String currency = null ;
		String fileName = JsPdfsUtils.getFileName(path)  ;
		List<String> cys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
		if(cys!= null && cys.size() == 1){
			currency = cys.get(0) ;
		}
        for(String targetText : targetTexts){
	        if(targetText != null){
	        	int lastTolal = -1 ;
	        	String[] lines = targetText.split("\\n") ;
	        	for(int i = 0 ; i < lines.length ; i++){
	        		if(lines[i].replace(" ", "").contains("COMBINEDTOTALS")){
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
	        			else if((row.replace(" ", "").contains("GRANDTOTAL") && !row.contains("..") && !row.contains("**")) 
	        					|| row.contains("..") 
	        					|| row.contains("**")){
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
	        		
	        		if(cc != null){
	        			resCC = cc ;
		        	}
	        		if(rm != null){
	        		    rmBig = rmBig.setScale(2, BigDecimal.ROUND_HALF_UP);   
	        			rmBig = rmBig.add(new BigDecimal(rm)) ;
	        			resRm = rmBig.toString() ;
	        		}
	        	} 
	        }
        }
       
        responseBean = new ResponseBean(fileName , resCC , resRm , currency) ;
        return responseBean ; 
	}
	
	@Override
	public String descrition() {
		return "AU" ;
	}

}
