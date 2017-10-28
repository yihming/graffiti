package org.bnuminer.client.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SystemDialog extends Dialog {
	private static SystemDialog instance;
	
	private TextField<String> UploadPathField;
	private TextField<String> DbTempPathField;
	private TextField<String> KnowledgeFlowPathField;
	private boolean[] flags;
	
	private List<BaseModel> selectedUserList;
	private Grid<BaseModel> userlistGrid;
	
	final CheckBoxSelectionModel<BaseModel> sm = new CheckBoxSelectionModel<BaseModel>();
	
	final Label uploadPathCheck = new Label();
	final Label dbTempPathCheck = new Label();
	final Label knowledgeFlowPathCheck = new Label();
	
	public static SystemDialog getInstance() {
		if (instance == null)
			instance = new SystemDialog();
		return instance;
	}
	
	public SystemDialog() {
		setHeading("系统管理");
		setLayout(new FlowLayout());
		setModal(true);
		setClosable(false);
		setResizable(false);
		setWidth(600);
		setHeight(500);
		//setIcon();
		flags = new boolean[3];
		for (int i = 0; i < 3; ++i)
			flags[i] = true;
		
		createSystemConfigs();
		createApprovalPanel();
		updateSystemConfigs();
		
		setButtons(Dialog.CANCEL);
		getButtonById(Dialog.CANCEL).setText("关闭");
	}
	
	
	
	public void createSystemConfigs() {
		FormPanel systemconfigsPanel = new FormPanel();
		systemconfigsPanel.setHeading("文件路径管理");
		systemconfigsPanel.setFrame(true);
		
		FormData formData = new FormData("-20");
		
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(LabelAlign.TOP);
		layout.setLabelWidth(75);
		layout.setLabelSeparator("");
		
		systemconfigsPanel.setLayout(layout);
		
		// 文件上传目录
		UploadPathField = new TextField<String>();
		UploadPathField.setFieldLabel("文件上传目录");
		systemconfigsPanel.add(UploadPathField, formData);
		
		
		uploadPathCheck.setStyleAttribute("color", "red");
		uploadPathCheck.setStyleAttribute("margin-top", "5px");
		uploadPathCheck.setStyleAttribute("margin-bottom", "5px");
		uploadPathCheck.setVisible(false);
		systemconfigsPanel.add(uploadPathCheck, formData);
		
		
		UploadPathField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (UploadPathField.getValue() == null) { // 路径为空
					uploadPathCheck.setText("路径不能为空");
					uploadPathCheck.setVisible(true);
					UploadPathField.setInputStyleAttribute("border-color", "red");
					flags[0] = false;
				} else {
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.isDirectoryValid(UploadPathField.getValue(), new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) { // 路径合法且存在
								uploadPathCheck.setVisible(false);
								UploadPathField.setInputStyleAttribute("border-color", "#b5b8c8");
								flags[0] = true;
								
							} else { // 路径不合法
								uploadPathCheck.setText("路径不合法");
								uploadPathCheck.setVisible(true);
								UploadPathField.setInputStyleAttribute("border-color", "red");
								flags[0] = false;
							}
							
						}
						
					});
				}
				
			}
			
		});
		
		// 数据库临时文件目录
		DbTempPathField = new TextField<String>();
		DbTempPathField.setFieldLabel("数据库临时文件目录");
		systemconfigsPanel.add(DbTempPathField, formData);
		
		dbTempPathCheck.setStyleAttribute("color", "red");
		dbTempPathCheck.setStyleAttribute("margin-top", "5px");
		dbTempPathCheck.setStyleAttribute("margin-bottom", "5px");
		dbTempPathCheck.setVisible(false);
		systemconfigsPanel.add(dbTempPathCheck, formData);
		
		DbTempPathField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (DbTempPathField.getValue() == null) { // 路径为空
					dbTempPathCheck.setText("路径不能为空");
					dbTempPathCheck.setVisible(true);
					DbTempPathField.setInputStyleAttribute("border-color", "red");
					flags[1] = false;
					
				} else {
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.isDirectoryValid(DbTempPathField.getValue(), new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) { // 路径合法
								dbTempPathCheck.setVisible(false);
								DbTempPathField.setInputStyleAttribute("border-color", "#b5b8c8");
								flags[1] = true;
								
							} else { // 路径不合法
								dbTempPathCheck.setText("路径不合法");
								dbTempPathCheck.setVisible(true);
								DbTempPathField.setInputStyleAttribute("border-color", "red");
								flags[1] = false;
								
							}
							
						}
						
					});
				}
				
			}
			
		});
		
		
		// 挖掘模块配置文件目录
		KnowledgeFlowPathField = new TextField<String>();
		KnowledgeFlowPathField.setFieldLabel("挖掘模块配置文件目录");
		systemconfigsPanel.add(KnowledgeFlowPathField, formData);
		
		knowledgeFlowPathCheck.setStyleAttribute("color", "red");
		knowledgeFlowPathCheck.setStyleAttribute("margin-top", "5px");
		knowledgeFlowPathCheck.setStyleAttribute("margin-bottom", "5px");
		knowledgeFlowPathCheck.setVisible(false);
		systemconfigsPanel.add(knowledgeFlowPathCheck, formData);
		
		KnowledgeFlowPathField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (KnowledgeFlowPathField.getValue() == null) { // 路径为空
					knowledgeFlowPathCheck.setText("路径不能为空");
					knowledgeFlowPathCheck.setVisible(true);
					KnowledgeFlowPathField.setInputStyleAttribute("border-color", "red");
					flags[2] = false;
					
				} else {
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.isDirectoryValid(KnowledgeFlowPathField.getValue(), new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) { // 路径合法
								knowledgeFlowPathCheck.setVisible(false);
								KnowledgeFlowPathField.setInputStyleAttribute("border-color", "#b5b8c8");
								flags[2] = true;
								
							} else { // 路径不合法
								knowledgeFlowPathCheck.setText("路径不合法");
								knowledgeFlowPathCheck.setVisible(true);
								KnowledgeFlowPathField.setInputStyleAttribute("border-color", "red");
								flags[2] = false;
							
							}
							
						}
						
					});
				}
				
			}
			
		});
		
		Button okButton = new Button("确定");
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (allFieldsValid()) {
					if (Window.confirm("确认更新路径吗？该操作移动路径下的所有文件至新目录下。")) {
						BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
						service.changeConfigs(UploadPathField.getValue().trim(), DbTempPathField.getValue().trim(), KnowledgeFlowPathField.getValue().trim(), new AsyncCallbackEx<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								if (result.booleanValue() == true) {
									updateSystemConfigs();
									MessageBox.info("成功", "更新系统路径成功！", null);
								} else {
									MessageBox.info("错误", "更新配置信息时，发生后台错误", null);
								}
								
							}
							
						});
					}
					
					
				} else {
					Info.display("错误", "表单中存在错误");
				}
				
			}
			
		});
		
		Button resetButton = new Button("默认");
		resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
				service.getDefaultConfigs(new AsyncCallbackEx<Map<String, String>>() {

					@Override
					public void onSuccess(Map<String, String> result) {
						if (!result.isEmpty()) {
							
							UploadPathField.setValue(result.get("UploadPath"));
							DbTempPathField.setValue(result.get("DbTempPath"));
							KnowledgeFlowPathField.setValue(result.get("KnowledgeFlowPath"));
							
							
							
						} else {
							MessageBox.info("错误", "配置文件打开错误或不存在", null);
						}
						
					}
					
				});
				
				
				
			}
			
		});
		
		resetButton.setToolTip("恢复最初默认路径");
		
		systemconfigsPanel.getButtonBar().add(okButton);
		systemconfigsPanel.getButtonBar().add(resetButton);
		
		add(systemconfigsPanel);
		
	}
	
	public void createApprovalPanel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		
		sm.setSelectionMode(SelectionMode.MULTI);
		
		configs.add(sm.getColumn());
		
		ColumnConfig column = new ColumnConfig();
		column.setId("user_name");
		column.setHeader("用户名");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("user_true_name");
		column.setHeader("真实姓名");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("user_email");
		column.setHeader("E-mail地址");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("user_signup");
		column.setHeader("注册时间");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("user_pri");
		column.setHeader("用户状态");
		column.setWidth(100);
		configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);
		
		RpcProxy<List<BaseModel>> proxy = new RpcProxy<List<BaseModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<BaseModel>> callback) {
				BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
				service.getUserlist(callback);
				
			}
			
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		ListStore<BaseModel> userlistStore = new ListStore<BaseModel>(loader);
		
		userlistStore.getLoader().load();
		
		ContentPanel cp = new ContentPanel();
		cp.setHeading("用户管理");
		cp.setFrame(true);
		cp.setLayout(new FitLayout());
		cp.setSize(600, 200);
		
		userlistGrid = new Grid<BaseModel>(userlistStore, cm);
		userlistGrid.setSelectionModel(sm);
		userlistGrid.setAutoExpandColumn("user_signup");
		userlistGrid.setBorders(true);
		userlistGrid.addPlugin(sm);
		
		sm.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
				selectedUserList = se.getSelection();
				
			}
			
		});
		
		cp.add(userlistGrid);
		add(cp);
		
		Button approvalButton = new Button("审批通过");
		approvalButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (checkSelectedUsersPri()) { // 选中者皆为待审用户
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.approvalUsers(selectedUserList, new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) {
								userlistGrid.getStore().getLoader().load();
							} else {
								Window.alert("糟糕！审批过程中发生后台错误！");
								userlistGrid.getStore().getLoader().load();
							}
							
						}
						
					});
				}
				
			}
			
		});
		
		Button deleteButton = new Button("删除用户");
		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (selectedUserList.size() == 0) {
					Info.display("提示", "请首先选择至少一个用户");
					
				} else if (Window.confirm("确认删除吗？")) {
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.removeUsers(selectedUserList, new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue()) {
								userlistGrid.getStore().getLoader().load();
							} else {
								Window.alert("糟糕！执行过程中发生后台错误！");
								userlistGrid.getStore().getLoader().load();
							}
							
						}
						
					});
				}
				
			}
			
		});
		
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.getButtonBar().add(approvalButton);
		cp.getButtonBar().add(deleteButton);
		
		
		
	}

	
	public void updateSystemConfigs() {
		BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
		service.getAllConfigs(new AsyncCallbackEx<Map<String, String>>() {

			@Override
			public void onSuccess(Map<String, String> result) {
				UploadPathField.setValue(result.get("UploadPath"));
				DbTempPathField.setValue(result.get("DbTempPath"));
				KnowledgeFlowPathField.setValue(result.get("KnowledgeFlowPath"));
				
			}
			
		});
	} // End of init().
	
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.CANCEL.equals(button.getItemId())) {
			reset();
			hide();
		}
	}
	
	public void reset() {
		updateSystemConfigs();
		uploadPathCheck.setVisible(false);
		dbTempPathCheck.setVisible(false);
		knowledgeFlowPathCheck.setVisible(false);
	}
	
	public boolean allFieldsValid() {
		boolean result = false;
		int count = 0;
		
		for (int i = 0; i < flags.length; ++i) {
			if (flags[i] == true)
				++count;
		}
		
		if (count == flags.length)
			result = true;
		
		return result;
	}
	
	public boolean checkSelectedUsersPri() {
		boolean flag = true;
		
		for (int i = 0; i < selectedUserList.size(); ++i) {
			if ("user".equals(selectedUserList.get(i).get("user_pri")))
				flag = false;
		}
		
		return flag;
	}

}
