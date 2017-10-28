package org.bnuminer.client.sql;



import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class ResultPanel extends ContentPanel {
	private static ResultPanel current;
	private QueryPanel m_QueryPanel;
	
	private ContentPanel m_ResultPanel;
	
	private Button m_ButtonClose = new Button("关闭");
	private Button m_ButtonCloseAll = new Button("关闭所有");
	private Button m_ButtonCopyQuery = new Button("重用结果");
	private Button m_ButtonOptWidth = new Button("优化行宽");
	
	public ResultPanel() {
		m_QueryPanel = null;
		
		createPanel();
	}
	
	public void createPanel() {
		ContentPanel panel;
		ContentPanel panel2;
		ContentPanel panel3;
		ContentPanel panel4;
		
		setLayout(new BorderLayout());
		
		// query result
		m_ResultPanel = new ContentPanel();
		add(m_ResultPanel, new BorderLayoutData(LayoutRegion.CENTER));
		
		// buttons
		panel = new ContentPanel(new BorderLayout());
		add(panel, new BorderLayoutData(LayoutRegion.EAST));
		panel2 = new ContentPanel(new BorderLayout());
		panel.add(panel2, new BorderLayoutData(LayoutRegion.CENTER));
		panel3 = new ContentPanel(new BorderLayout());
		panel2.add(panel3, new BorderLayoutData(LayoutRegion.CENTER));
		panel4 = new ContentPanel(new BorderLayout());
		panel3.add(panel4, new BorderLayoutData(LayoutRegion.CENTER));
		
		panel.add(m_ButtonClose, new BorderLayoutData(LayoutRegion.NORTH));
		
		panel2.add(m_ButtonCloseAll, new BorderLayoutData(LayoutRegion.NORTH));
		
		m_ButtonCopyQuery.setToolTip("复制当前的查询结果至查询区域");
		panel3.add(m_ButtonCopyQuery, new BorderLayoutData(LayoutRegion.NORTH));
		
		m_ButtonOptWidth.setToolTip("计算当前结果各行的合适宽度");
		panel4.add(m_ButtonOptWidth, new BorderLayoutData(LayoutRegion.NORTH));
		
		// 增加按钮区的高度
		panel4.add(new Label(" "), new BorderLayoutData(LayoutRegion.CENTER));
		panel4.add(new Label(" "), new BorderLayoutData(LayoutRegion.SOUTH));
		
		setButtons();
	}
	
	public void setFocus() {
		m_ResultPanel.focus();
	}
	
	public void clear() {
		// closeAll();
	}
	
	public void setButtons() {
		/*int         index;

	    index = m_TabbedPane.getSelectedIndex();

	    m_ButtonClose.setEnabled(index > -1);
	    m_ButtonCloseAll.setEnabled(m_TabbedPane.getTabCount() > 0);
	    m_ButtonCopyQuery.setEnabled(index > -1);
	    m_ButtonOptWidth.setEnabled(index > -1);
	*/
	}
	
	
}
