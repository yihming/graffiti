package com.as.function;
/**
 * AlphaSphere Group Team Function Library
 * All Rights Reserved 
 * version 1.11 2009-08-27
 * Guo Kai : Time pattern Gao Ding
 * 
 */
/***************************************************************************
 * 				AS.java  -  description						   *
 * 					------------------									   *
 * 		begin		: Wed Aug 26 2009									   *
 * 		author		: 2009 by Bauer Yung								   *
 * 		email		: bauerdelscu@gmail.com								   *
 * 		version		: 0.5a												   *
 ***************************************************************************/
/***************************************************************************
 * 		change-1	: Wed Aug 26 2009								   	   *
 * 		author		: 2009 by Kevin										   *
 * 		version		: 0.7a												   *
 **************************************************************************/
/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/


/***************************************************************************
 * A sample of using this package to convert Clob to String:
 * 
 *		1. At the front of your JSP file, write as follows:
 *			(在jsp文件开头中，添加如下语句)
 *
 *					<%@ page import="com.as.function.*" %>
 *
 *		2. When you deal with a Clob type content, write the following statements:
 *			（在对Clob对象进行处理的地方，添加如下语句以转换为String对象【my_as, result, "dept_intro"可自行选择】）
 *		
 *				AS my_as = new AS();
 *				String result = my_as.Clob_To_String(rs.getClob("dept_intro"));
 *
 *		3. Finally, you can put the generated String object "result" into the place you'd like to show in the browser, e.g.:
 *			（最后，在需要显示的地方添加转换后的String对象，如下）
 *		
 *				<td> <%= result %> </td>
 *
 *
 *Version 1.13 2009-8-29
 *add: judge whether the parameter is NullPointer, if it is,we return ""
 *instead of exception
 *Guokai 
 *
 *
 *Version 1.14 2009-8-29
 *Guo Kai : debug
 *
 *
 *************************************************************/
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class AS {
	// The class encapsulating the core operations of AS Project.
		/**
		 * 功能：将DB中的Clob转换为Java中的String类型
		 * 参数：读出的Clob类实体
		 * 返回值：String
		 */
	public String Clob_To_String(Clob base){// Converting main method.
/*
 * Input: Clob ConverClub.base
 * Output: A String object, or an Exception with content of "error". 
 */
		if(base == null) return "";
		try {
			char[] chars = new char[(int) base.length()]; // The temporary character array, initialized with the length of "base".
			try {
				base.getCharacterStream().read(chars);  // Assign the content of "base" to the character array "chars".
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";  // If an IOException happens, return an "error" content.
			}
			String content = new String(chars);  // Assign the content of "chars" to the final String object "content".
			return content;  // return this String object.
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";  // If a SQLException occurred, return an "error" content.
		}		
	
	}// End of Convert_To_String().
	/**
	 * 功能：MD5加密
	 * 参数：String
	 * 返回值：String
	 * @param str
	 * @return
	 */
	public String md5(String str) {
		MessageDigest messageDigest = null;
	
		try {
			messageDigest = MessageDigest.getInstance("MD5");
	
			messageDigest.reset();
	
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	
		byte[] byteArray = messageDigest.digest();
	
		StringBuffer md5StrBuff = new StringBuffer();
	
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}
	
	
	
	/*************************************************************************************
	 * 从数据库读出时间字段处理方法
	 * java.util.Date date = dbc.rs.getTimestamp("m_start");
		String strTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(date);
	 */
	/*************************************************************************************
	 *************************************************************************************
	 *获取系统时间，写入数据库使用如下方法： 
	 * 功能：取得系统日期
	 * 参数：NULL
	 * 返回值：String
	 */
	public String getSystemDate(){
		TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8"); 
		TimeZone.setDefault(tz);
		java.util.Date date = new java.util.Date();		
		java.sql.Date dd = new java.sql.Date(date.getTime());
		return dd.toString();
	}
 	
	/**
	 * 功能：取得系统时间
	 * 参数：NULL
	 * 返回值：String
	 */
	
	public String getSystemTime(){
		TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8"); 
		TimeZone.setDefault(tz);
		java.util.Date date = new java.util.Date();
		Time tt = new java.sql.Time(date.getTime());
		return tt.toString();
	}
	
	/**
	 * 功能：取得系统日期和时间
	 * 参数：NULL
	 * 返回值：String
	 * @return
	 */
	public String getSystemDateTime(){
		TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8"); 
		TimeZone.setDefault(tz);
		java.util.Date date = new java.util.Date();
		java.sql.Date dd = new java.sql.Date(date.getTime());
		java.sql.Time tt = new java.sql.Time(date.getTime());
		return dd.toString()+" "+tt.toString();
	}
	
	/**
	 * 功能：生成当前系统日期和时间的Oracle数据库插入语句值
	 * 参数：NULL
	 * 返回值：String
	 * @return
	 */
	public String getsqlDateTime(){
		return "to_date('"+getSystemDateTime()+"','yyyy-mm-dd hh24-mi-ss')";
	}
	
	public String getsqlDateTime(String datetime){
		
		return "to_date('"+datetime+"','yyyy-mm-dd hh24-mi-ss')";
		
	}
	
	public String StringNotEmpty(String str){
		if(str!=null && !str.equals("null") && !str.equals("NULL"))
			return str;
		else return "";
	}

	
}// End of definition of class AS.


