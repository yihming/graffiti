package org.bnuminer;
/**
 * r47: 实现从XML配置文档中读取数据库连接信息之功能
 */
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Config {
	// MySQL数据库访问驱动Class
	String dbDriver;
	// 数据库URL，同时设置访问编码为UTF-8
	String dbURL;
	// 登录用户名
	String userName;
	// 登录密码
	String passWord;
	
	public Config() {
		InputStream is = null;
		
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			// 读取配置文档内容
			builder = dbfactory.newDocumentBuilder();
			is = getClass().getResourceAsStream("/conf/config.xml");
			Document document = builder.parse(is);
			document.getDocumentElement().normalize();
			Element rootElement = document.getDocumentElement();
			
			NodeList dbconfigList = rootElement.getElementsByTagName("database-configuration");
			Node dbconfig = dbconfigList.item(0);

			NodeList list = dbconfig.getChildNodes();
			for (int i = 0; i < list.getLength(); ++i) {
				Node curNode = list.item(i);
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) { // 筛选出有效子节点
					
					Element curElem = (Element) curNode;
					
					if ("JDBC-Driver".equals(curElem.getNodeName())) {
						
						// 获取JDBC驱动类名
						dbDriver = curElem.getTextContent().trim();
						
					} else if ("Connection-URL".equals(curElem.getNodeName())) {
						
						// 获取数据库URL地址，采用UTF-8编码方式访问
						dbURL = curElem.getTextContent().trim() + "?useUnicode=true&characterEncoding=UTF-8";
					
					} else if ("UserName".equals(curElem.getNodeName())) {
						
						// 获取用户名
						userName = curElem.getTextContent().trim();
						
					} else if ("Password".equals(curElem.getNodeName())) {
						
						// 获取密码
						passWord = curElem.getTextContent().trim();
						
					}
					
				} // End of if.
			} // End of for.
			
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	} // End of Config().
	
	/**
	 * <p>功能：通过带参数的构造函数
	 * @param dbDriver - JDBC驱动
	 * @param driverConn - 数据库URL
	 * @param userName - 用户名
	 * @param passWord - 密码
	 */
	public Config(String dbDriver, String dbURL, String userName, String passWord) {
		this.dbDriver = dbDriver;
		this.dbURL = dbURL + "?useUnicode=true&characterEncoding=UTF-8";
		this.userName = userName;
		this.passWord = passWord;
	}
	
	// 获取JDBC驱动名称
	public String getDbDriver() {
		return dbDriver;
	}
	
	// 设置JDBC驱动名称
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	
	// 获取数据库URL地址
	public String getDbURL() {
		return dbURL;
	}
	
	// 设置数据库URL地址
	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	} 
	
	// 获取用户名
	public String getUserName() {
		return userName;
	}
	
	// 设置用户名
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	// 获取密码
	public String getPassWord() {
		return passWord;
	}
	
	// 设置密码
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	public static void main(String[] args) {
		Config config = new Config();
		System.out.println(config.getDbDriver());
	}
	
}
