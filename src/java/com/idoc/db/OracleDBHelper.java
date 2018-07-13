package com.idoc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleDBHelper {  
  
    public Connection conn = null ;
    public PreparedStatement pst = null;  
  
    public OracleDBHelper(String sql) {  
        try {   
            conn = DataBasePool.getInstance().getConnection() ;  
            pst = conn.prepareStatement(sql); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public void close(ResultSet rs) {  
        try {  
        	if (rs != null) {
				rs.close();
			}
        	if (this.conn != null) {
        		this.conn.close();  
			}
        	if (this.pst != null) {
        		this.pst.close();  
			}
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    } 
   
    
}