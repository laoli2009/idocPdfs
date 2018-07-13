package com.idoc.pdf;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


public class GBLoader implements Loader{
	private static final Logger LOG = LoggerFactory.getLogger(GBLoader.class) ;
	
	private int findIndx(String str , int n){
		int f = 0 ; 
		for(int i = 0 ; i < str.length() ; i++){
			if(str.charAt(i) == '|'){
				f++ ;
			}
			if(f == n){
				return i ; 
			}
		}
		return -1 ;
	}

	@Override
	public ResponseBean load(String path,String loadCurrency) throws IOException {
		ResponseBean responseBean = null ;
		PDDocument doc = null ;
		List<PDDocument> pages = null ; 
		String targetText = null ;
		try{
			doc = PDDocument.load(new File(path));
	        Splitter splitter = new Splitter() ;
	        pages = splitter.split(doc); 
	        PDFTextStripper stripper = new PDFTextStripper(); 
	        stripper.setSortByPosition(true); 
	        int pageSize = pages.size() ;
	        for(int it = pageSize-1 ; it >= 0 ; it--){
	        	 String text = stripper.getText(pages.get(it)) ;
	        	 if(text.replace(" ", "").contains("TOTALAIRLINE781")){
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
        	String rowFirst = null ;
        	String rowSecond = null ;
        	String rowThird = null ;
        	String[] lines = targetText.split("\\n") ;
        	for(int i = 0 ; i < lines.length ; i++){
        		if(!lines[i].contains("TOTAL") && lines[i].contains("TOT")){
        			rowFirst = lines[i] ; 
        			if(i+1 < lines.length){
        				rowSecond = lines[i+1] ;
        			}
        			if(i+2 < lines.length){
        				rowThird = lines[i+2] ;
        			}
        		}
        	} 
        	String cc1 = null , cc2 = null  , cc3 = null  , cc4 = null  , rm = null ; 
        	int f = findIndx(rowFirst, 1) ;
        	int s = findIndx(rowFirst, 2) ;
        	if(f != -1 && s != -1 && f < s){
        		List<String> wds = JsPdfsUtils.splitWords(rowFirst.substring(f+1 , s)) ;
        		if(wds.size() >= 2){
	        		cc1 = wds.get(0) ;
	        		cc2 = wds.get(1) ;
        		}
        	}
        	f = findIndx(rowFirst, 3) ;
        	if(f != -1){
	        	List<String> wds = JsPdfsUtils.splitWords(rowFirst.substring(f+1)) ;
	        	rm = wds.get(0) ;
        	}
        	f = findIndx(rowSecond, 1) ;
        	s = findIndx(rowSecond, 2) ;
        	if(f != -1 && s != -1 && f < s){
        		List<String> wds = JsPdfsUtils.splitWords(rowSecond.substring(f+1 , s)) ;
        		if(wds.size() >= 1){
        			cc3 = wds.get(0) ;
        		}
        	}
        	if(cc1 != null){
        		cc1 = JsPdfsUtils.filterNum(cc1) ;
        	}
        	if(cc2 != null){
        		cc2 = JsPdfsUtils.filterNum(cc2) ;
        	}
        	if(cc3 != null){
        		cc3 = JsPdfsUtils.filterNum(cc3) ;
        	}
        	if(rm != null){
        		rm = JsPdfsUtils.filterNum(rm) ; 
        	}
        	BigDecimal ccBig = null , ccBigSum = BigDecimal.ZERO ; 
        	if(rowThird != null){
        		f = findIndx(rowThird, 1) ;
            	s = findIndx(rowThird, 2) ;
            	if(f != -1 && s != -1 && f < s){
            		List<String> wds = JsPdfsUtils.splitWords(rowThird.substring(f+1 , s)) ;
            		if(wds.size() >= 1){
            			cc4 = wds.get(0) ;
            		}
            	}
        	} 
        	if(cc4 != null){
        		cc4 = JsPdfsUtils.filterNum(cc4) ;
        	} 
        	
        	if(cc1 != null){
        		ccBigSum = ccBigSum.add(new BigDecimal(cc1)) ;
        		ccBig = ccBigSum ;
        	}
        	if(cc2 != null){
        		ccBigSum = ccBigSum.add(new BigDecimal(cc2)) ;
        		ccBig = ccBigSum ;
        	}
        	if(cc3 != null){
        		ccBigSum = ccBigSum.add(new BigDecimal(cc3)) ;
        		ccBig = ccBigSum ;
        	}
        	if(cc4 != null){
        		ccBigSum = ccBigSum.add(new BigDecimal(cc4)) ;
        		ccBig = ccBigSum ;
        	} 
        	 
        	String cc = null ; 
        	if(ccBig != null){
        		cc = ccBig.toString() ;
        	}
    		String currency = null ;
    		String fileName = JsPdfsUtils.getFileName(path)  ;
    		List<String> cys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
    		if(cys!= null && cys.size() == 1){
    			currency = cys.get(0) ;
    		}
    		responseBean = new ResponseBean(fileName , cc , rm , currency) ;
        }
        return responseBean ; 
	}

	@Override
	public String descrition() {
		return "GB" ;
	}

}
