package org.bnuminer.client.dialog;

import java.util.ArrayList;
import java.util.List;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileManageDialog extends Dialog {
	private static FileManageDialog instance;
	
	final CheckBoxSelectionModel<BaseModel> sm = new CheckBoxSelectionModel<BaseModel>();
	final SimpleComboBox<String> type = new SimpleComboBox<String>();
	
	private Grid<BaseModel> filelistGrid;
	
	private List<BaseModel> selectedItemList;
	
	private Listener<BaseEvent> listupdateListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			filelistGrid.getStore().getLoader().load();
			
		}
		
	};
	
	public static FileManageDialog getInstance() {
		if (instance == null)
			instance = new FileManageDialog();
		return instance;
	}
	
	public FileManageDialog() {
		setHeading("在线文档管理");
		setLayout(new FitLayout());
		setResizable(false);
		setClosable(false);
		setModal(true);
		setHeight(500);
		setWidth(600);
		
		createGrid();
		
		BnuMiner.getCurrent().addAppListener(AppEvents.UploadSuccess, listupdateListener);
		
		setButtons(Dialog.CLOSE);
		getButtonById(Dialog.CLOSE).setText("关闭");
	}
	
	public void createGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		
		configs.add(sm.getColumn());
		
		ColumnConfig column = new ColumnConfig();
		column.setId("file_name");
		column.setHeader("文件名");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("shared_users");
		column.setHeader("分享给");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("file_size");
		column.setHeader("文件大小");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("file_type");
		column.setHeader("文件类型");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("file_create_time");
		column.setHeader("创建时间");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("file_modify_time");
		column.setHeader("修改时间");
		column.setWidth(100);
		configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);
		
		RpcProxy<List<BaseModel>> proxy = new RpcProxy<List<BaseModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<BaseModel>> callback) {
				BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
				service.getEditableFilelist(callback);
			}
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		ListStore<BaseModel> filelistStore = new ListStore<BaseModel>(loader);
		
		filelistStore.getLoader().load();
		
		final ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setFrame(true);
		cp.setLayout(new FitLayout());
		cp.setSize(600, 300);
		
		filelistGrid = new Grid<BaseModel>(filelistStore, cm);
		filelistGrid.setSelectionModel(sm);
		filelistGrid.setBorders(true);
		filelistGrid.addPlugin(sm);
		
		sm.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
				selectedItemList = se.getSelection();
				
			}
			
		});
		
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("选择模式："));
		
		type.setTriggerAction(TriggerAction.ALL);
		type.setEditable(false);
		type.setFireChangeEventOnSetValue(true);
		type.setWidth(100);
		type.add("多选");
		type.add("单选");
		type.setSimpleValue("多选");
		type.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				boolean single = type.getSimpleValue().equals("单选");
				sm.deselectAll();
				sm.setSelectionMode(single ? SelectionMode.SINGLE : SelectionMode.MULTI);
				
			}
			
		});
		
		toolBar.add(type);
		toolBar.add(new SeparatorToolItem());
		
		Button shareButton = new Button("分享...");
		shareButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if (sm.getSelectionMode() != SelectionMode.SINGLE) {
					MessageBox.info("提示", "请切换成单选模式", null);
				} else if (sm.getSelectedItem() == null) {
					MessageBox.info("提示", "请选择文件", null);
				} else {
					BaseModel selectedItem = sm.getSelectedItem();
					int file_id = selectedItem.get("file_id");
					ShareDialog.getInstance().initSelectedUsers(file_id);
					ShareDialog.getInstance().show();
				}
				
			}
			
		});
		
		
		Button deleteButton = new Button("删除");
		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if (Window.confirm("确认删除吗？")) {
					
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.deleteFiles(selectedItemList, new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) { // 删除成功
								filelistGrid.getStore().getLoader().load();
							} else {
								Window.alert("哎呀，糟糕！删除过程中出现后台错误！");
								filelistGrid.getStore().getLoader().load();
							}
							
						}
						
					});
				}
				
				
				
			}
			
		});
		
		Button downloadButton = new Button("下载");
		downloadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (sm.getSelectionMode() == SelectionMode.MULTI) {
					MessageBox.info("提示", "请首先切换到单选模式", null);
				} else if (sm.getSelectedItem() == null) {
					MessageBox.info("提示", "请选择文件", null);
				} else {
					int file_id = sm.getSelectedItem().get("file_id");
					Window.open(GWT.getHostPageBaseURL() + "bnuminer/download?id=" + String.valueOf(file_id), "", "");
				}
				
			}
			
		});
		
		Button refreshButton = new Button("刷新");
		refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				filelistGrid.getStore().getLoader().load();
				
			}
			
		});
		
		
		toolBar.add(shareButton);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(deleteButton);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(downloadButton);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(refreshButton);
		toolBar.add(new SeparatorToolItem());
		
		cp.setTopComponent(toolBar);
		
		
		
		cp.add(filelistGrid);
		add(cp);
	}
	
	public void reset() {
		sm.deselectAll();
		type.setSimpleValue("多选");
	}
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.CLOSE.equals(button.getItemId())) {
			reset();
			hide();
		}
	}
	
	public void reload() {
		filelistGrid.getStore().getLoader().load();
	}
}
