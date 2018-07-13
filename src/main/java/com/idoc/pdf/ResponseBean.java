package com.idoc.pdf;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ResponseBean {
	private String name ;
	private String CC ;
	private String RM ;
	private String currency ;
	private String fileDate ; 
	private Boolean calcSuccess ;
	private String country ;
	private String city ;
	private String path ; 
	public String getCC() {
		return CC;
	}
	public void setCC(String cC) {
		CC = cC;
	}
	public String getRM() {
		return RM;
	}
	public void setRM(String rM) {
		RM = rM;
	}
	public ResponseBean(String cC, String rM) {
		super();
		this.CC = cC;
		this.RM = rM;
		this.name = null;
		this.currency = null;
		this.fileDate = null ;
		this.country = null ;
		this.city = null ;
		this.path = null ;
		this.calcSuccess = Boolean.TRUE ;
	}
	
	public ResponseBean(String name, String cC, String rM) {
		super();
		this.name = name;
		this.CC = cC;
		this.RM = rM;
		this.currency = null;
		this.fileDate = null ;
		this.country = null ;
		this.city = null ;
		this.path = null ;
		this.calcSuccess = Boolean.TRUE ;
	}
	public ResponseBean(String name, String cC, String rM, String currency) {
		super();
		this.name = name;
		this.CC = cC;
		this.RM = rM;
		this.currency = currency;
		this.fileDate = null ;
		this.country = null ;
		this.city = null ;
		this.path = null ;
		this.calcSuccess = Boolean.TRUE ;
	}
	public ResponseBean() {
		super();
		this.name = null ;
		this.CC = null;
		this.RM = null;
		this.currency = null;
		this.fileDate = null ;
		this.country = null ;
		this.city = null ;
		this.path = null ;
		this.calcSuccess = Boolean.TRUE ;
	}
	
	public Boolean getCalcSuccess() {
		return calcSuccess;
	}
	public void setCalcSuccess(Boolean calcSuccess) {
		this.calcSuccess = calcSuccess;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getFileDate() {
		return fileDate;
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	} 
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPath() {
		if(path != null){
			return path.replace("\\" , "/") ;
		}
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return JSONObject.toJSONString(this , SerializerFeature.WriteMapNullValue) ;
	} 
	
}
