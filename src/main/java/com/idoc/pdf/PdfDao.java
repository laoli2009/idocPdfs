package com.idoc.pdf;

import com.idoc.db.OracleDBHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class PdfDao {
	
	public boolean isExistResponseBean(ResponseBean responseBean){
		String sql = " select count(1) count from jiesuan_he " +
				     " where name = ? and filedate = to_date(? , 'YYYY-MM-DD')  and currency = ? " ;
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		ResultSet resultSet = null ; 
		boolean isExist = false ;
		try {
			dbHelper.pst.setString(1, responseBean.getName()) ;
			dbHelper.pst.setString(2, responseBean.getFileDate()) ;
			dbHelper.pst.setString(3, responseBean.getCurrency()) ;
			resultSet = dbHelper.pst.executeQuery() ; 
			while(resultSet.next()){
				if(resultSet.getInt("count") > 0){
					isExist = true ;
					break ;
				}
			}
		} catch (SQLException e) {
		} catch (Exception e) {
		}  finally {
			dbHelper.close(resultSet);
		} 
		return isExist ;
	} 
	
	public void addresponseBean(ResponseBean responseBean) {
		String sql = " insert into jiesuan_he(name , currency , filedate , cc , rm , calcSuccess , file_path) " +
					 " values (? , ? , to_date(? , 'YYYY-MM-DD') , ? , ?  , ? , ?) " ;
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		try {
			dbHelper.pst.setString(1, responseBean.getName()) ;
			dbHelper.pst.setString(2, responseBean.getCurrency()) ;
			dbHelper.pst.setString(3, responseBean.getFileDate()) ;
			dbHelper.pst.setBigDecimal(4, responseBean.getCC() == null ? null : new BigDecimal(responseBean.getCC())) ;
			dbHelper.pst.setBigDecimal(5, responseBean.getRM() == null ? null : new BigDecimal(responseBean.getRM())) ;
			dbHelper.pst.setInt(6, responseBean.getCalcSuccess()? 1 : 0) ;
			dbHelper.pst.setString(7, responseBean.getPath()) ;
			dbHelper.pst.executeUpdate();
		} catch (SQLException e) {
		} catch (Exception e) {
		}  finally {
			dbHelper.close(null);
		} 
	}
	
	
	private int getCount(String fromDate , String toDate , String name){
		String sql = " select count(1) count from jiesuan_he j "  + 
					 " where j.filedate >= to_date(? , 'yyyy-mm-dd') " + 
				     " and  j.filedate <= to_date(? , 'yyyy-mm-dd') " ;
		if(! "all".equals(name)){
			  sql += " and substr(j.name , 0 , 2) = ? "  ;
		}
		
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		ResultSet resultSet = null ; 
		try {
			dbHelper.pst.setString(1, fromDate) ;
			dbHelper.pst.setString(2, toDate) ;
			if(! "all".equals(name)){
				dbHelper.pst.setString(3 , name) ;
			}
			resultSet = dbHelper.pst.executeQuery() ; 
			while(resultSet.next()){ 
				return resultSet.getInt("count") ; 
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			dbHelper.close(resultSet);
		} 
		return 0 ; 
	}
	
	
	public PageResponseBean getPageResponseBean(String fromDate , String toDate , String name , int pageNow , int pageSize){
		PageResponseBean pageResponseBean = new PageResponseBean() ;
		int count = getCount(fromDate , toDate , name) ;
		pageResponseBean.setPageNow(pageNow) ;
		pageResponseBean.setTotalNumber(count) ; 
		pageResponseBean.setPageSize(pageSize) ;
		pageResponseBean.setMaxPage(count%pageSize == 0 ? count/pageSize : count/pageSize+1) ;
		int startOffset = pageSize * (pageNow-1) + 1 ;
		int endOffset = pageSize * pageNow ; 
		String sql = " select name, currency, cc, rm, file_path , to_char(filedate , 'yyyy-mm-dd') filedate , calcsuccess " + 
					 " from (select t.name, t.currency, t.cc, t.rm, t.filedate, t.calcsuccess , t.file_path , rownum r " + 
					 "       from (select j.name, j.currency, j.cc, j.rm, j.filedate , j.calcsuccess , j.file_path " + 
				     "              from jiesuan_he j " + 
				     "             where j.filedate >= to_date(? , 'yyyy-mm-dd') " + 
				     "               and j.filedate <= to_date(? , 'yyyy-mm-dd') " ; 
		if(! "all".equals(name)){
			   sql +=             "  and substr(j.name , 0 , 2) = ? "  ;
		}
			  sql +=              "  order by filedate desc, name asc) t " + 
				        "  where rownum <= ?) " + 
				     " where r > = ? " ;
		
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		ResultSet resultSet = null ; 
		try {
			dbHelper.pst.setString(1, fromDate) ;
			dbHelper.pst.setString(2, toDate) ;
			if(! "all".equals(name)){
				dbHelper.pst.setString(3 , name) ;
				dbHelper.pst.setInt(4 , endOffset) ;
				dbHelper.pst.setInt(5 , startOffset) ;
			}
			else{
				dbHelper.pst.setInt(3 , endOffset) ;
				dbHelper.pst.setInt(4 , startOffset) ;
			}
			resultSet = dbHelper.pst.executeQuery() ; 
			while(resultSet.next()){ 
				ResponseBean responseBean = new ResponseBean() ;
				responseBean.setName(resultSet.getString("name")) ;
				responseBean.setPath(resultSet.getString("file_path"));
				String country = resultSet.getString("name").substring(0, 2).toUpperCase() ;
				responseBean.setCountry(country) ;
				responseBean.setCity(CitysMapping.getCity(country)) ;
				BigDecimal cc = resultSet.getBigDecimal("cc") ;
				if(cc == null){
					responseBean.setCC(null) ;
				}
				else{
					responseBean.setCC(String.format("%.02f", cc)) ;
				}
				BigDecimal rm = resultSet.getBigDecimal("rm") ;
				if(rm == null){
					responseBean.setRM(null) ;
				}
				else{
					responseBean.setRM(String.format("%.02f", rm)) ;
				}
				responseBean.setCurrency(resultSet.getString("currency"));
				responseBean.setCalcSuccess("1".equals(resultSet.getString("calcsuccess")));
				responseBean.setFileDate(resultSet.getString("filedate")) ;
				pageResponseBean.getResponseBeans().add(responseBean) ;
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			dbHelper.close(resultSet);
		} 
		return pageResponseBean ; 
	}
	
	public List<ResponseBean> getResponseBeans(String fromDate , String toDate , String name){
		List<ResponseBean> responseBeans = Lists.newArrayList() ;
		String sql = " select j.name, j.currency, j.cc, j.rm, to_char(j.filedate , 'yyyy-mm-dd') filedate , j.calcsuccess " + 
				     "             from jiesuan_he j " + 
				     "             where j.filedate >= to_date(? , 'yyyy-mm-dd') " + 
				     "               and j.filedate <= to_date(? , 'yyyy-mm-dd') " ; 
		if(! "all".equals(name)){ 
			   sql +=             "  and substr(j.name , 0 , 2) = ? "  ;
			   sql +=             "  order by filedate desc " ; 
		}
		else{	   
			   sql +=             "  and  j.name is not null "  ;
			   sql +=             "  order by substr(j.name , 0 , 2) asc , filedate desc " ; 
		}
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		ResultSet resultSet = null ; 
		try {
			dbHelper.pst.setString(1, fromDate) ;
			dbHelper.pst.setString(2, toDate) ;
			if(! "all".equals(name)){
				dbHelper.pst.setString(3 , name) ; 
			} 
			resultSet = dbHelper.pst.executeQuery() ; 
			while(resultSet.next()){ 
				ResponseBean responseBean = new ResponseBean() ;
				responseBean.setName(resultSet.getString("name")) ;
				String country = resultSet.getString("name").substring(0, 2).toUpperCase() ;
				responseBean.setCountry(country) ;
				responseBean.setCity(CitysMapping.getCity(country)) ;
				BigDecimal cc = resultSet.getBigDecimal("cc") ;
				if(cc == null){
					responseBean.setCC(null) ;
				}
				else{
					responseBean.setCC(String.format("%.02f", cc)) ;
				}
				BigDecimal rm = resultSet.getBigDecimal("rm") ;
				if(rm == null){
					responseBean.setRM(null) ;
				}
				else{
					responseBean.setRM(String.format("%.02f", rm)) ;
				}
				responseBean.setCurrency(resultSet.getString("currency"));
				responseBean.setCalcSuccess("1".equals(resultSet.getString("calcsuccess")));
				responseBean.setFileDate(resultSet.getString("filedate")) ;
				responseBeans.add(responseBean) ;
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			dbHelper.close(resultSet);
		} 
		return responseBeans  ; 
	}
	
	public boolean isCalcFilePath(String filePath){
		filePath = filePath.replace("\\", "/") ;
		String sql = " select count(1) count from jiesuan_he " +
				     " where file_path = ? " ;
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		ResultSet resultSet = null ; 
		boolean isCalc = false ;
		try {
			dbHelper.pst.setString(1, filePath) ; 
			resultSet = dbHelper.pst.executeQuery() ; 
			while(resultSet.next()){
				if(resultSet.getInt("count") > 0){
					isCalc = true ;
					break ;
				}
			}
		} catch (SQLException e) {
		} catch (Exception e) {
		}  finally {
			dbHelper.close(resultSet);
		} 
		return isCalc ;
	}
	
	
	public Set<String> getLastMonthCalcFilePaths(){
		String sql = " select file_path from jiesuan_he j " + 
					 " where j.filedate >=  trunc(add_months(sysdate, -2), 'month') " ;
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		ResultSet resultSet = null ; 
		Set<String> filePaths = Sets.newHashSet() ;
		try {
			resultSet = dbHelper.pst.executeQuery() ; 
			while(resultSet.next()){
				String filePath = resultSet.getString("file_path") ;
				filePaths.add(filePath.replace(" ", "").replace("\\" , "/")) ;
			}
		} catch (SQLException e) {
		} catch (Exception e) {
		}  finally {
			dbHelper.close(resultSet);
		} 
		return filePaths ;
	} 
	
	public String getLatestFileDate(){
		String sql = " select to_char(max(filedate) , 'yyyy-mm-dd') filedate from jiesuan_he "  ;
		OracleDBHelper dbHelper = new OracleDBHelper(sql);
		ResultSet resultSet = null ; 
		String latestFileDate = null ;
		try {
			resultSet = dbHelper.pst.executeQuery() ; 
			while(resultSet.next()){
				latestFileDate = resultSet.getString("filedate") ;
			}
		} catch (SQLException e) {
		} catch (Exception e) {
		}  finally {
			dbHelper.close(resultSet);
		} 
		return latestFileDate ;
	} 
	
	
	
	public static void main(String[] args) {
		PdfDao dao = new PdfDao() ;
		//System.out.println(dao.getCount("2017-10-01", "2017-11-01" , "all")) ; 
	//	System.out.println(dao.getPageResponseBean("2017-06-01", "2017-07-01" , "all" , 1 , 15)) ; 
		//System.out.println(dao.getResponseBeans("2015-10-01", "2018-11-01" , "AU")) ; 
	  /*  boolean x = dao.isCalcFilePath("E:/ly/unzipjsuan/BSP/2018/01/MOaa7814_20180124_Airline Billing013.zip/MO_FCAIBILLDET_781_20180103.PDF") ;
	    System.out.println(x) ;
	    ResponseBean r = new ResponseBean() ;
	    r.setName("FRae7814_20160207_bspR3200.781.pdf");
	    r.setFileDate("2016-02-07");
	    r.setCurrency("EUR");
	    x = dao.isExistResponseBean(r) ;
	    System.out.println(x);*/
	//	System.out.println(dao.getLastMonthCalcFilePaths()) ;
		/* boolean x = dao.isCalcFilePath("E:/ly/unzipjsuan/BSP/2018/01/MOaa7814_20180124_Airline Billing013.zip/MO_FCAIBILLDET_781_20180103.PDF") ;
	     System.out.println(x) ;*/
		System.out.println(dao.getLatestFileDate());
	}

}
