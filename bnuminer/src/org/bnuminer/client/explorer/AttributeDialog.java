package org.bnuminer.client.explorer;

import java.util.ArrayList;
import java.util.List;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.service.PrepService;
import org.bnuminer.client.service.PrepServiceAsync;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AttributeDialog extends Dialog {
	private static AttributeDialog instance;
	private final CheckBoxSelectionModel<BaseModel> sm = new CheckBoxSelectionModel<BaseModel>();
	private Grid<BaseModel> attributesGrid;
	
	private List<BaseModel> selectedAttrList;
	
	private Listener<BaseEvent> changeListListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			selectedAttrList = null;
			attributesGrid.getStore().getLoader().load();
			
		}
		
	};
	
	public static AttributeDialog getInstance() {
		if (instance == null)
			instance = new AttributeDialog();
		
		return instance;
	}
	
	public AttributeDialog() {
		setHeading("选择属性");
		setFrame(true);
		setModal(true);
		setWidth(400);
		setHeight(500);
		setResizable(false);
		setClosable(false);
		
		createPanel();
		initSelection();
		
		BnuMiner.getCurrent().addAppListener(AppEvents.OpenSuccess, changeListListener);
		
		setButtons(Dialog.CLOSE);
		getButtonById(Dialog.CLOSE).setText("关闭");
	}
	
	public void createPanel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		configs.add(sm.getColumn());
		
		ColumnConfig column = new ColumnConfig();
		column.setId("attrNo");
		column.setHeader("序号");
		column.setWidth(50);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("attrName");
		column.setHeader("名称");
		column.setWidth(100);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("attrType");
		column.setHeader("类型");
		column.setWidth(100);
		configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);
		
		ContentPanel mainPanel = new ContentPanel();
		mainPanel.setHeaderVisible(false);
		mainPanel.setFrame(true);
		mainPanel.setLayout(new FitLayout());
		mainPanel.setSize(400, 400);
		
		RpcProxy<List<BaseModel>> proxy = new RpcProxy<List<BaseModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<BaseModel>> callback) {
				PrepServiceAsync service = GWT.create(PrepService.class);
				service.getAttributeList(ExplorerWindow.getInstance().getCurrentOpenMode(), ExplorerWindow.getInstance().getChosenFileId(), callback);
				
			}
			
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		ListStore<BaseModel> attributeStore = new ListStore<BaseModel>(loader);
		
		attributeStore.getLoader().load();
		
		attributesGrid = new Grid<BaseModel>(attributeStore, cm);
		attributesGrid.setAutoExpandColumn("attrName");
		attributesGrid.setSelectionModel(sm);
		attributesGrid.setBorders(true);
		attributesGrid.addPlugin(sm);
		attributesGrid.setHeight(400);
		
		sm.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
				
				selectedAttrList = se.getSelection();
			}
			
		});
		
		
		
		mainPanel.add(attributesGrid);
		
		add(mainPanel);
		
	} // End of createPanel().
	
	public String[] getSelectedAttributes() {
		String[] result;
		
		if (selectedAttrList == null) {
			result = null;
		} else {
			result = new String[selectedAttrList.size()];
			for (int i = 0; i < selectedAttrList.size(); ++i) {
				result[i] = selectedAttrList.get(i).get("attrName");
			}
		}
	
		return result;
	}
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.CLOSE.equals(button.getItemId())) {
			hide();
		}
	}
	
	public void initSelection() {
		if (selectedAttrList != null) {
			for (int i = 0; i < selectedAttrList.size(); ++i) {
				for (int j = 0; j < attributesGrid.getStore().getModels().size(); ++j) {
					if (selectedAttrList.get(i).get("attrIndex").equals(attributesGrid.getStore().getModels().get(j).get("attrIndex"))) {
						sm.select(attributesGrid.getStore().getModels().get(j), true);
					}
				}
			}
		}
	} // End of initSelection().
	
	
}
