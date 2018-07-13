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

public class FRLoader implements Loader{
	private static final Logger LOG = LoggerFactory.getLogger(FRLoader.class) ;
	
	public ResponseBean load(String path,String loadCurrency) throws IOException{
		ResponseBean responseBean = null ;
		PDDocument doc = null ;
		List<PDDocument> pages = null ; 
		List<String> targetTexts = Lists.newArrayList()  ;
		try{
			doc=PDDocument.load(new File(path));
	        Splitter splitter = new Splitter() ;
	        pages = splitter.split(doc); 
	        PDFTextStripper stripper = new PDFTextStripper(); 
	        stripper.setSortByPosition(true); 
	        int pageSize = pages.size() ;
	        for(int it = 0 ; it < pageSize ; it++){
	        	 String text = stripper.getText(pages.get(it)) ;
	        	 String _text = text.replace(" ", "") ;
	        	 if(_text.contains("TOT.TRANSPORTEUR781")){
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
        String ccLine = null ;
        List<String> rmLines = Lists.newArrayList() ;
        if(! targetTexts.isEmpty()){
        	for(String targetText : targetTexts){
        		String[] lines = targetText.split("\\n") ;
        		for(int i = 0 ; i < lines.length ; i++){
            		String row = lines[i] ;
            		String _row = row.replace(" ", "") ;
            		if(_row.contains("TOT.CIE.COMPT:ENEUR")){
            			if(i+1 < lines.length && lines[i+1].contains("CRED")){
            				ccLine = lines[i+1]   ;
            			} 
            			rmLines.add(row) ; 
        		   }
        	   }
             }
       }
       
        BigDecimal ccBig = BigDecimal.ZERO  , rmBig = BigDecimal.ZERO ;
        if(ccLine != null){
        	String[] cols = ccLine.split(" ") ;
      		for(String col : cols){
      			col = col.replace(" ", "") ; 
      			col = JsPdfsUtils.replaceWithBlank(col) ;
      			if(! Strings.isNullOrEmpty(col)){
      				if(col.contains("CRED") || col.contains("TOT.") || col.contains("CIE.") || col.contains("EUR") || col.contains("COMPT:EN") ){
      					continue ;
      				}
      				col = JsPdfsUtils.filterNum(col)  ;
      				ccBig = ccBig.add(new BigDecimal(col)) ; 
      			}
      		}
        }
        if(! rmLines.isEmpty()){
        	for(String rmLine : rmLines){
                String rm = null ;
        		String[] cols = rmLine.split(" ") ;
          		for(String col : cols){
          			col = col.replace(" ", "") ; 
          			col = JsPdfsUtils.replaceWithBlank(col) ;
          			if(! Strings.isNullOrEmpty(col)){
          				if(col.contains("TOT.") || col.contains("CIE.") || col.contains("EUR") || col.contains("COMPT:EN") ){
          					continue ;
          				}
          				col = JsPdfsUtils.filterNum(col)  ;
          				rm = col ; 
          			}
          		}
          		if(rm != null){
          			rmBig = rmBig.add(new BigDecimal(rm)) ; 
  				}
        	}
        }
    	String currency = null ;
		String fileName = JsPdfsUtils.getFileName(path) ; 
		List<String> cys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
		if(cys!= null && cys.size() == 1){
			currency = cys.get(0) ;
		} 
        ccBig = ccBig.setScale(2, BigDecimal.ROUND_HALF_UP);   
        rmBig = rmBig.setScale(2, BigDecimal.ROUND_HALF_UP);   
        responseBean = new ResponseBean(fileName , ccBig.toString() , rmBig.toString() , currency) ;
        return responseBean ; 
	}
	
	public String descrition() {
		return "FR" ;
	}

}
