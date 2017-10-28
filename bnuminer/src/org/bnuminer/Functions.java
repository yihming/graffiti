package org.bnuminer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;

/**
 * <p>系统全局所使用到的函数
 * @author John Yung
 *
 */
public class Functions {
	
	
	/**
	 * <p>功能：生成MD5码
	 * @param base - String对象
	 * @return String - base所对应的MD5码
	 */
	public static String md5(String base) {
		MessageDigest messageDigest = null;
		
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(base.getBytes("UTF-8"));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] byteArray = messageDigest.digest();
		
		StringBuffer md5StrBuff = new StringBuffer();
		
		for (int i = 0; i < byteArray.length; ++i) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF &byteArray[i]));
			else 
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		
		return md5StrBuff.toString();
		
	} // End of md5().
	
	
	/**
	 * 功能：获取系统当前日期与实践
	 * @param 无
	 * @return String
	 */
	public static String getCurrentDatetime() {
		TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
		TimeZone.setDefault(tz);
		java.util.Date date = new java.util.Date();
		java.sql.Date dd = new java.sql.Date(date.getTime());
		java.sql.Time tt = new java.sql.Time(date.getTime());
		return dd.toString() + " " + tt.toString();
		
	} // End of getCurrentDatetime().
	
	/**
	 * 功能：移动原目录下的所有文件至新目录中
	 * @param oldDirectory 原目录路径
	 * @param newDirectory 新目录路径
	 */
	public static void moveAllFiles(String oldDirectory, String newDirectory) {
		
		File oldFile = new File(oldDirectory);
		File newFile = new File(newDirectory);
		
		// 若新路径不存在，则创建之
		if (!newFile.exists())
			newFile.mkdirs();
		
		// 获取原路径下的所有文件（夹）
		File[] childrenFiles = oldFile.listFiles();
		for (int i = 0; i < childrenFiles.length; ++i) {
			
			if (!childrenFiles[i].isDirectory()) {
				// 文件，移动之
				File destFile = new File(newDirectory + "/" + childrenFiles[i].getName());
				childrenFiles[i].renameTo(destFile);
				
			} else {
				
				// 文件夹，迭代移动其内容
				moveAllFiles(childrenFiles[i].getPath(), newFile.getPath() + "/" + childrenFiles[i].getName());
			}
			
			// 删除文件或文件夹
			childrenFiles[i].delete();
		} // End of for.
		
		// 删除该目录
		oldFile.delete();
		
	} // End of moveAllFiles().
	
	
}
