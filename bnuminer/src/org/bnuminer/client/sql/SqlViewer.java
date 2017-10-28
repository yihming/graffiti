package org.bnuminer.client.sql;

import java.util.Properties;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class SqlViewer extends ContentPanel {
	protected final static String HISTORY_FILE = "SqlViewerHistory.props";
	
	public final static String WIDTH = "width";
	public final static String HEIGHT = "height";
	private static SqlViewer current;
	
	protected ConnectionPanel m_ConnectionPanel;
	protected QueryPanel m_QueryPanel;
	protected ResultPanel m_ResultPanel;
	protected HorizontalPanel m_InfoPanel;
	
	private String m_URL;
	private String m_User;
	private String m_Password;
	private String m_Query;
	//private Properties m_History;
	
	public SqlViewer() {
		current = this;
		m_URL = "";
		m_User = "";
		m_Password = "";
		m_Query = "";
		//m_History = new Properties();
		
		createPanel();
	}
	
	public void createPanel() {
		ContentPanel panel;
		ContentPanel panel2;
		
		setLayout(new BorderLayout());
		
		// Connection.
		m_ConnectionPanel = new ConnectionPanel();
		panel = new ContentPanel(new BorderLayout());
		BorderLayoutData borderData = new BorderLayoutData(LayoutRegion.NORTH);
		borderData.setMargins(new Margins(0, 5, 5, 5));
		borderData.setSplit(true);
		panel.setHeaderVisible(true);
		panel.setHeading("连接");
		add(panel, borderData);
		
		borderData = new BorderLayoutData(LayoutRegion.CENTER);
		borderData.setMargins(new Margins(0, 5, 5, 5));
		panel.add(m_ConnectionPanel, borderData);
		
		// Query.
		m_QueryPanel = new QueryPanel();
		panel = new ContentPanel(new BorderLayout());
		borderData = new BorderLayoutData(LayoutRegion.CENTER);
		add(panel, borderData);
		
		panel2 = new ContentPanel(new BorderLayout());
		borderData = new BorderLayoutData(LayoutRegion.NORTH);
		borderData.setMargins(new Margins(0, 5, 5, 5));
		panel2.add(m_QueryPanel, borderData);
		panel2.setHeaderVisible(true);
		panel2.setHeading("SQL查询");
		panel.add(panel2, new BorderLayoutData(LayoutRegion.NORTH));
		
		
		// Result.
		m_ResultPanel = new ResultPanel();
		//m_ResultPanel.setQueryPanel(m_QueryPanel);
		panel2 = new ContentPanel(new BorderLayout());
		borderData = new BorderLayoutData(LayoutRegion.CENTER);
		borderData.setMargins(new Margins(0, 5, 5, 5));
		panel2.setHeaderVisible(true);
		panel2.setHeading("查询结果");
		panel2.add(m_ResultPanel, borderData);
		panel.add(panel2, new BorderLayoutData(LayoutRegion.CENTER));
		
		// Info.
		m_InfoPanel = new HorizontalPanel();
		panel = new ContentPanel(new BorderLayout());
		borderData = new BorderLayoutData(LayoutRegion.CENTER);
		borderData.setMargins(new Margins(0, 5, 5, 5));
		panel.add(m_InfoPanel, borderData);
		panel.setHeaderVisible(true);
		panel.setHeading("状态信息");
		
		add(panel, new BorderLayoutData(LayoutRegion.SOUTH));
		
	} // End of createPanel().
	
	public String getURL() {
		return m_URL;
	}
	
	public void setURL(String url) {
		this.m_URL = url;
	}
	
	public String getUser() {
		return m_User;
	}
	
	public void setUser(String user) {
		this.m_User = user;
	}
	
	public String getPassword() {
		return m_Password;
	}
	
	public void setPassword(String password) {
		this.m_Password = password;
	}
	
	public static SqlViewer getCurrent() {
		return current;
	}
}
