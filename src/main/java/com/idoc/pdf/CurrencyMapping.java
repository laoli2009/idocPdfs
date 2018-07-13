package com.idoc.pdf;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class CurrencyMapping {
	
	 private static Map<String , List<String>> currency = Maps.newHashMap() ;
	
	 static{
		    Class clazz = CurrencyMapping.class;
	        InputStream inputestream = clazz.getResourceAsStream("currency.properties");
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
		        String[] valueStr = value.replace(" ", "").split(",") ; 
		        List<String> values = Lists.newArrayList() ;
		        for(String str : valueStr){
		        	values.add(str) ;
		        }
		        currency.put(key, values) ;
		    }
	 }
	 
	 public static List<String> getCurrency(String key){
		 return currency.get(key) ;
	 }

}
 