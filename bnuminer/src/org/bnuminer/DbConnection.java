package org.bnuminer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {
	public Connection conn;
	public Statement st;
	public ResultSet rs;
	
	/**
	 * <p>功能：通过默认构造函数建立数据库连接
	 */
	public DbConnection() {
		Config conf = new Config();
		try {
			Class.forName(conf.getDbDriver());
			conn = DriverManager.getConnection(conf.getDbURL(), conf.getUserName(), conf.getPassWord());
			st = conn.createStatement();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // End of the constructor.
	
	/**
	 * <p>功能：关闭数据库连接
	 */
	public void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} // End of closeConnection().
}
