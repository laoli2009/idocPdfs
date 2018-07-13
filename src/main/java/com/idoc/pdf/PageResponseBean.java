package com.idoc.pdf;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;

import java.util.List;

public class PageResponseBean {
	private Integer totalNumber ;
    private Integer pageSize ;
    private Integer pageNow ;
    private Integer maxPage ;
    private List<ResponseBean> responseBeans ;
	public Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageNow() {
		return pageNow;
	}
	public void setPageNow(Integer pageNow) {
		this.pageNow = pageNow;
	}
	public List<ResponseBean> getResponseBeans() {
		return responseBeans;
	}
	public void setResponseBeans(List<ResponseBean> responseBeans) {
		this.responseBeans = responseBeans;
	}
	public Integer getMaxPage() {
		return maxPage;
	}
	public void setMaxPage(Integer maxPage) {
		this.maxPage = maxPage;
	}
	public PageResponseBean() {
		super();
		this.responseBeans = Lists.newArrayList() ;
	}
	
	@Override
	public String toString() {
		return JSONObject.toJSONString(this , SerializerFeature.WriteMapNullValue) ;
	}
    
    
}
