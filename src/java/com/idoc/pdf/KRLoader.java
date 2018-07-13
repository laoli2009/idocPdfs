package com.idoc.pdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class KRLoader implements Loader{
	private static final Logger LOG = LoggerFactory.getLogger(KRLoader.class) ;
	
	private int getFirstNumber(String str){
		for(int i = 1 ; i < str.length() ; i++){
			if('0' <= str.charAt(i) && str.charAt(i) <= '9'){
				return i ;
			}
		}
		return -1 ;
	}

	@Override
	public ResponseBean load(String path,String loadCurrency) throws IOException {
		Scanner in = null ;
		String ccLine = null , rmLine = null ;
		try{
			in = new Scanner(new File(path)) ;
		    while(in.hasNext()){
				String line = in.nextLine() ;
				if(line.contains("G.TOTAL")){
					if(ccLine == null){
						ccLine = line ;
					}
					else{
						rmLine = line ; 
					}
				}
			}
	    }catch(Exception e){
			LOG.error(path + "    error=" + e.getMessage())  ; 
		}finally{
		    if(in != null){
		    	in.close();
	        } 
	    }
	    int f = getFirstNumber(ccLine) ;
	    if(f != -1){
	    	ccLine = ccLine.substring(f).replace("\\", "") ;
	    }
	    f = getFirstNumber(rmLine) ;
	    if(f != -1){
	    	rmLine = rmLine.substring(f).replace("\\", "") ;
	    }
	    List<String>ccs = JsPdfsUtils.splitWords(ccLine) ;
	    String cc = JsPdfsUtils.filterNum(ccs.get(2)) ;
	    List<String>rms = JsPdfsUtils.splitWords(rmLine) ;
	    String rm = JsPdfsUtils.filterNum(rms.get(rms.size()-1));
	    String currency = null ;
		String fileName = JsPdfsUtils.getFileName(path) ; 
		List<String> cys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
		if(cys!= null && cys.size() == 1){
			currency = cys.get(0) ;
		} 
	    ResponseBean responseBean = new ResponseBean(fileName, cc, rm, currency) ;
		return responseBean ;
	}

	@Override
	public String descrition() {
		return "KR" ;
	}

}
