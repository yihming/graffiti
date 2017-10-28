package org.bnuminer.client.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.dialog.OpenFileDialog;
import org.bnuminer.client.dialog.SimpleOpenDbDialog;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;
import org.bnuminer.client.service.PrepService;
import org.bnuminer.client.service.PrepServiceAsync;
import org.bnuminer.client.widget.LoadingPanel;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ExplorerWindow extends Window {
	private static ExplorerWindow instance;
	private TabPanel main_desktop;
	private ContentPanel left_bottom;
	private ScrollPanel attributesScan;
	private Grid<BaseModel> attributesGrid;
	private VerticalPanel rightPanel;
	private ScrollPanel attributeStatScan;
	private Grid<BaseModel> attributeStatGrid;
	//private SimpleComboBox<BaseModel> visualizeAttribute;
	
	private List<BaseModel> selectedAttributesList;
	private Button remove;
	
	//private SimpleOpenDbDialog opendbDialog;
	
	protected Map<String, TabItem> explorerMap;
	protected Map<String, Label> labelMap;
	protected ChooseDialog chooseDialog;
	protected ClusterPanel clusterPanel;
	
	/**
	 * 当前所选的文件id：
	 * -1 : 当前未选择
	 * >=0 : 选择上传文件
	 */
	private int chosen_file_id = -1;
	
	/**
	 * 打开模式：
	 * 0 : 未选择
	 * 1 : 打开上传文件模式
	 * 2  ： 打开URL模式
	 * 3  ： 打开数据库模式
	 */
	private int open_mode = 0;
	
	private String clusterer_name;
	private String clusterer_options;
	private TextArea clusterer_output;
	
	// 将其他标签设置为可用
	private Listener<BaseEvent> enableListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			// TODO Auto-generated method stub
			
			ExplorerWindow.getInstance().createClusterPanel();
			getTabItem("聚类").setEnabled(true);
	
			BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
			service.openFile(OpenFileDialog.getInstance().getChosenFileId(), new AsyncCallbackEx<String>() {
				@Override
				public void onSuccess(String result) {
					
				}
			});
		}
		
	};
	
	// 统计当前关系的属性信息
	private Listener<BaseEvent> prep_statisticsListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
		
			
			
			PrepServiceAsync service = GWT.create(PrepService.class);
			service.prep_statistics(getCurrentOpenMode(), getChosenFileId(), new AsyncCallbackEx<Map<String, Object>>() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void onSuccess(Map<String, Object> result) {
					// 当前关系统计信息
					Label item = labelMap.get("preprocess_relation");
					item.setText("关系名称:" + (String)result.get("preprocess_relation"));
					
					item = labelMap.get("preprocess_attributes");
					item.setText("属性数：" + ((Integer)result.get("preprocess_attributes")).toString());
					
					item = labelMap.get("preprocess_instances");
					item.setText("步长：" + ((Integer)result.get("preprocess_instances")).toString());
					
					item = labelMap.get("preprocess_weight");
					item.setText("权重和：" + ((Double)result.get("preprocess_weight")).toString());
					
					// 属性信息
					
					if (attributesGrid != null) {
						attributesScan.remove(attributesGrid);
					}
					
					List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
					
					CheckBoxSelectionModel<BaseModel> sm = new CheckBoxSelectionModel<BaseModel>();
					
					configs.add(sm.getColumn());
					
					ColumnConfig column = new ColumnConfig();
					column.setId("attr_No");
					column.setHeader("序号");
					column.setWidth(100);
					configs.add(column);
					
					column = new ColumnConfig();
					column.setId("attr_name");
					column.setHeader("属性名称");
					column.setWidth(200);
					configs.add(column);
					
					ListStore<BaseModel> store = new ListStore<BaseModel>();
					store.add((List<BaseModel>)result.get("attr_list"));
					
					
					ColumnModel cm = new ColumnModel(configs);
					attributesGrid = new Grid<BaseModel>(store, cm);
			
					attributesGrid.setSelectionModel(sm);
					attributesGrid.setBorders(true);
					attributesGrid.setHeight(200);
					attributesGrid.addPlugin(sm);
					attributesGrid.setAutoExpandColumn("attr_name");
					
					sm.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

						@Override
						public void selectionChanged(
								SelectionChangedEvent<BaseModel> se) {
							selectedAttributesList = se.getSelection();
							
							if (se.getSelection().size() > 0) {
								remove.setEnabled(true);
								int attr_index = (Integer)selectedAttributesList.get(selectedAttributesList.size() - 1).get("attr_No") - 1;
								ExplorerWindow.getInstance().displaySelectedAttribute(attr_index);
							} else {
								remove.setEnabled(false);
							}
							
						}
						
					});
					
					
					
					attributesScan.add(attributesGrid);
					
					// 可视化
					//visualizeAttribute.add((List<BaseModel>)result.get("attr_list"));
					//visualizeAttribute.setSimpleValue(((List<BaseModel>)result.get("attr_list")).get(0));
					
				}
			});
			
			
		}
		
		
	};
	
	/**
	 * 获取ExplorerWindow之一实例
	 * @return
	 */
	public static ExplorerWindow getInstance() {
		if (instance == null)
			instance = new ExplorerWindow();
		return instance;
	}
	

	/**
	 * 构造函数
	 */
	public ExplorerWindow() {
		explorerMap = new HashMap<String, TabItem>();
		
		setHeading("算法实验");
		setModal(true);
		setSize(930, 550);
		setMaximizable(true);
		setClosable(true);
		setLayout(new FitLayout());
		
		createTabPanel();
		addTabItem("预处理", true);
		addTabItem("分类", false);
		addTabItem("聚类", false);
		addTabItem("Associate", false);
		addTabItem("特征提取", false);
		addTabItem("可视化", false);
		
		BnuMiner.getCurrent().addAppListener(AppEvents.OpenSuccess, enableListener);
		BnuMiner.getCurrent().addAppListener(AppEvents.OpenSuccess, prep_statisticsListener);
		
		addPreprocessContentPanel();
	
		
	}
	
	/**
	 * 创建标签页面板
	 */
	public void createTabPanel() {
		
		main_desktop = new TabPanel();
		labelMap = new HashMap<String, Label>();
		main_desktop.setAutoWidth(true);
		main_desktop.setAutoHeight(true);
		main_desktop.setTabScroll(true);
		add(main_desktop);
		
	}
	
	/**
	 * 创建标签页
	 * @param title 标题
	 * @param state 是否可用
	 * @return 标签页对象
	 */
	public TabItem addTabItem(String title, boolean state) {
		TabItem item = new TabItem(title);
		item.setScrollMode(Scroll.AUTO);
		item.setEnabled(state);
		explorerMap.put(title, item);
		main_desktop.add(item);
		
		return item;
	}
	
	/**
	 * 获取指定标签页
	 * @param title 指定标签页之标题
	 * @return 标签页对象
	 */
	public TabItem getTabItem(String title) {
		TabItem item = explorerMap.get(title);
		
		return item;
	}
	
	/**
	 * 创建预处理主界面
	 * @return ContentPanel面板对象
	 */
	public ContentPanel addPreprocessContentPanel() {
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setBorders(true);
		contentPanel.setScrollMode(Scroll.AUTOY);
		contentPanel.setWidth(910);
		contentPanel.setLayout(new FitLayout());
		TabItem item = explorerMap.get("预处理");
		
		VerticalPanel main_panel = new VerticalPanel();
		main_panel.setWidth("100%");
		
		// 按钮栏
		ToolBar toolbar = new ToolBar();
		toolbar.setWidth("100%");
		
		Button openFile = new Button("打开文件...");
		
		openFile.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
				
				OpenFileDialog.getInstance().show("ExplorerWindow");
			}
		});
		
		Button openUrl = new Button("打开URL...");
		openUrl.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				
			}
			
		});
		
		Button openDB = new Button("打开数据库...");
		openDB.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				SimpleOpenDbDialog.getInstance().show();
				
			}
			
		});
	
		
		Button save = new Button("保存...");
		
		
		toolbar.add(openFile);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(openUrl);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(openDB);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(save);
		
		contentPanel.setTopComponent(toolbar);
		
		
		
		// 第二行
		FormPanel second = new FormPanel();
		second.setHeading("过滤算法");
		second.setStyleAttribute("margin", "10 10 10 10");
		second.setWidth(880);
		second.setHeight(60);
		second.setLayout(new FitLayout());
		
		HorizontalPanel second_panel = new HorizontalPanel();
		second_panel.setWidth(880);
		
		Button choose = new Button("选择");
		choose.setStyleAttribute("margin", "0 5 0 0");
		
		Label filter_name = new Label();
		filter_name.setStyleAttribute("font-size", "13");
		filter_name.setStyleAttribute("padding", "3 10");
		filter_name.setStyleAttribute("background-color", "#D4D4D4");
		filter_name.setAutoWidth(false);
		filter_name.setWidth(600);
		filter_name.setText("None");
		labelMap.put("preprocess_filter", filter_name);
		
		Button apply = new Button("应用");
		apply.setEnabled(false);
		apply.setStyleAttribute("margin", "0 0 0 5");
		
		second_panel.add(choose);
		second_panel.add(filter_name);
		second_panel.add(apply);
		
		second.add(second_panel);
		
		main_panel.add(second);
		
		// 左下第一行
		HorizontalPanel thirdPanel = new HorizontalPanel();
		thirdPanel.setStyleAttribute("margin", "0 5 5 0");
		thirdPanel.setAutoWidth(false);
		thirdPanel.setWidth(435);
		
		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.setWidth(435);
		
		FormPanel left_top = new FormPanel();
		left_top.setHeading("当前关系");
		left_top.setWidth(435);
		left_top.setStyleAttribute("margin", "0 10 5 10");
		left_top.setLayout(new FitLayout());
		
		HorizontalPanel left_top_left = new HorizontalPanel();
		Label relation = new Label();
		relation.setText("关系：无");
		relation.setStyleAttribute("font-size", "13");
		relation.setStyleAttribute("margin", "0 5 0 0");
		labelMap.put("preprocess_relation", relation);
		
		Label attributes = new Label();
		attributes.setText("属性数：无");
		attributes.setStyleAttribute("font-size", "13");
		labelMap.put("preprocess_attributes", attributes);
		
		left_top_left.add(relation);
		left_top_left.add(attributes);
		
		HorizontalPanel left_top_right = new HorizontalPanel();
		Label instances = new Label();
		instances.setText("步数：无");
		instances.setStyleAttribute("font-size", "13");
		instances.setStyleAttribute("margin", "0 5 0 0");
		labelMap.put("preprocess_instances", instances);
		
		Label sum_of_weights = new Label();
		sum_of_weights.setStyleAttribute("font-size", "13");
		sum_of_weights.setText("权重和：无");
		labelMap.put("preprocess_weight", sum_of_weights);
		left_top_right.add(instances);
		left_top_right.add(sum_of_weights);
		
		left_top.add(left_top_left);
		left_top.add(left_top_right);
		
		leftPanel.add(left_top);
		
		// 左下第二行
		left_bottom = new ContentPanel();
		left_bottom.setHeading("属性");
		left_bottom.setStyleAttribute("margin", "0 10 5 10");
		left_bottom.setWidth(435);
		left_bottom.setHeight(200);
		left_bottom.setLayout(new FitLayout());
		
		
		ToolBar attributes_toolbar = new ToolBar();
		
		remove = new Button("删除选定属性");
		remove.setEnabled(false);
		
		attributes_toolbar.add(remove);
		attributes_toolbar.add(new SeparatorToolItem());
		
		left_bottom.setTopComponent(attributes_toolbar);
		
		attributesScan = new ScrollPanel();
		attributesScan.setHeight("200");
		attributesScan.setWidth("435");
		left_bottom.add(attributesScan);
		
		leftPanel.add(left_bottom);
		
		
		// 右下第一行
		rightPanel = new VerticalPanel();
		rightPanel.setWidth(435);
		rightPanel.setHeight(500);
		
		/*ContentPanel right_top = new ContentPanel();
		right_top.setHeading("所选属性");
		right_top.setWidth(435);
		right_top.setHeight(300);
		right_top.setStyleAttribute("margin", "0 10 5 0");
		right_top.setLayout(new FitLayout());
		*/
		ContentPanel right_top_top = new ContentPanel();
		right_top_top.setHeading("所选属性");
		right_top_top.setWidth(435);
		right_top_top.setBorders(false);
		right_top_top.setHeight(100);
		right_top_top.setLayout(new RowLayout(Orientation.VERTICAL));
		
		
		HorizontalPanel right_top_top_firstline = new HorizontalPanel();
		right_top_top.add(right_top_top_firstline, new RowData(1, -1, new Margins(5)));
		
		Label name = new Label();
		name.setText("名称：无");
		name.setStyleAttribute("font-size", "13");
		name.setStyleAttribute("margin", "0 30 0 0");
		labelMap.put("preprocess_attribute_name", name);
		Label type = new Label();
		type.setText("类型：无");
		type.setStyleAttribute("font-size", "13");
		labelMap.put("preprocess_attribute_type", type);
		
		right_top_top_firstline.add(name);
		right_top_top_firstline.add(type);
		
		
		HorizontalPanel right_top_top_secondline = new HorizontalPanel();
		right_top_top.add(right_top_top_secondline, new RowData(1, -1, new Margins(5)));
		
		Label missing = new Label();
		missing.setText("Missing: 无");
		missing.setStyleAttribute("font-size", "13");
		missing.setStyleAttribute("margin", "0 10 0 0");
		labelMap.put("preprocess_attribute_missing", missing);
		
		Label distinct = new Label();
		distinct.setText("不同值数目：无");
		distinct.setStyleAttribute("font-size", "13");
		distinct.setStyleAttribute("margin", "0 10 0 0");
		labelMap.put("preprocess_attribute_distinct", distinct);
		
		Label unique = new Label();
		unique.setText("Unique: 无");
		unique.setStyleAttribute("font-size", "13");
		labelMap.put("preprocess_attribute_unique", unique);
		
		right_top_top_secondline.add(missing);
		right_top_top_secondline.add(distinct);
		right_top_top_secondline.add(unique);
		
		
		// 属性详细统计信息
		ContentPanel right_top_bottom = new ContentPanel();
		right_top_bottom.setHeading("属性详细信息");
		right_top_bottom.setWidth(435);
		right_top_bottom.setHeight(200);
		right_top_bottom.setStyleAttribute("margin-top", "5px");
		right_top_bottom.setLayout(new FitLayout());
		
		attributeStatScan = new ScrollPanel();
		attributeStatScan.setHeight("150");
		
		right_top_bottom.add(attributeStatScan);
		
		rightPanel.add(right_top_top);
		rightPanel.add(right_top_bottom);
		
		
		// 右下第二行
		/*ContentPanel right_bottom = new ContentPanel();
		right_bottom.setHeading("可视化");
		right_bottom.setWidth("100%");
		right_bottom.setHeight(100);
		right_bottom.setStyleAttribute("margin", "0 10 0 0");
		right_bottom.setLayout(new FitLayout());
		
		ToolBar right_bottom_toolbar = new ToolBar();
		right_bottom_toolbar.add(new LabelToolItem("选择属性："));
		
		visualizeAttribute = new SimpleComboBox<BaseModel>();
		visualizeAttribute.setTriggerAction(TriggerAction.ALL);
		visualizeAttribute.setEditable(false);
		visualizeAttribute.setDisplayField("attr_name");
		visualizeAttribute.setEmptyText("无");
		visualizeAttribute.setFireChangeEventOnSetValue(true);
		visualizeAttribute.setWidth(100);
		visualizeAttribute.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				PrepServiceAsync service = GWT.create(PrepService.class);
				//service
				
			}
			
		});
		
		right_bottom_toolbar.add(visualizeAttribute);
		right_bottom_toolbar.add(new SeparatorToolItem());
		
		Button visualize_all = new Button("全部可视化");
		
		right_bottom_toolbar.add(visualize_all);
		
		right_bottom.setTopComponent(right_bottom_toolbar);*/
		
		
		

		//rightPanel.add(right_bottom);
		
		thirdPanel.add(leftPanel);
		thirdPanel.add(rightPanel);
		
		main_panel.add(thirdPanel);
		
		contentPanel.add(main_panel);
		item.add(contentPanel);
		
		return contentPanel;
		
	} // End of addPreprocessContentPanel().
	
	/**
	 * 创建聚类界面
	 */
	public void createClusterPanel() {
		TabItem item = explorerMap.get("聚类");
		item.setLayout(new FitLayout());
		
		clusterPanel = new ClusterPanel();
		clusterPanel.create(this);
		
		item.add(clusterPanel);
		
	}
	
	public TextArea getClusterer_output() {
		return clusterer_output;
	}
	
	public void setClusterer_output(String content) {
		clusterer_output.setValue(content);
	}
	
	public String getClustererName() {
		return clusterer_name;
	}
	
	public void setClustererName(String clusterer_name) {
		this.clusterer_name = clusterer_name;
	}
	
	public String getClustererOptions() {
		return clusterer_options;
	}
	
	public void setClustererOptions(String clusterer_options) {
		this.clusterer_options = clusterer_options;
	}
	
	
	@Override
	public void hide() {
		reset();
		super.hide();
	}
	
	/**
	 * 重置所有成员与属性值
	 */
	public void reset() {
		OpenFileDialog.getInstance().setChosenFileId(-1);
		clusterer_name = "EM";
		clusterer_options = "-I 100 -N 3 -M 1.0E-6 -S 100";
	}
	
	public Label getLabel(String title) {
		Label item = labelMap.get(title);
		
		return item;
	}
	
	public void setChosenFileId(int file_id) {
		this.chosen_file_id = file_id;
	}
	
	public int getChosenFileId() {
		return chosen_file_id;
	}
	
	/**
	 * 打开模式初始化
	 */
	public void setInitialMode() {
		open_mode = 0;
	}
	
	/**
	 * 打开上传文件模式
	 */
	public void setOpenFileMode() {
		open_mode = 1;
	}
	
	/**
	 * 打开URL模式
	 */
	public void setOpenURLMode() {
		open_mode = 2;
	}
	
	/**
	 * 打开数据库模式
	 */
	public void setOpenDbMode() {
		open_mode = 3;
	}
	
	/**
	 * 获取当前模式
	 * @return 当前模式代号
	 */
	public int getCurrentOpenMode() {
		return open_mode;
	}
	
	/**
	 * 显示选中属性的统计信息
	 * @param attribute_index 选中信息的Index值
	 */
	public void displaySelectedAttribute(int attribute_index) {
		PrepServiceAsync service = GWT.create(PrepService.class);
		service.displaySelectedAttribute(attribute_index, getCurrentOpenMode(), getChosenFileId(), new AsyncCallbackEx<Map<String, Object>>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(Map<String, Object> result) {
				
				String attributeType = (String)result.get("preprocess_attribute_type");
				
				Label label = labelMap.get("preprocess_attribute_name");
				label.setText("名称：" + (String)result.get("preprocess_attribute_name"));
				
				label = labelMap.get("preprocess_attribute_type");
				label.setText("类型：" + (String)result.get("preprocess_attribute_type"));
				
				label = labelMap.get("preprocess_attribute_distinct");
				label.setText("不同值数目：" + (String)result.get("preprocess_attribute_distinct"));
				
				label = labelMap.get("preprocess_attribute_missing");
				label.setText("Missing: " + (String)result.get("preprocess_attribute_missing"));
				
				label = labelMap.get("preprocess_attribute_unique");
				label.setText("Unique：" + (String)result.get("preprocess_attribute_unique"));
				
				// 获取属性详细统计信息
				if (attributeStatGrid != null)
					attributeStatScan.remove(attributeStatGrid);
				
				ListStore<BaseModel> store = null;
				
				if ("numeric".equals(attributeType)) {
					
					// NUMERIC类型属性
					store = new ListStore<BaseModel>();
					store.add((List<BaseModel>)result.get("preprocess_attribute_statistics"));
					
					List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
					ColumnConfig column = new ColumnConfig();
					column.setId("stat_name");
					column.setHeader("统计信息名称");
					column.setWidth(200);
					configs.add(column);
					
					column = new ColumnConfig();
					column.setId("stat_value");
					column.setHeader("统计值");
					column.setWidth(200);
					configs.add(column);
					
					ColumnModel cm = new ColumnModel(configs);
					
					
						
					attributeStatGrid = new Grid<BaseModel>(store, cm);
						
					attributeStatGrid.setAutoExpandColumn("stat_value");
					attributeStatGrid.setHeight(150);
					attributeStatGrid.setBorders(true);
					attributeStatScan.add(attributeStatGrid);
						
					
					
					
				} else if ("nominal".equals(attributeType)) {
					
					// NOMINAL属性
					store = new ListStore<BaseModel>();
					store.add((List<BaseModel>) result.get("preprocess_attribute_statistics"));
					
					List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
					ColumnConfig column = new ColumnConfig();
					column.setId("valueNo");
					column.setHeader("序号");
					column.setWidth(100);
					configs.add(column);
					
					column = new ColumnConfig();
					column.setId("valueLabel");
					column.setHeader("名称");
					column.setWidth(100);
					configs.add(column);
					
					column = new ColumnConfig();
					column.setId("valueCount");
					column.setHeader("出现次数");
					column.setWidth(100);
					configs.add(column);
					
					column = new ColumnConfig();
					column.setId("valueWeight");
					column.setHeader("权重");
					column.setWidth(100);
					configs.add(column);
					
					ColumnModel cm = new ColumnModel(configs);
					
						
					attributeStatGrid = new Grid<BaseModel>(store, cm);
					attributeStatGrid.setAutoExpandColumn("valueLabel");
					attributeStatGrid.setHeight(150);
					attributeStatGrid.setBorders(true);
						
					attributeStatScan.add(attributeStatGrid);
						
					
					
				} // End of if.
				
				
				
				
			} // End of onSuccess().
			
		});
	}
	
	
}
