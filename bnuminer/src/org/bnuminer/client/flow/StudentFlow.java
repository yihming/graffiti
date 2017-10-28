package org.bnuminer.client.flow;

import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.dialog.ChartDialog;
import org.bnuminer.client.dialog.OpenFileDialog;
import org.bnuminer.client.model.Logic;
import org.bnuminer.client.service.StudentFlowService;
import org.bnuminer.client.service.StudentFlowServiceAsync;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;

public class StudentFlow extends Dialog {
	private static StudentFlow instance;
	private int openDatasetFileId = -1;
	private int taskId;
	//private String xmlConfig;
	
	private ScrollPanel mainPanel;
	
	private TextField<String> datasetFileChosen;
	//private TextField<String> testingFileChosen;
	private TextField<String> foldsNum;
	private TextField<String> seedNum;
	private SimpleComboBox<String> displayModelInOldFormatBox;
	private TextField<String> maxIterationsField;
	private TextField<String> minStdDevField;
	private TextField<String> numClustersField;
	private TextField<String> seedField;
	
	//private LoadingPanel loadingPanel;
	
	public static StudentFlow getInstance() {
		if (instance == null)
			instance = new StudentFlow();
		
		return instance;
	}
	
	public StudentFlow() {
		setHeading("学生信息挖掘模块");
		setWidth(440);
		setHeight(370);
		setModal(true);
		setResizable(false);
		setClosable(false);
		setLayout(new FitLayout());
		
		
		
		createForm();
		
		//loadingPanel = new LoadingPanel(mainPanel);
		
		this.setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("开始");
		getButtonById(Dialog.CANCEL).setText("取消");
		
		getButtonBar().add(new Button("流程图", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// 设置任务号
				taskId = 1;
				
				// 生成XML配置文件
				StudentFlowServiceAsync service = GWT.create(StudentFlowService.class);
				service.writeToXML(String.valueOf(taskId), getDatasetFileName(), getFoldsNumToString(), getSeedNumToString(), 
						getDisplayModelInOldFormat(), getMaxIterationsToString(), getMinStdDevToString(), 
						getNumClustersToString(), getSeedToString(), new AsyncCallbackEx<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								if (result.booleanValue() == true) { // 成功，显示流程图
									
									ChartDialog.getInstance().show(String.valueOf(taskId));
									
								} else if (result.booleanValue() == false) { // 失败，输出失败信息
									
									Window.alert("XML配置文件生成失败！");
								}
								
							}
					
				});
				
				
			}
			
		}));
	}
	
	public void createForm() {
		
		mainPanel = new ScrollPanel();
		add(mainPanel);
		ContentPanel optionPanel = new ContentPanel();
		optionPanel.setLayout(new FitLayout());
		mainPanel.add(optionPanel);
		
		FormData formData = new FormData("-20");
		
		// 数据来源
		FormPanel dataSourcePanel = new FormPanel();
		dataSourcePanel.setHeading("数据来源");
		dataSourcePanel.setFrame(true);
		
		datasetFileChosen = new TextField<String>();
		datasetFileChosen.setFieldLabel("训练数据");
		datasetFileChosen.setReadOnly(true);
		datasetFileChosen.setAllowBlank(false);
		datasetFileChosen.setLabelSeparator("");
		
		Button chooseDatasetFileButton = new Button("选择文件...");
		chooseDatasetFileButton.setStyleAttribute("margin-bottom", "5px");
		chooseDatasetFileButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				OpenFileDialog.getInstance().show("StudentFlow_DataSet");
			}
			
		});
		
		
		dataSourcePanel.add(datasetFileChosen, formData);
		dataSourcePanel.add(chooseDatasetFileButton);
		
		optionPanel.add(dataSourcePanel);
		
		// 交叉验证参数
		FormPanel validationPanel = new FormPanel();
		validationPanel.setHeading("CrossValidation 参数设置");
		validationPanel.setFrame(true);
		
		foldsNum = new TextField<String>();
		foldsNum.setFieldLabel("分类数目(Folds)");
		foldsNum.setLabelSeparator("");
		foldsNum.setValue("10");
		validationPanel.add(foldsNum, formData);
	
		seedNum = new TextField<String>();
		seedNum.setFieldLabel("随机种子(Seed)");
		seedNum.setValue("1");
		seedNum.setLabelSeparator("");
		validationPanel.add(seedNum, formData);
		
		optionPanel.add(validationPanel);
		
		// 聚类算法
		FormPanel classifierPanel = new FormPanel();
		classifierPanel.setFrame(true);
		classifierPanel.setHeading("聚类器设置");
		
		Label classifierContent = new Label("聚类算法：EM");
		classifierPanel.add(classifierContent);
		
		ListStore<Logic> logicStore = new ListStore<Logic>();
		logicStore.add(new Logic("false", 0));
		logicStore.add(new Logic("true", 1));
		
		
		// 输出格式选择
		displayModelInOldFormatBox = new SimpleComboBox<String>();
		displayModelInOldFormatBox.setFieldLabel("以旧有格式输出");
		displayModelInOldFormatBox.setTriggerAction(TriggerAction.ALL);
		displayModelInOldFormatBox.setEditable(false);
		displayModelInOldFormatBox.add("否");
		displayModelInOldFormatBox.add("是");
		displayModelInOldFormatBox.setSimpleValue("否");
		displayModelInOldFormatBox.setLabelSeparator("");
		classifierPanel.add(displayModelInOldFormatBox, formData);
		
		// 最大迭代次数
		maxIterationsField = new TextField<String>();
		maxIterationsField.setFieldLabel("最大迭代次数");
		maxIterationsField.setLabelSeparator("");
		maxIterationsField.setValue("100");
		classifierPanel.add(maxIterationsField, formData);
		
		// 最小标准差
		minStdDevField = new TextField<String>();
		minStdDevField.setFieldLabel("最小标准差");
		minStdDevField.setLabelSeparator("");
		minStdDevField.setValue("1.0E-6");
		classifierPanel.add(minStdDevField, formData);
		
		// 聚类数目
		numClustersField = new TextField<String>();
		numClustersField.setFieldLabel("聚类数目（-1为自动调整）");
		numClustersField.setValue("-1");
		classifierPanel.add(numClustersField, formData);
		
		// 随机种子
		seedField = new TextField<String>();
		seedField.setFieldLabel("随机数生成种子");
		seedField.setValue("100");
		classifierPanel.add(seedField, formData);
		
		
		
		
		optionPanel.add(classifierPanel);
	}
	
	@Override
	protected void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) { // 校验参数，开始挖掘
			//loadingPanel.loadingBegin();
			StudentFlowServiceAsync service = GWT.create(StudentFlowService.class);
			service.doWekaFlow(getDatasetFileId(), getFoldsNumToString(),
					getSeedNumToString(), getDisplayModelInOldFormat(), getMaxIterationsToString(),
					getMinStdDevToString(), getNumClustersToString(), getSeedToString(),
					new AsyncCallbackEx<String>() {

						@Override
						public void onSuccess(String result) {
							MessageBox.info("聚类结果", result, null);
							//loadingPanel.loadingEnd();
						}
				
			});
			
			
		} else if (Dialog.CANCEL.equals(button.getItemId())) { // 取消，关闭，重置
			reset();
			hide();
		}
	} // End of onButtonPressed().
	
	public void reset() {
		openDatasetFileId = -1;
		
		datasetFileChosen.setValue("");
		foldsNum.setValue("10");
		seedNum.setValue("1");
		maxIterationsField.setValue("100");
		minStdDevField.setValue("1.0E-6");
		numClustersField.setValue("-1");
		seedField.setValue("100");
		
	} // End of reset().
	
	
	
	
	public String getDatasetFileName() {
		return datasetFileChosen.getValue();
	}
	
	public void setDatasetFileName(String fileName) {
		datasetFileChosen.setValue(fileName);
	}
	
	public int getDatasetFileId() {
		return openDatasetFileId;
	}
	
	public void setDatasetFileId(int id) {
		this.openDatasetFileId = id;
	}
	
	public String getFoldsNumToString() {
		return foldsNum.getValue();
	}
	
	public void setFoldsNumToString(String value) {
		foldsNum.setValue(value);
	}
	
	public String getSeedNumToString() {
		return seedNum.getValue();
	}
	
	public void setSeedNumToString(String value) {
		seedNum.setValue(value);
	}
	
	public String getDisplayModelInOldFormat() {
		return displayModelInOldFormatBox.getSimpleValue();
	}
	
	public String getMaxIterationsToString() {
		return maxIterationsField.getValue();
	}
	
	public void setMaxIterations(String value) {
		maxIterationsField.setValue(value);
	}
	
	public String getMinStdDevToString() {
		return minStdDevField.getValue();
	}
	
	public void setMinStdDev(String value) {
		minStdDevField.setValue(value);
	}
	
	public String getNumClustersToString() {
		return numClustersField.getValue();
	}
	
	public void setNumClusters(String value) {
		numClustersField.setValue(value);
	}
	
	public String getSeedToString() {
		return seedField.getValue();
	}
	
	public void setSeed(String value) {
		seedField.setValue(value);
	}
	
	
	
}
