package org.trajectory;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Config {

	String data_root_path;
	
	public Config() {
		InputStream is = null;
		
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = dbfactory.newDocumentBuilder();
			is = getClass().getResourceAsStream("/conf/config.xml");
			Document document = builder.parse(is);
			document.getDocumentElement().normalize();
			Element rootElement = document.getDocumentElement();
			
			NodeList configList = rootElement.getElementsByTagName("Data-Root-Path");
			Node curNode = configList.item(0);
			
			if (curNode.getNodeType() == Node.ELEMENT_NODE) {
				Element curElem = (Element) curNode;
				
				data_root_path = curElem.getTextContent().trim();
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // End of Config().
	
	
	public String getDataRootPath() {
		return data_root_path;
	}
	
}
