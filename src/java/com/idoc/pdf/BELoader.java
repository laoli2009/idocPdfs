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

public class BELoader implements Loader{
	private static final Logger LOG = LoggerFactory.getLogger(BELoader.class) ;
	
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
	        	 if(text.replace(" ", "").contains("TOTALNETTOBEPAID")){
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
        	String rm = null  , _GROSSCREDITINCLUDINGTAXES = null  , _CREDITBREAKDOWN = null ; 
        	String[] lines = targetText.split("\\n") ;
        	for(int i = 0 ; i < lines.length ; i++){
        		String row = lines[i] ;
        		if(row.replace(" ", "").contains("TOTALNETTOBEPAID")){
        			String[] cols = row.split(" ") ;
    				for(String col : cols){
    					col = col.replace(" ", "") ; 
    					col = JsPdfsUtils.replaceWithBlank(col) ;
    					if(! Strings.isNullOrEmpty(col)){
    						rm = col ; 
    					}
    				}
    				rm = JsPdfsUtils.filterNum(rm)  ;
        		}
        		else if(row.replace(" ", "").contains("GROSSCREDITINCLUDINGTAXES")){
        			String[] cols = row.split(" ") ;
    				for(String col : cols){
    					col = col.replace(" ", "") ; 
    					col = JsPdfsUtils.replaceWithBlank(col) ;
    					if(! Strings.isNullOrEmpty(col)){
    						_GROSSCREDITINCLUDINGTAXES = col ; 
    					}
    				}
    				_GROSSCREDITINCLUDINGTAXES = JsPdfsUtils.filterNum(_GROSSCREDITINCLUDINGTAXES)  ;
        		}
        		else if(row.replace(" ", "").contains("CREDITBREAKDOWN")){
        			String[] cols = row.split(" ") ;
        			String pre = null ;
    				for(String col : cols){
    					col = col.replace(" ", "") ; 
    					col = JsPdfsUtils.replaceWithBlank(col) ;
    					if(! Strings.isNullOrEmpty(col) ){
    						if(":".equals(pre)){
    							_CREDITBREAKDOWN = col ;
    						} 
    						pre = col ;
    					}
    				}
    				_CREDITBREAKDOWN = JsPdfsUtils.filterNum(_CREDITBREAKDOWN)  ;
        		}
        	}
        	String currency = null ;
    		String fileName = JsPdfsUtils.getFileName(path) ; 
    		List<String> cys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
    		if(cys!= null && cys.size() == 1){
    			currency = cys.get(0) ;
    		} 
        	BigDecimal ccBig = BigDecimal.ZERO ;
        	if(_CREDITBREAKDOWN != null){
        		ccBig = ccBig.add(new BigDecimal(_CREDITBREAKDOWN)) ;
        	}
           	if(_GROSSCREDITINCLUDINGTAXES != null){
        		ccBig = ccBig.add(new BigDecimal(_GROSSCREDITINCLUDINGTAXES)) ;
        	}
        	ccBig = ccBig.setScale(2, BigDecimal.ROUND_HALF_UP);   
            responseBean = new ResponseBean(fileName , ccBig.toString() , rm  , currency) ;
        }
        return responseBean ; 
	}
	
	public String descrition() {
		return "BE" ;
	}

}
