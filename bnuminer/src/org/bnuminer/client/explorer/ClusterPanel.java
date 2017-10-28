package org.bnuminer.client.explorer;

import java.util.List;

import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.dialog.OpenFileDialog;
import org.bnuminer.client.service.ClusterService;
import org.bnuminer.client.service.ClusterServiceAsync;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.InputSlider;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ClusterPanel extends ContentPanel {
	private static ClusterPanel instance;
	private String clusterer_name = "EM";
	private String clusterer_options = "-I 100 -N 3 -M 1.0E-6 -S 100";
	private int testFileId = -1;
	private String testFileName = "";
	
	private final RadioGroup cluster_mode_choices = new RadioGroup();
	private Label testFileNameLabel = new Label();
	
	protected TextArea clusterer_output;
	
	public static ClusterPanel getInstance() {
		if (instance == null)
			instance = new ClusterPanel();
		return instance;
	}
	
	public ClusterPanel() {
		clusterer_output = new TextArea();
		clusterer_output.setReadOnly(true);
		clusterer_output.setLabelSeparator("");
		clusterer_output.setWidth("100%");
		
		
		setAutoWidth(false);
		setHeaderVisible(false);
		setWidth(910);
		setHeight(550);
		setLayout(new FitLayout());
	}
	
	/**
	 * 创建主界面
	 * @param parent 父组件对象
	 */
	public void create(ExplorerWindow parent) {
		
		
		// 主界面
		VerticalPanel main_panel = new VerticalPanel();
		
		// 顶部区域
		FormPanel top_panel = new FormPanel();
		top_panel.setLayout(new FitLayout());
		top_panel.setHeading("聚类器");
		top_panel.setWidth(900);
		top_panel.setStyleAttribute("margin", "10 10 10 10");
		top_panel.setHeight(70);
		
		// 选择聚类算法
		HorizontalPanel hPanel = new HorizontalPanel();
		Button choose = new Button("选择");
		choose.setStyleAttribute("margin", "0 10 5 5");
		choose.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ExplorerWindow.getInstance().chooseDialog = new ChooseDialog();
				ExplorerWindow.getInstance().chooseDialog.createGrid("clusterer");
				ExplorerWindow.getInstance().chooseDialog.show();
				
			}
			
		});
		
		Label clusterer = new Label();
		clusterer.setBorders(true);
		clusterer.setStyleAttribute("background-color", "#D4D4D4");
		clusterer.setStyleAttribute("font-size", "13px");
		clusterer.setText("<b>EM</b> -I 100 -N 3 -M 1.0E-6 -S 100");
		parent.labelMap.put("cluster_clusterer", clusterer);
		clusterer.setAutoWidth(false);
		clusterer.setWidth(600);
		
		Button paramModify = new Button("修改参数");
		paramModify.setStyleAttribute("margin", "0 5 5 10");
		paramModify.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				OptionsDialog.getInstance().show();
				
			}
			
		});
		
		hPanel.add(choose);
		hPanel.add(clusterer);
		hPanel.add(paramModify);
		top_panel.add(hPanel);
		
		// 下方区域
		HorizontalPanel bottom_panel = new HorizontalPanel();
		
		
		// 左下方区域
		VerticalPanel bottom_left_panel = new VerticalPanel();
		bottom_left_panel.setAutoWidth(false);
		bottom_left_panel.setWidth(450);
		
		// 聚类模式
		ContentPanel cluster_mode = new ContentPanel();
		//cluster_mode.setLayout(new RowLayout(Orientation.VERTICAL));
		cluster_mode.setLayout(new FitLayout());
		cluster_mode.setHeading("聚类模式");
		cluster_mode.setFrame(true);
		cluster_mode.setStyleAttribute("margin", "0 10 10 10");
		
		//VerticalPanel radio_panel = new VerticalPanel();
		
		// 选项1
		Radio choice1 = new Radio();
		choice1.setBoxLabel("使用训练数据集");
		choice1.setData("mode", 1);
		choice1.setTitle("对训练数据集进行聚类");
		
		//cluster_mode.add(choice1, new RowData(1, -1, new Margins(5)));
		
		
		// 选项2
		Radio choice2 = new Radio();
		choice2.setBoxLabel("提供测试数据集");
		choice2.setData("mode", 2);
		choice2.setTitle("对用户提供的数据集进行聚类");
		
		
		
		
		//cluster_mode.add(choice2Panel, new RowData(1, -1, new Margins(5)));
		//cluster_mode.add(choice2Panel);
		
		// 选项3
		Radio choice3 = new Radio();
		choice3.setBoxLabel("百分比划分");
		choice3.setData("mode", 3);
		choice3.setTitle("将数据集中的该百分比部分作为训练样本，其余为测试样本");
		
		
		
		//cluster_mode.add(choice3Panel, new RowData(1, -1, new Margins(5)));
		//cluster_mode.add(choice3Panel);
		
		// 选项4
		/*VerticalPanel choice4Panel = new VerticalPanel();
		Radio choice4 = new Radio();
		choice4.setBoxLabel("选择特定属性评估聚类器");
		choice4.setData("mode", 4);
		choice4.setTitle("根据所选属性评价聚类器");
		choice4Panel.add(choice4);
		
		RpcProxy<List<BaseModel>> proxy = new RpcProxy<List<BaseModel>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<BaseModel>> callback) {
				ClusterServiceAsync service = GWT.create(ClusterService.class);
				service.getAttributes(ExplorerWindow.getInstance().getCurrentOpenMode(), ExplorerWindow.getInstance().getChosenFileId(), callback);
				
			}
			
		};
		
		ListLoader<BaseListLoadResult<BaseModel>> loader = new BaseListLoader<BaseListLoadResult<BaseModel>>(proxy);
		//ListStore<BaseModel> attrlistStore = new ListStore<BaseModel>(loader);
		
		final ComboBox<BaseModel> choiceAttribute = new ComboBox<BaseModel>();
		choiceAttribute.setTriggerAction(TriggerAction.ALL);
		choiceAttribute.setEditable(false);
		choiceAttribute.setEnabled(false);
		choiceAttribute.setStore(new ListStore<BaseModel>(loader));
		choiceAttribute.setDisplayField("attr_name");
		choiceAttribute.setValue(choiceAttribute.getStore().getAt(0));
		choice4Panel.add(choiceAttribute);
		
		
	
		//cluster_mode.add(choice4Panel, new RowData(1, 1, new Margins(5)));
		radio_panel.add(choice4Panel);
		*/
		cluster_mode_choices.setLayoutData(new RowLayout(Orientation.VERTICAL));
		cluster_mode_choices.setFieldLabel("模式选择");
		cluster_mode_choices.setLabelSeparator("");
		cluster_mode_choices.add(choice1);
		cluster_mode_choices.add(choice2);
		cluster_mode_choices.add(choice3);
		//cluster_mode_choices.add(choice4);
		cluster_mode_choices.setValue(choice1);
		
		cluster_mode.add(cluster_mode_choices);
		
		HorizontalPanel choose_testPanel = new HorizontalPanel();
		
		// 选项2之选择测试集按钮
		final Button choose_testdataset = new Button("选择测试文件...");
		choose_testdataset.setEnabled(false);
		choose_testdataset.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				OpenFileDialog.getInstance().show("ClusterTestDataset");
				
			}
			
		});
		
		
		choose_testPanel.add(choose_testdataset);
		
		
		testFileNameLabel.setStyleAttribute("font-size", "13");
		testFileNameLabel.setText("未选择");
		
		choose_testPanel.add(testFileNameLabel);
		
		cluster_mode.add(choose_testPanel);
		
		// 选项3之选择百分比按钮
		HorizontalPanel percentagePanel = new HorizontalPanel();
		
		Label percentageLabel = new Label();
		percentageLabel.setText("用于训练的百分比");
		percentageLabel.setStyleAttribute("font-size", "13");
		percentageLabel.setStyleAttribute("margin-right", "10px");
		
		percentagePanel.add(percentageLabel);
		
		final InputSlider percentage = new InputSlider();
		percentage.setMinValue(0);
		percentage.setMaxValue(100);
		percentage.setValue(66);
		percentage.setIncrement(1);
		percentage.setInputWidth(100);
		percentage.setEnabled(false);
		
		percentagePanel.add(percentage);
		
		cluster_mode.add(percentagePanel);
		
		
		// 单选框之监听函数
		cluster_mode_choices.addListener(Events.Change, new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				System.out.println((Integer)cluster_mode_choices.getValue().getData("mode"));
				
				if (1 == (Integer)cluster_mode_choices.getValue().getData("mode")) {
					
					choose_testdataset.setEnabled(false);
					percentage.setEnabled(false);
				
				} else if (2 == (Integer)cluster_mode_choices.getValue().getData("mode")) {
					
					choose_testdataset.setEnabled(true);
					percentage.setEnabled(false);
					
					
					
				} else if (3 == (Integer)cluster_mode_choices.getValue().getData("mode")) {
					
					choose_testdataset.setEnabled(false);
					percentage.setEnabled(true);
					
				} 
				
			}
			
		});
		
		
		Button ignore_attributes = new Button("忽略属性");
		ignore_attributes.setAutoWidth(false);
		ignore_attributes.setWidth(400);
		ignore_attributes.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				AttributeDialog.getInstance().show();
				
			}
			
		});
		
		HorizontalPanel start_stop_panel = new HorizontalPanel();
		Button start = new Button("开始");
		start.setAutoWidth(false);
		start.setWidth(400);
		start.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				if (clusterer_output.getValue() != null)
					clusterer_output.setValue(null);
				
				if (getClusterMode() == 1) { // 仅使用训练集
					ClusterServiceAsync cluster_service = GWT.create(ClusterService.class);
					cluster_service.doing_cluster(ClusterPanel.getInstance().getClustererName(), ClusterPanel.getInstance().getClustererOptions(), ExplorerWindow.getInstance().getCurrentOpenMode(), ExplorerWindow.getInstance().getChosenFileId(), AttributeDialog.getInstance().getSelectedAttributes(), new AsyncCallbackEx<String>() {

						@Override
						public void onSuccess(String result) {
							
							setClustererOutput(result);
						}
						
					});
				} else if (getClusterMode() == 2) { // 选择测试集
					ClusterServiceAsync service = GWT.create(ClusterService.class);
					service.doing_cluster_with_testdataset(ClusterPanel.getInstance().getClustererName(), ClusterPanel.getInstance().getClustererOptions(), ExplorerWindow.getInstance().getCurrentOpenMode(),
														ExplorerWindow.getInstance().getChosenFileId(), ClusterPanel.getInstance().getTestFileId(), AttributeDialog.getInstance().getSelectedAttributes(), new AsyncCallbackEx<String>() {

						@Override
						public void onSuccess(String result) {
							
							setClustererOutput(result);
																
						}
						
					});
					
				} else if (getClusterMode() == 3) {
					
				} 
				
				
			}
			
		});
		
		/*
		Button stop = new Button("中止");
		stop.setAutoWidth(false);
		stop.setWidth(200);
		stop.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				
			}
			
		});
		*/
		start_stop_panel.add(start);
		
		//start_stop_panel.add(stop);
		
		FormPanel result_list = new FormPanel();
		result_list.setHeading("结果列表");
		result_list.setLayout(new FormLayout());
		TextArea result_show = new TextArea();
		result_list.add(result_show);
		
		bottom_left_panel.add(cluster_mode);
		bottom_left_panel.add(ignore_attributes);
		bottom_left_panel.add(start_stop_panel);
		bottom_left_panel.add(result_list);
		
		bottom_panel.add(bottom_left_panel);
		
		// 右下方区域
		FormPanel bottom_right_panel = new FormPanel();
		bottom_right_panel.setHeading("聚类器输出");
		
		clusterer_output.setId("clusterer_output");
		clusterer_output.setValue(null);
		clusterer_output.setAutoHeight(false);
		clusterer_output.setHeight(400);
		clusterer_output.setAutoWidth(false);
		clusterer_output.setWidth(450);
		
		bottom_right_panel.add(clusterer_output);
		
		bottom_panel.add(bottom_right_panel);
		
		main_panel.add(top_panel);
		main_panel.add(bottom_panel);
		add(main_panel);
		
	} // End of ClusterPanel().
	
	public void setClustererOutput(String content) {
		clusterer_output.setValue(content);
	}
	
	public String getClustererOutput() {
		return clusterer_output.getValue();
	}
	
	public void setClustererName(String clusterer_name) {
		this.clusterer_name = clusterer_name;
	}
	
	public String getClustererName() {
		return clusterer_name;
	}
	
	public void setClustererOptions(String clusterer_options) {
		this.clusterer_options = clusterer_options;
	}
	
	public String getClustererOptions() {
		return clusterer_options;
	}
	
	public void setTestFileId(int file_id) {
		testFileId = file_id;
	}
	
	public int getTestFileId() {
		return testFileId;
	}
	
	public int getClusterMode() {
		return cluster_mode_choices.getValue().getData("mode");
	}
	
	public String getTestFileName() {
		return testFileName;
	}
	
	public void setTestFileName(String testFileName) {
		this.testFileName = testFileName;
		setTestFileNameLabel(testFileName);
		
	}
	
	public Label getTestFileNameLabel() {
		return testFileNameLabel;
	}
	
	public void setTestFileNameLabel(String filename) {
		testFileNameLabel.setText(filename);
	}
	
}
