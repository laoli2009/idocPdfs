package com.idoc.pdf;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class FileNameFilter {
	
	private static class FilterBean{
		private String type ;
		private int first ;
		private int second ;
		private String sampleFileName ;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getFirst() {
			return first;
		}
		public void setFirst(int first) {
			this.first = first;
		}
		public int getSecond() {
			return second;
		}
		public void setSecond(int second) {
			this.second = second;
		}
		public String getSampleFileName() {
			return sampleFileName;
		}
		public void setSampleFileName(String sampleFileName) {
			this.sampleFileName = sampleFileName;
		}
		public FilterBean() {
			super();
		}
		@Override
		public String toString() {
			return "FilterBean [type=" + type + ", first=" + first
					+ ", second=" + second + ", sampleFileName="
					+ sampleFileName + "]";
		}
		
	 }
	
	 private static Map<String , FilterBean> fileNameFilterMap = Maps.newHashMap() ;
	
	 static{
		    Class clazz = LoaderMapping.class;
	        InputStream inputestream = clazz.getResourceAsStream("filefilter.properties");
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
		        FilterBean filterBean = new FilterBean() ;
		        String[] vs = value.split(",") ;
		        String fullFileName = vs[0] ;
		        String type = vs[1] ;
		        filterBean.setType(type) ;
		        filterBean.setSampleFileName(vs[0]); 
		        if(! "3".equals(type)){
		        	int first = Integer.valueOf(vs[2]) ;
		        	int second = Integer.valueOf(vs[3]) ;
		        	filterBean.setSampleFileName(fullFileName.substring(0 , first) + fullFileName.substring(second)); 
		            filterBean.setFirst(first);
		            filterBean.setSecond(second);
		        }
		        fileNameFilterMap.put(key, filterBean) ;
		    }
	}
	 
	public static boolean passByFileName(String fileName){
		try{
			FilterBean filterBean = fileNameFilterMap.get(fileName.substring(0,2).toUpperCase()) ; 
			String nowName = fileName ; 
			if(! "3".equals(filterBean.getType())){ 
				nowName = fileName.substring(0 , filterBean.getFirst()) + fileName.substring(filterBean.getSecond()); 
			}
			return nowName.replaceAll(" ", "").equalsIgnoreCase(filterBean.getSampleFileName().replaceAll(" ", "")) ;
		}catch(Exception e){
		}
		return false ;
	} 
	
	public static String getDayByFileName(String fileName){
		try{
			FilterBean filterBean = fileNameFilterMap.get(fileName.substring(0,2).toUpperCase()) ; 
			if(! "3".equals(filterBean.getType())){ 
				return fileName.substring(filterBean.getSecond() - 2 , filterBean.getSecond()); 
			}
		}catch(Exception e){
		}
		return "01" ;
	} 

}
