package com.idoc.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils extends PropertyPlaceholderConfigurer {  
	private static final Logger logger = Logger.getLogger(PropertiesUtils.class);  
  
    private static final Map<String, String> map = new HashMap<String, String>() ;   
    private static final String localJiesuanFtpFilesDirectory = "local.jiesuan.ftp.files.directory" ; 
    private static final String localJiesuanMakepdfFilesDirectory = "local.jiesuan.makepdf.files.directory" ; 
  
    @Override  
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) {  
        super.processProperties(beanFactory, props) ;  
    	for (Object key : props.keySet()) {  
            String keyStr = key.toString().replace(" ", "") ;  
            String value = props.getProperty(keyStr).replace(" ", "") ;  
            logger.info(key+"="+value);
            map.put(keyStr, value) ;  
            if(localJiesuanFtpFilesDirectory.equals(keyStr) || localJiesuanMakepdfFilesDirectory.equals(keyStr)){ 
            	  File file =new File(value);    
                  if(!file.exists()  && !file.isDirectory()){     
                      file.mkdirs();    
                  } 
            }
        }  
    }  
      
    public static String getProperty(String key) {  
        return map.get(key);  
    }  
      
}  