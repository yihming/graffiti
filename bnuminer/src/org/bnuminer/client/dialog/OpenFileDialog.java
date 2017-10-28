package org.bnuminer.client.dialog;

import java.util.ArrayList;
import java.util.List;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.explorer.ClusterPanel;
import org.bnuminer.client.explorer.ExplorerWindow;
import org.bnuminer.client.flow.StudentFlow;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OpenFileDialog extends Dialog {
	private static OpenFileDialog instance;
	private Grid<BaseModel> filelistGrid;
	private int chosenFileId;
	private String chosenFileName;
	
	/**
	 * 工作模式
	 * -1 - 初始化
	 * 0 - ExplorerWindow模式
	 * 1 - StudentFlow模式之选取数据集
	 * 2 - Cluster界面之选择测试数据集
	 */
	private int workMode = -1;
	
	private Listener<BaseEvent> updateFilelistListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			
			filelistGrid.getStore().getLoader().load();
		}
		
	};
	
	public static OpenFileDialog getInstance() {
		if (instance == null)
			instance = new OpenFileDialog();
		return instance;
	}
	
	public OpenFileDialog() {
		// 初始化
		chosenFileId = -1;  // 未选择文件
		setHeading("打开文件");
		setLayout(new FitLayout());
		setModal(true);
		setResizable(false);
		setClosable(false);
		setWidth(400);
		setHeight(300);
		
		BnuMiner.getCurrent().addAppListener(AppEvents.UploadSuccess, updateFilelistListener);
		BnuMiner.getCurrent().addAppListener(AppEvents.LoginSuccess, updateFilelistListener);
		
		
		// 读取并存储文件列表
		RpcProxy<List<BaseModel>> proxy = new RpcProxy<List<BaseModel>>() {
			@Override
			protected void load(Object loadConfig, AsyncCallback<List<BaseModel>> callback) {
				BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
				service.getSimpleFilelist(callback);
			}

			
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		ListStore<BaseModel> filelistStore = new ListStore<BaseModel>(loader);
		
		filelistStore.getLoader().load();
		
		
		List<ColumnConfig> columnList = new ArrayList<ColumnConfig>();
		columnList.add(new ColumnConfig("file_name", "名称", 100));
		columnList.add(new ColumnConfig("owner_name", "所有者", 100));
		columnList.add(new ColumnConfig("file_size", "大小", 100));
		columnList.add(new ColumnConfig("file_type", "类型", 100));
		columnList.add(new ColumnConfig("file_create_time", "创建时间", 200));
		columnList.add(new ColumnConfig("file_modify_time", "修改时间", 200));
		ColumnModel cm = new ColumnModel(columnList);
		
		filelistGrid = new Grid<BaseModel>(filelistStore, cm);
		
		
		
		GridSelectionModel<BaseModel> gsm = new GridSelectionModel<BaseModel>() {
			protected void onSelectChange(BaseModel model, boolean select) {
				super.onSelectChange(model, select);
				if (select) {
					OpenFileDialog.getInstance().setChosenFileId(model.get("file_id").toString());
					OpenFileDialog.getInstance().setChosenFileName(model.get("file_name").toString());
				}
			}
		};
		
		filelistGrid.setSelectionModel(gsm);
		add(filelistGrid);
		
		//System.out.println(filelistGrid.getStore().getModels().get(0).get("owner_name"));
		
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("打开");
		getButtonById(Dialog.CANCEL).setText("取消");
		
	} // End of OpenFileDialog().
	
	
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) {
			if (chosenFileId != -1) { // 选择了文件
				
				if (workMode == 0) { // 在ExplorerWindow模式下工作
					
					ExplorerWindow.getInstance().setOpenFileMode();
					ExplorerWindow.getInstance().setChosenFileId(getChosenFileId());
					BnuMiner.getCurrent().fireAppEvent(AppEvents.OpenSuccess, null);
					
				
				} else if (workMode == 1) { // 在学生信息挖掘模块下工作，选取数据集
					
					StudentFlow.getInstance().setDatasetFileId(getChosenFileId());
					StudentFlow.getInstance().setDatasetFileName(getChosenFileName());
					
				} else if (workMode == 2) { // 在Cluster界面下工作，选择测试数据集
					ClusterPanel.getInstance().setTestFileId(getChosenFileId());
					ClusterPanel.getInstance().setTestFileName(getChosenFileName());
					
				}
				
				hide();
				
			} else { // 未选择文件
				MessageBox.info("错误", "请点击选择一个文件", null);
				
			}
		} else if (Dialog.CANCEL.equals(button.getItemId())) {
			hide();
		}
	}
	
	public int getChosenFileId() {
		return chosenFileId;
	}
	
	public void setChosenFileId(int chosenFileId) {
		this.chosenFileId = chosenFileId;
	}
	
	public void setChosenFileId(String chosenFileId) {
		this.chosenFileId = Integer.valueOf(chosenFileId);
	}
	
	public String getChosenFileName() {
		return chosenFileName;
	}
	
	public void setChosenFileName(String chosenFileName) {
		this.chosenFileName = chosenFileName;
	}
	

	public void show(String flag) {
		if ("ExplorerWindow".equals(flag)) { // ExplorerWindow调用OpenFileDialog
			workMode = 0;
			super.show();
		} else if ("StudentFlow_DataSet".equals(flag)) { // StudentFlow界面，选取训练集
			workMode = 1;
			super.show();
		} else if ("ClusterTestDataset".equals(flag)) { // 聚类界面中选择测试数据集
			workMode = 2;
			super.show();
			
		}
	} // End of show(String).
}
