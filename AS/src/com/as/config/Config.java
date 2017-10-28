/**
 * Version: 1.2: All Rights Reserved to AlphaSphere R&D Group
 * 2009-8-26 Guo Kai
 */
package com.as.config;

public class Config {
	String DbDriver = "oracle.jdbc.driver.OracleDriver";
	String DriverConn = "jdbc:oracle:thin:@10.0.3.10:1521:icss";
	String DbName = "asoa";
	String DbPass = "asoa";
	public Config(){		
	}
	public Config(String DbDriver,String DriverConn,String DbName,String DbPass ){
		this.DbDriver = DbDriver;
		this.DriverConn = DriverConn;
		this.DbName = DbName;
		this.DbPass = DbPass;		
	}
	public String getDbDriver() {
		return DbDriver;
	}
	public void setDbDriver(String dbDriver) {
		DbDriver = dbDriver;
	}
	public String getDriverConn() {
		return DriverConn;
	}
	public void setDriverConn(String driverConn) {
		DriverConn = driverConn;
	}
	public String getDbName() {
		return DbName;
	}
	public void setDbName(String dbName) {
		DbName = dbName;
	}
	public String getDbPass() {
		return DbPass;
	}
	public void setDbPass(String dbPass) {
		DbPass = dbPass;
	}	
}
