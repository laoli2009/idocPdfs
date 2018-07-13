package com.idoc.pdf;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


public class PdfService {
	private static final Logger LOG = LoggerFactory.getLogger(PdfService.class) ;
	
	public static ResponseBean calcSigle(String path , String loadCurrency) throws IOException{
		String fileName = JsPdfsUtils.getFileName(path) ;
		if(! FileNameFilter.passByFileName(fileName)){
			return null ;
		}
		Loader loader = LoaderMapping.getLoader(path) ; 
		if(loader != null){
			ResponseBean responseBean = null ;
			try{
			    responseBean = loader.load(path , loadCurrency) ;
			    if(responseBean == null){
					responseBean = new ResponseBean() ;
					responseBean.setName(fileName) ;
				}
				responseBean.setFileDate(FileDateMapping.getFileDate(path));
				responseBean.setCalcSuccess(Boolean.TRUE) ;
				responseBean.setPath(path);
			}catch(Exception e){
				responseBean = new ResponseBean() ;
				responseBean.setName(fileName) ;
				responseBean.setFileDate(FileDateMapping.getFileDate(path));
				responseBean.setCalcSuccess(Boolean.FALSE) ;
				responseBean.setPath(path);
			}
			return responseBean ; 
		}
		ResponseBean responseBean = new ResponseBean() ;
		responseBean.setFileDate(FileDateMapping.getFileDate(path));
		responseBean.setName(fileName) ;
		responseBean.setCalcSuccess(Boolean.FALSE) ;
		responseBean.setPath(path);
		return responseBean ;
	}
	
	public static List<ResponseBean> calc(String path) throws IOException{
		String fileName = JsPdfsUtils.getFileName(path) ;
		if(! FileNameFilter.passByFileName(fileName)){
			return null ;
		}
		List<ResponseBean> responseBeans = Lists.newArrayList() ;
		List<String> currencys = CurrencyMapping.getCurrency(fileName.substring(0, 2).toUpperCase()) ;
		if(currencys != null && !currencys.isEmpty()){ 
			for(String currency : currencys){
				responseBeans.add(calcSigle(path, currency)) ;
			}
		} 
		return responseBeans ;
	}
	
	public static void main(String[] args) throws IOException {
		//PH_FCAIBILLSUM_B_781_0402_PHP 没有
		//AU_FCAIBILLDET_B_781_0605_AUD
		//SKac7814_20170823_bspR3200.781.pdf
		//SKac7814_20170823_bspR3200.781
		//PTca7814_20170607_bspR3630.781
		//PTca7814_20170607_bspR3630.781
		//FRae7814_20171031_bspR3200.781
		//KRab7814_20170220_Billing Statement
		//AEai7814_20170315_bspR3922.781.pdf
		//ITia7814_20170107_bspR3920.781.pdf
	    String path = "C:/Users/liyang/Desktop/2017unzip/201707月账单/KTM/NP_FCAIBILLDET_B_781_0903_NPR.pdf" ;
	    path = "C:/Users/liyang/Desktop/HK_FCAIBILLDET_781_20180104.PDF" ;
		//path = "C:/Users/liyang/Desktop/2017unzip/201710月账单/MNL/PH_FCAIBILLDET_B_781_1004_PHP.pdf" ;
	    List<ResponseBean> responseBean = null ;
        responseBean = PdfService.calc(path) ;
        System.out.println(path);
        System.out.println(responseBean);
		
/*		String unzipdir = "E:/ly/unzipjsuan" ;
		List<String> listname = Lists.newArrayList() ;
		ZipUtils.readAllFile(unzipdir, listname); 
	    
	    PdfDao pdfDao = new PdfDao() ;
	    for(String path : listname){
	    	LOG.info(path) ;
	    	String fileName = JsPdfsUtils.getFileName(path) ;
			if(! FileNameFilter.passByFileName(fileName)){
				continue ;
			}
	    	if(pdfDao.isCalcFilePath(path)){
	    		LOG.info("had calc " + path) ;
	    		continue ;
	    	}
	    	LOG.info("to calc..") ;
	    	List<ResponseBean> responseBeans = null ;
	    	try{
	    		responseBeans = PdfService.calc(path) ;
	    	}catch(Exception e){
	    		
	    	}
		    if(responseBeans != null && !responseBeans.isEmpty()){
		    	for(ResponseBean responseBean : responseBeans){
		    		if(responseBean == null){
		    			continue ;
		    		} 
			    	if(! pdfDao.isExistResponseBean(responseBean)){ 
			    		pdfDao.addresponseBean(responseBean) ; 
			    	}
			    	else{
			    		LOG.info("had insert " + responseBean.getName()) ;
			    	}
		    	}
		    }
	    }*/
	} 

}
