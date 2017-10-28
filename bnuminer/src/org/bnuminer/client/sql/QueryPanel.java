package org.bnuminer.client.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.service.PrepService;
import org.bnuminer.client.service.PrepServiceAsync;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QueryPanel extends ContentPanel {
	public static QueryPanel current;
	
	public final static String HISTORY_NAME = "query";
	public final static String MAX_ROWS = "max_rows";
	
	private TextArea m_TextQuery;
	private Button m_ButtonExecute = new Button("执行");
	private Button m_ButtonClear = new Button("清除");
	private Button m_ButtonHistory = new Button("历史...");
	
	private boolean m_Connected;
	private List<String> m_History = new ArrayList<String>();
	
	public QueryPanel() {
		m_Connected = false;
		current = this;
		
		createPanel();
	}
	
	public static QueryPanel getCurrent() {
		return current;
	}
	
	public void createPanel() {
		ContentPanel panel;
		ContentPanel panel2;
		ContentPanel panel3;
		
		setLayout(new BorderLayout());
		
		// text area
		m_TextQuery = new TextArea();
		add(m_TextQuery, new BorderLayoutData(LayoutRegion.CENTER));
		
		// buttons
		panel = new ContentPanel();
		panel.setLayout(new BorderLayout());
		add(panel, new BorderLayoutData(LayoutRegion.EAST));
		panel.add(m_ButtonExecute, new BorderLayoutData(LayoutRegion.NORTH));
		
		panel2 = new ContentPanel(new BorderLayout());
		panel.add(panel2, new BorderLayoutData(LayoutRegion.CENTER));
		panel2.add(m_ButtonClear, new BorderLayoutData(LayoutRegion.NORTH));
		
		panel3 = new ContentPanel(new BorderLayout());
		panel2.add(panel3, new BorderLayoutData(LayoutRegion.CENTER));
		panel3.add(m_ButtonHistory, new BorderLayoutData(LayoutRegion.NORTH));
		
		// limit
		
		// Set initial state
		setButtons();
	}
	
	public void setFocus() {
		m_TextQuery.focus();
	}
	
	public void setButtons() {
		boolean isEmpty;
	    
	    isEmpty = m_TextQuery.getValue().trim().equals("");
	    
	    m_ButtonExecute.setEnabled((m_Connected) && (!isEmpty));
	    m_ButtonClear.setEnabled(!isEmpty);
	    m_ButtonHistory.setEnabled(m_History.size() > 0);	
	}
	
	/**
	 * 执行数据库查询
	 */
	public void execute() {
		PrepServiceAsync service = GWT.create(PrepService.class);
		service.db_query(SqlViewer.getCurrent().getURL(), SqlViewer.getCurrent().getUser(), SqlViewer.getCurrent().getPassword(), getQuery(), new AsyncCallbackEx<Map<String, Object>>() {

			@Override
			public void onSuccess(Map<String, Object> result) {
				// 将result返回给SqlViewer
				
			}
		
		});
		
		setButtons();
	} // End of execute().
	
	/**
	 * 重置语句输入框
	 */
	public void clear() {
		m_TextQuery.setValue("");
	}
	
	public void addHistory(String history) {
		if (history.equals(""))
			return;
		
		if (m_History.contains(history))
			m_History.remove(history);
		
		m_History.add(0, history);
	}
	
	public void setHistory(List<String> history) {
		m_History.clear();
		
		for (int i = 0; i < history.size(); ++i) {
			m_History.add(history.get(i));
		}
		
		setButtons();
	}
	
	public List<String> getHistory() {
		return m_History;
	}
	
	/**
	 * 显示查询历史记录
	 */
	public void showHistory() {
		Dialog historyDialog = new Dialog() {
			@Override
			public void onButtonPressed(Button button) {
				if (Dialog.OK.equals(button.getItemId())) { // OK
					
					hide();
				} else if (Dialog.CANCEL.equals(button.getItemId())) { // CANCEL
					hide();
				}
				
				QueryPanel.getCurrent().setButtons();
			}
		};
		
		historyDialog.setWidth(300);
		historyDialog.setHeight(400);
		historyDialog.setLayout(new FitLayout());
		historyDialog.setHeading("历史记录");
		historyDialog.setModal(true);
		historyDialog.setClosable(false);
		historyDialog.setResizable(false);
		
		// 读取并存储记录条目
		RpcProxy<List<BaseModel>> proxy = new RpcProxy<List<BaseModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<BaseModel>> callback) {
				PrepServiceAsync service = GWT.create(PrepService.class);
				service.get_History(m_History, "query", callback);
				
			}
			
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		ListStore<BaseModel> historyStore = new ListStore<BaseModel>(loader);
		
		historyStore.getLoader().load();
		
		List<ColumnConfig> columnList = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig("content", "查询历史", 100);
		columnList.add(column);
		
		ColumnModel cm = new ColumnModel(columnList);
		
		Grid<BaseModel> historyGrid = new Grid<BaseModel>(historyStore, cm);
		historyGrid.setAutoExpandColumn("content");
		
		// 选择事件
		GridSelectionModel<BaseModel> gsm = new GridSelectionModel<BaseModel>() {
			protected void onSelectChange(BaseModel model, boolean select) {
				super.onSelectChange(model, select);
				if (select) {
					if (model != null) {
						QueryPanel.getCurrent().setQuery((String)model.get("content"));
						
					}
				}
			}
		};
		
		historyGrid.setSelectionModel(gsm);
		historyDialog.add(historyGrid);
		
		historyDialog.setButtons(Dialog.OKCANCEL);
		historyDialog.getButtonById(Dialog.OK).setText("选择");
		historyDialog.getButtonById(Dialog.CANCEL).setText("取消");
		
	} // End of showHistory().
	
	public String getQuery() {
		return m_TextQuery.getValue();
	}
	
	public void setQuery(String sql) {
		m_TextQuery.setValue(sql);
	}
}
