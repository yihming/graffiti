package org.bnuminer.client.dialog;

import java.util.ArrayList;
import java.util.List;

import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ShareDialog extends Dialog {
	private static ShareDialog instance;
	
	final CheckBoxSelectionModel<BaseModel> sm = new CheckBoxSelectionModel<BaseModel>();
	
	private int selectedFileId;
	private Grid<BaseModel> userlistGrid;
	private List<BaseModel> selectedUserList;
	
	public static ShareDialog getInstance() {
		if (instance == null)
			instance = new ShareDialog();
		return instance;
	}
	
	public ShareDialog() {
		setHeading("分享用户选择");
		setLayout(new FitLayout());
		setClosable(false);
		setResizable(false);
		setModal(true);
		setHeight(500);
		setWidth(400);
		
		createUserGrid();
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("确定");
		getButtonById(Dialog.CANCEL).setText("关闭");
	}
	
	public void createUserGrid() {
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
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
		
		ColumnModel cm = new ColumnModel(configs);
		
		RpcProxy<List<BaseModel>> proxy = new RpcProxy<List<BaseModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<BaseModel>> callback) {
				BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
				service.getValidUsers(callback);
				
			}
			
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		ListStore<BaseModel> userlistStore = new ListStore<BaseModel>(loader);
		
		userlistStore.getLoader().load();
		
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setFrame(true);
		cp.setLayout(new FitLayout());
		cp.setSize(400, 400);
		
		userlistGrid = new Grid<BaseModel>(userlistStore, cm);
		userlistGrid.setSelectionModel(sm);
		userlistGrid.setBorders(true);
		userlistGrid.addPlugin(sm);
		userlistGrid.setAutoExpandColumn("user_email");
		
		sm.setSelectionMode(SelectionMode.MULTI);
		sm.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
				selectedUserList = se.getSelection();
				
			}
			
		});
		
		cp.add(userlistGrid);
		
		add(cp);
		
	} // End of createUserGrid().
	
	/**
	 * 勾选上指定文档的分享者
	 * @param file_id 文档ID
	 */
	public void initSelectedUsers(int file_id) {
	
		setSelectedFileId(file_id);
		BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
		service.getSelectedUsers(selectedFileId, new AsyncCallbackEx<List<BaseModel>>() {

			@Override
			public void onSuccess(List<BaseModel> result) {
				
				for (int i = 0; i < result.size(); ++i) {
					
					for (int j = 0; j < userlistGrid.getStore().getModels().size(); ++j) {
						
						// 查找到分享者之ID，则勾选之
						if (result.get(i).get("user_id").equals(userlistGrid.getStore().getModels().get(j).get("user_id")))
							sm.select(userlistGrid.getStore().getModels().get(j), true);
						
					} // End of for.

					
				} // End of for.
		
				
			}
			
		});
	} // End if initSelectedUsers().
	
	public void setSelectedFileId(int file_id) {
		selectedFileId = file_id;
	}
	
	public int getSelectedFileId() {
		return selectedFileId;
	}

	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) {
			BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
			service.setSharedUsers(selectedUserList, selectedFileId, new AsyncCallbackEx<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					if (result.booleanValue()) {
						MessageBox.info("成功", "执行成功！", null);
						FileManageDialog.getInstance().reload();
					} else {
						Window.alert("糟糕！执行操作时发生后台错误！");
						userlistGrid.getStore().getLoader().load();
						initSelectedUsers(selectedFileId);
						FileManageDialog.getInstance().reload();
					}
					
				}

				
			});
		} else if (Dialog.CANCEL.equals(button.getItemId())) {
			hide();
		}
	}
}
