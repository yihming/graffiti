package org.bnuminer.client.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.explorer.ExplorerWindow;
import org.bnuminer.client.service.PrepService;
import org.bnuminer.client.service.PrepServiceAsync;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;

public class SimpleOpenDbDialog extends Dialog {
	private static SimpleOpenDbDialog instance;
	private TextField<String> db_URL;
	private TextField<String> db_User;
	private TextField<String> db_Password;
	private TextArea db_query;
	
	private Button db_ButtonConnectHistory = new Button("连接历史...");
	private Button db_ButtonQueryHistory = new Button("查询历史...");
	private Button executeButton = new Button("执行");
	
	private Grid<BaseModel> dataGrid;
	
	private ScrollPanel resultPanel;
	
	public static SimpleOpenDbDialog getInstance() {
		if (instance == null) {
			instance = new SimpleOpenDbDialog();
		}
		return instance;
	}
	
	public SimpleOpenDbDialog() {
		setHeading("打开数据库");
		setModal(true);
		setResizable(false);
		setClosable(false);
		setWidth(500);
		setHeight(600);
		setLayout(new FitLayout());
		
		createPanel();
		
		initFields();
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("确定");
		getButtonById(Dialog.CANCEL).setText("取消");
	}
	
	public void createPanel() {
		
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setLabelWidth(100);
		add(panel);
		
		HorizontalPanel hPanel = new HorizontalPanel();
		db_URL = new TextField<String>();
		db_URL.setWidth(200);
		db_URL.setStyleAttribute("margin", "5 10");
		db_URL.setFieldLabel("数据库URL ");
		db_URL.setLabelSeparator("");
		hPanel.add(db_URL);
		db_ButtonConnectHistory.setStyleAttribute("margin", "5 10");
		hPanel.add(db_ButtonConnectHistory);
		panel.add(hPanel, new FormData("90%"));
		
		db_User = new TextField<String>();
		db_User.setWidth(200);
		db_User.setLabelSeparator("");
		db_User.setStyleAttribute("margin", "5 10");
		db_User.setFieldLabel("用户名 ");
		panel.add(db_User, new FormData("90%"));
		
		db_Password = new TextField<String>();
		db_Password.setFieldLabel("密码 ");
		db_Password.setWidth(200);
		db_Password.setLabelSeparator("");
		db_Password.setStyleAttribute("margin", "5 10");
		db_Password.setPassword(true);
		panel.add(db_Password, new FormData("90%"));
		
		// 查询语句
		HorizontalPanel hPanel2 = new HorizontalPanel();
		db_query = new TextArea();
		db_query.setWidth(200);
		db_query.setHeight(200);
		db_query.setLabelSeparator("");
		db_query.setStyleAttribute("margin", "5 10");
		hPanel2.add(db_query);
		
		VerticalPanel vPanel = new VerticalPanel();
		
		db_ButtonQueryHistory.setStyleAttribute("margin", "5 10");
		executeButton.setStyleAttribute("margin", "5 10");
		executeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void componentSelected(ButtonEvent ce) {
				PrepServiceAsync service = GWT.create(PrepService.class);
				service.db_query(getURL(), getUser(), getPassword(), getQuery(), new AsyncCallbackEx<Map<String, Object>>() {

					@Override
					public void onSuccess(Map<String, Object> result) {
						List<BaseModel> metaDataList = (List<BaseModel>) result.get("metaDataList");
						
						List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
						
						// 显示字段信息
						ColumnConfig column = new ColumnConfig();
						column.setId("dataNo");
						column.setHeader("序号");
						column.setWidth(50);
						configs.add(column);
						
						for (int i = 0; i < metaDataList.size(); ++i) {
							column = new ColumnConfig();
							column.setId((String)metaDataList.get(i).get("column_name"));
							column.setHeader((String)metaDataList.get(i).get("column_name"));
							column.setWidth(100);
							configs.add(column);
						}
						
						ColumnModel cm = new ColumnModel(configs);
						
						// 显示数据信息
						ListStore<BaseModel> store = new ListStore<BaseModel>();
						store.add((List<BaseModel>)result.get("dataList"));
						
						dataGrid = new Grid<BaseModel>(store, cm);
						dataGrid.setHeight(150);
						dataGrid.setBorders(true);
						
						resultPanel.add(dataGrid);
						
						
					}
					
				});
				
			}
			
		});
		
		vPanel.add(db_ButtonQueryHistory);
		vPanel.add(executeButton);
		hPanel2.add(vPanel);
		panel.add(hPanel2, new FormData("90%"));
		
		resultPanel = new ScrollPanel();
		resultPanel.setHeight("150");
		panel.add(resultPanel);
		
	}
	
	public void initFields() {
		PrepServiceAsync service = GWT.create(PrepService.class);
		service.db_init_configs(new AsyncCallbackEx<Map<String, String>>() {

			@Override
			public void onSuccess(Map<String, String> result) {
				db_URL.setValue(result.get("database_url"));
				db_User.setValue(result.get("user_name"));
				db_Password.setValue(result.get("password"));
				
			}
			
		});
	}
	
	public void reset() {
		db_URL.setValue("");
		db_User.setValue("");
		db_Password.setValue("");
		db_query.setValue("");
	}
	
	public String getURL() {
		return db_URL.getValue();
	}
	
	public String getUser() {
		return db_User.getValue();
	}
	
	public String getPassword() {
		return db_Password.getValue();
	}
	
	public String getQuery() {
		return db_query.getValue().trim();
	}
	
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) { // OK
			
			ExplorerWindow.getInstance().setOpenDbMode();
			BnuMiner.getCurrent().fireAppEvent(AppEvents.OpenSuccess, null);
			hide();
			
			
		} else if (Dialog.CANCEL.equals(button.getItemId())) { // CANCEL
			reset();
			hide();
		}
	}
}
