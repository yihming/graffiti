/**
 * Version: 1.2: All Rights Reserved to AlphaSphere R&D Group
 * 2009-8-26 Guo Kai
 * 
 * Version: 1.3: Add closeConnection()
 * 2009-8-27 Bauer Yung
 * 
 * Version: 1.6: Add getConnection()
 * 2009-8-27 Bauer Yung
 * 
 * Version: 1.8: Remove getConnection()
 * 2009-8-27 Bauer Yung
 */
package com.as.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.as.config.Config;

public class DbConnection {
	public Connection conn;
	public Statement st;
	public ResultSet rs;

	public DbConnection() { //采用默认构造函数，建立数据库连接。
		Config conf = new Config();
		try {
			Class.forName(conf.getDbDriver());
			conn = DriverManager.getConnection(conf.getDriverConn(), conf
					.getDbName(), conf.getDbPass());
			st = conn.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() { // 断开数据库连接
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
/*
	public static void main(String[] args){
		DbConnection db = new DbConnection();
		System.out.println(db.st);
	}
*/
}
