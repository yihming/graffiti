package org.bnuminer.client.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.dialog.DatabaseConnectionDialog;
import org.bnuminer.client.service.PrepService;
import org.bnuminer.client.service.PrepServiceAsync;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ConnectionPanel extends ContentPanel {
	public final static String HISTORY_NAME = "connection";
	private static ConnectionPanel current;
	
	/**
	 * 数据库连接详细信息对话框
	 */
	protected DatabaseConnectionDialog m_DbDialog;
	
	/**
	 * 数据库连接信息——URL，用户名，密码
	 */
	protected String m_URL = "";
	protected String m_User = "";
	protected String m_Password = "";
	
	/**
	 * 界面控件
	 */
	protected Label m_LabelURL = new Label("URL ");
	protected TextField<String> m_TextURL;
	protected Button m_ButtonDatabase = new Button("用户...");
	protected Button m_ButtonConnect = new Button("连接");
	protected Button m_ButtonHistory = new Button("历史记录...");
	
	/**
	 * 历史记录
	 */
	protected List<String> m_History;
	
	/**
	 * 若连接数据库测试成功，则添加该次连接信息至历史信息中
	 */
	private Listener<BaseEvent> addHistoryListener = new Listener<BaseEvent>(){

		@Override
		public void handleEvent(BaseEvent be) {
			addHistory(getm_User() + "@" + getm_URL());
			
		}
		
	};
	
	/**
	 * 构造函数
	 */
	public ConnectionPanel() {
		current = this;
		
		m_TextURL = new TextField<String>();
		m_History = new ArrayList<String>();
		
		/**
		 * 初始化数据库连接信息
		 */
		PrepServiceAsync service = GWT.create(PrepService.class);
		service.db_init_configs(new AsyncCallbackEx<Map<String, String>>() {

			@Override
			public void onSuccess(Map<String, String> result) {
				m_URL = result.get("database_url");
				m_User = result.get("user_name");
				m_Password = result.get("password");
				
			}
			
		});
		
		// 创建面板
		createPanel();
		
		// 添加响应
		BnuMiner.getCurrent().addAppListener(AppEvents.DbConnectSuccess, addHistoryListener);
	}
	
	/**
	 * 创建面板
	 */
	public void createPanel() {
		ContentPanel panel;
		ContentPanel panel2;
		
		setLayout(new BorderLayout());
		panel2 = new ContentPanel(new FlowLayout());
		add(panel2, new BorderLayoutData(LayoutRegion.WEST));
		
		// Label.
		panel2.add(m_LabelURL);
		
		// edit field
		m_TextURL.setWidth(40);
		m_TextURL.setValue(m_URL);
		panel2.add(m_TextURL);
		
		// buttons
		panel = new ContentPanel(new FlowLayout());
		panel2.add(panel);
		
		panel.add(m_ButtonDatabase);
		
		m_ButtonConnect.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				connect();
				
			}
			
		});
		panel.add(m_ButtonConnect);
		
		m_ButtonHistory.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				showHistory();
				
			}
			
		});
		panel.add(m_ButtonHistory);
		
		setButtons();
	}
	
	/**
	 * 设置各按钮是否可用
	 */
	public void setButtons() {
		boolean isEmpty;
		
		isEmpty = m_TextURL.getValue().equals("");
		m_ButtonConnect.setEnabled(!isEmpty);
		m_ButtonDatabase.setEnabled(!isEmpty);
		m_ButtonHistory.setEnabled(m_History.size() > 0);
	}
	
	/**
	 * 重置数据库连接信息
	 */
	public void clear() {
		PrepServiceAsync service = GWT.create(PrepService.class);
		service.db_init_configs(new AsyncCallbackEx<Map<String, String>>() {

			@Override
			public void onSuccess(Map<String, String> result) {
				setm_URL(result.get("database_url"));
				setm_User(result.get("user_name"));
				setm_Password(result.get("password"));
				
			}
			
		});
		
	} // End of clear().
	
	/**
	 * 设置焦点
	 */
	public void setFocus() {
		m_TextURL.focus();
	}
	
	/**
	 * 添加历史记录
	 * @param item - 记录内容
	 */
	public void addHistory(String item) {
		if (item.equals(""))
			return;
		
		if (m_History.contains(item)) 
			m_History.remove(item);
		
		m_History.add(0, item);
		
		// Send notification.
		
	}
	
	/**
	 * 设置历史记录内容
	 * @param history - 历史记录对象
	 */
	public void setHistory(List<String> history) {
		m_History.clear();
		
		for (int i = 0; i < history.size(); ++i) {
			m_History.add(history.get(i));
			
		}
		
		setButtons();
	}
	
	/**
	 * 获取历史记录对象
	 * @return
	 */
	public List<String> getHistory() {
		return m_History;
	}
	
	
	/**
	 * 设置数据库URL
	 * @param m_URL - URL地址
	 */
	public void setm_URL(String m_URL) {
		this.m_URL = m_URL;
	}
	
	/**
	 * 获取数据库URL
	 * @return
	 */
	public String getm_URL() {
		return m_URL;
	}
	
	/**
	 * 设置数据库用户名
	 * @param m_User - 用户名
	 */
	public void setm_User(String m_User) {
		this.m_User = m_User;
	}
	
	/**
	 * 获取数据库用户名
	 * @return
	 */
	public String getm_User() {
		return m_User;
	}
	
	/**
	 * 设置数据库连接密码
	 * @param m_Password - 连接密码
	 */
	public void setm_Password(String m_Password) {
		this.m_Password = m_Password;
	}
	
	/**
	 * 获取数据库连接密码
	 * @return
	 */
	public String getm_Password() {
		return m_Password;
	}
	
	/**
	 * 根据详细信息对话框之内容，设置数据库连接信息
	 */
	public void showDialog() {
		m_DbDialog = new DatabaseConnectionDialog(getm_URL(), getm_User());
		m_DbDialog.show();
		if (m_DbDialog.getReturnValue() == 1) { // OK
			setm_URL(m_DbDialog.getURL());
			setm_User(m_DbDialog.getUsername());
			setm_Password(m_DbDialog.getPassword());
		}
		
		setButtons();
	}
	
	/**
	 * 连接数据库
	 */
	public void connect() {
		PrepServiceAsync service = GWT.create(PrepService.class);
		service.db_connect_test(getm_URL(), getm_User(), getm_Password(), new AsyncCallbackEx<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result.booleanValue()) { // 连接测试成功
					BnuMiner.getCurrent().fireAppEvent(AppEvents.DbConnectSuccess, null);
				}
				
			}
			
		});
	} // End of connect().
	
	/**
	 * 显示历史记录信息
	 */
	public void showHistory() {
		
		Dialog historyDialog = new Dialog() {
			@Override
			public void onButtonPressed(Button button) {
				if (Dialog.OK.equals(button.getItemId())) { // OK
					ConnectionPanel.getCurrent().showDialog();
					hide();
				} else if (Dialog.CANCEL.equals(button.getItemId())) { // CANCEL
					hide();
				}
				
				ConnectionPanel.getCurrent().setButtons();
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
				service.get_History(m_History, "connect", callback);
				
			}
			
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		ListStore<BaseModel> historyStore = new ListStore<BaseModel>(loader);
		
		historyStore.getLoader().load();
		
		List<ColumnConfig> columnList = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig("content", "连接记录", 100);
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
						ConnectionPanel.getCurrent().setm_URL((String)model.get("url"));
						ConnectionPanel.getCurrent().setm_User((String)model.get("user"));
						
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
	
	/**
	 * 获取当前ConnectionPanel对象
	 * @return
	 */
	public static ConnectionPanel getCurrent() {
		return current;
	}
}
