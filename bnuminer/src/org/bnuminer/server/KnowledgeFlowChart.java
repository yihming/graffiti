package org.bnuminer.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.bnuminer.UserSession;
import org.bnuminer.dao.ConfigsDAO;
import org.bnuminer.vo.ConfigsVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class KnowledgeFlowChart extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			// 获取任务ID
			String taskId = request.getParameter("id");
			
			// 获取当前用户ID
			UserSession user = (UserSession) request.getSession().getAttribute("UserSession");
			String user_id = String.valueOf(user.getUser_id());
			
			// 获取文件存储根目录
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("KnowledgeFlowPath");
			String path = configsvo.getContent();
			
			path = path + "/" + user_id;
			
			// 若文件夹不存在，则创建之
			if (!new File(path).isDirectory())
				new File(path).mkdirs();
			
			// 读取XML文件
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = dbfactory.newDocumentBuilder();
			FileInputStream is = new FileInputStream(path + "/" + taskId + ".chart.xml");
			Document document = builder.parse(is);
			
			XPathFactory factoryXpah = XPathFactory.newInstance();
			XPath xpath = factoryXpah.newXPath();
			XPathExpression expr = xpath.compile("/chart/task/stage");
			NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Element taskEl = (Element) nodeList.item(i);
				
				if (taskEl.getAttribute("memo") != null) {
					String memo_origin = taskEl.getAttribute("memo");
					String[] memo_items = memo_origin.split(";");
					StringBuilder memo = new StringBuilder();
					
					for (int j = 0; j < memo_items.length; ++j) {
						if (j != memo_items.length - 1) {
							memo.append(memo_items[j] + "\n");
						} else {
							memo.append(memo_items[j]);
						}
					} // End of for.
					
					taskEl.setAttribute("memo", memo.toString());
					
				} // End of if.
				
			} // End of for.
			
			StringWriter stringOut = new StringWriter();
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			DOMSource source = new DOMSource(document);
			StreamResult streamResult = new StreamResult(stringOut);
			transformer.transform(source, streamResult);
			response.setContentType("text/xml");
			response.setCharacterEncoding("utf-8");
			String result = stringOut.toString();
			stringOut.close();
			response.getWriter().write(result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} // End of doGet().
}
