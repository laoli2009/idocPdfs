package com.idoc.pdf;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


public class CitysMapping {
	
	 private static Map<String , String> citys = Maps.newHashMap() ;
	
	 static{
		    Class clazz = CitysMapping.class;
	        InputStream inputestream = clazz.getResourceAsStream("citys.properties");
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
		        citys.put(key, value) ;
		    }
	 }
	 
	 public static String getCity(String key){
		 return citys.get(key) ;
	 }

}
 