package com.idoc.pdf;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsPdfsUtils {
	
	public static Boolean hasDot(String str){
		int n = str.length() ;
		if(n >= 3){
			if('.' == str.charAt(n-3) || ',' == str.charAt(n-3)){
				return true ;
			}
		}
		return false ;
	}
	
	public static String filterNum(String str){
		String neg = "" ;
		if(str.charAt(str.length()-1) == '-'|| str.charAt(str.length()-1) == '−'){
			str = str.substring(0 , str.length()-1) ;
		    neg = "-" ;
		}
		if(hasDot(str)){
			int n = str.length() ;
			String pre = str.substring(0 , n-3).replace(",", "").replace(".", "") ;
			String last = str.substring(n-2) ;
			return neg + pre + "." + last ;
		}
		return neg + str.replace(",", "").replace(".", "") ;
	}

	public static String replaceWithBlank(String str){
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		String finishedReplaceStr = m.replaceAll("");
		return finishedReplaceStr;
	}
	
	public static String getFileName(String path){ 
		path =  path.replace("\\", "/") ; 
		return path.substring(path.lastIndexOf("/")+1) ;
	}
	
	public static List<String> splitWords(String text){
		String[] wds = text.split(" ") ;
		List<String> words = Lists.newArrayList() ;
		for(String wd : wds){
			wd = JsPdfsUtils.replaceWithBlank(wd) ;
			if(! Strings.isNullOrEmpty(wd)){
				words.add(wd) ;
			}
		}
		return words ;
	}
	
}
