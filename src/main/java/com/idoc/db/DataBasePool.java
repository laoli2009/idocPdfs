package com.idoc.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBasePool{
//	public static final String url = PropertiesUtils.getProperty("pcd_url") ;
//	public static final String user = PropertiesUtils.getProperty("pcd_user") ;
//	public static final String password = PropertiesUtils.getProperty("pcd_password") ;
	//172.31.69.199:1521/IDOCDEV   "jdbc:oracle:thin:@172.31.68.240:1521:CUST" ;
	public static final String url = "jdbc:oracle:thin:@172.31.69.199:1521:IDOCDEV" ;
	public static final String user = "mu_emd" ;
	public static final String password = "mu_emd" ; 
	
    private static DataBasePool instance  ;

    private ComboPooledDataSource dataSource;
    
    static{
        instance = new DataBasePool();
    }

    private DataBasePool() {
        try {
            dataSource = new ComboPooledDataSource(); 
            dataSource.setDriverClass("oracle.jdbc.driver.OracleDriver");
            dataSource.setJdbcUrl(url);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setInitialPoolSize(10) ;
            dataSource.setMaxIdleTime(30) ;
            dataSource.setMinPoolSize(10) ;
            dataSource.setMaxPoolSize(100) ;
        } catch (Exception e) { 
        }
    }
    
    public static DataBasePool getInstance(){
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}