package com.idoc.quartz;

import java.util.List;

public class FileMonthRecoder implements Comparable<FileMonthRecoder>{
	private String month ;
	private List<String> fileName ;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public List<String> getFileName() {
		return fileName;
	}
	public void setFileName(List<String> fileName) {
		this.fileName = fileName;
	}
	public FileMonthRecoder() {
		super();
	} 
	@Override
	public int compareTo(FileMonthRecoder other) {
		return Integer.valueOf(other.getMonth()).compareTo(Integer.valueOf(this.getMonth())) ;
	}
}
