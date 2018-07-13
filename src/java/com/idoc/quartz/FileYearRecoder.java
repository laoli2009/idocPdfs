package com.idoc.quartz;

import com.google.common.collect.Lists;

import java.util.List;

public class FileYearRecoder implements Comparable<FileYearRecoder>{
	private String year ;
	private List<FileMonthRecoder> months ;
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public List<FileMonthRecoder> getMonths() {
		return months;
	}
	public void setMonths(List<FileMonthRecoder> months) {
		this.months = months;
	}
	public FileYearRecoder() {
		super();
		months = Lists.newArrayList() ;
	}   
	@Override
	public int compareTo(FileYearRecoder other) { 
		return Integer.valueOf(other.getYear()).compareTo(Integer.valueOf(this.getYear())) ;
	}
}
