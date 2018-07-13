package com.idoc.pdf;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


public class LoaderMapping {
	
	 private static Map<String , Loader> clzzLoader = Maps.newHashMap() ;
	
	 static{
		    Class clazz = LoaderMapping.class;
	        InputStream inputestream = clazz.getResourceAsStream("loader.properties");
		    Properties prop = new Properties();      
		    try {
				prop.load(inputestream);
				inputestream.close();
			} catch (IOException e) {
			}
		    Iterator<String> it = prop.stringPropertyNames().iterator();
		    while(it.hasNext()){
		        String key = it.next();
		        String value = prop.getProperty(key) ;
		        key = key.replace(" ", "") ; 
		        value = value.replace(" ", "") ; 
		        if("AU".equals(value)){
		        	clzzLoader.put(key, new AULoader()) ;
		        }
		        else if("BE".equals(value)){
		        	clzzLoader.put(key, new BELoader()) ;
		        }
		        else if("CH".equals(value)){
		        	clzzLoader.put(key, new CHLoader()) ;
		        }
		        else if("FR".equals(value)){
		        	clzzLoader.put(key, new FRLoader()) ;
		        }
		        else if("KR".equals(value)){
		        	clzzLoader.put(key, new KRLoader()) ;
		        }
		        else if("GB".equals(value)){
		        	clzzLoader.put(key, new GBLoader()) ;
		        }
		        else if("NP".equals(value)){
		        	clzzLoader.put(key, new NPLoader()) ;
		        }
		        else if("TH".equals(value)){
		        	clzzLoader.put(key, new THLoader()) ;
		        }
		    }
	 }
	 
	 public static Loader getLoader(String path){
		 String fileName = JsPdfsUtils.getFileName(path) ;
		 return clzzLoader.get(fileName.substring(0 , 2).toUpperCase()) ;
	 }

}
 