package com.idoc.pdf;

import com.google.common.base.Strings;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class CHLoader implements Loader{
	private static final Logger LOG = LoggerFactory.getLogger(CHLoader.class) ;
	
	public ResponseBean load(String path,String loadCurrency) throws IOException{
		ResponseBean responseBean = null ;
		PDDocument doc = null ;
		List<PDDocument> pages  = null ;
		String targetText = null ;
		try{
			doc=PDDocument.load(new File(path));
	        Splitter splitter = new Splitter() ;
	        pages = splitter.split(doc); 
	        PDFTextStripper stripper=new PDFTextStripper(); 
	        stripper.setSortByPosition(true); 
	        int pageSize = pages.size() ;
	       
	        for(int it = pageSize-1 ; it >= 0 ; it--){
	        	 String text = stripper.getText(pages.get(it)) ;
	        	 String _text = text.replace(" ", "") ;
	        	 if(_text.contains("TOTALAIRLINE781") || _text.contains("TOTALCOMPANHIA781")  || _text.contains("TOTALCOMPANIA781")){
	        		 targetText = text ; 
	        		 break ; 
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
        if(targetText != null){
        	String rm = null , cc = null ; 
        	BigDecimal ccBig = BigDecimal.ZERO ;
        	String[] lines = targetText.split("\\n") ;
        	for(int i = 0 ; i < lines.length ; i++){
        		String row = lines[i] ;
        		if(row.contains("TOT.") || row.contains("TTL.")){
        			String[] cols = row.split(" ") ;
    				for(String col : cols){
    					col = col.replace(" ", "") ; 
    					col = JsPdfsUtils.replaceWithBlank(col) ;
    					if(! Strings.isNullOrEmpty(col)){
    						rm = col ; 
    					}
    				}
    				rm = JsPdfsUtils.filterNum(rm)  ;
    				
    				if(i+1 < lines.length && lines[i+1].contains("CRED")){
    					row = lines[i+1] ;
    					cols = row.split(" ") ;
    					for(String col : cols){
        					col = col.replace(" ", "") ; 
        					col = JsPdfsUtils.replaceWithBlank(col) ;
        					if(! Strings.isNullOrEmpty(col)){
        						if(col.contains("CRED") || col.contains(":")){
        							continue ;
        						} 
        						col = JsPdfsUtils.filterNum(col)  ;
        						ccBig = ccBig.add(new BigDecimal(col)) ;
        					}
        				}
    					ccBig = ccBig.setScale(2, BigDecimal.ROUND_HALF_UP);   
    					cc = ccBig.toString() ;
    				}
        		}
        		String currency = null ;
        		String fileName = JsPdfsUtils.getFileName(path) ; 
        		List<String> cys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
        		if(cys!= null && cys.size() == 1){
        			currency = cys.get(0) ;
        		} 
        		responseBean = new ResponseBean(fileName , cc , rm , currency) ;
        	}
        }
        return responseBean ; 
	}
	
	public String descrition() {
		return "CH" ;
	}

}
