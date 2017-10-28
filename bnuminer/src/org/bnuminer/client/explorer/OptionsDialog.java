package org.bnuminer.client.explorer;


import java.util.HashMap;
import java.util.Map;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.BnuMiner;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class OptionsDialog extends Dialog {
	private static OptionsDialog instance;
	
	private String clustererName;
	
	private ContentPanel mainPanel;
	
	
	private Map<String, Object> paramMap;
	
	private Listener<BaseEvent> changeContentListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			/*if (mainPanel != null)
				mainPanel.removeAll();
			
			mainPanel.setLayout(new FormLayout());
			*/
		}
		
	};
	
	public static OptionsDialog getInstance() {
		if (instance == null)
			instance = new OptionsDialog();
		
		return instance;
	}
	
	public OptionsDialog() {
		setHeading("选择聚类器参数");
		setFrame(true);
		setModal(true);
		setClosable(false);
		setResizable(false);
		setSize(400, 550);
		setLayout(new FitLayout());
		
		BnuMiner.getCurrent().addAppListener(AppEvents.ChangeClusterer, changeContentListener);
			
		
		
		
		paramMap = new HashMap<String, Object>();
	
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("确定");
		getButtonById(Dialog.CANCEL).setText("取消");
	}
	
	public void createPanel() {

		
		
		FormData formData = new FormData("-20");
		
		if (mainPanel != null) {
			mainPanel.removeAll();

		} else {
			mainPanel = new ContentPanel();
			mainPanel.setLayout(new FormLayout());
			mainPanel.setHeaderVisible(false);
			mainPanel.setFrame(true);
			mainPanel.setBodyBorder(false);
			add(mainPanel);
		}
		
		if ("Cobweb".equals(getClustererName())) {
			
			System.out.println("Cobweb");
			
			final TextField<String> acuity = new TextField<String>();
			acuity.setFieldLabel("灵敏度");
			paramMap.put("Cobweb_acuity", acuity);
			mainPanel.add(acuity, formData);
			
			final TextField<String> cutoff = new TextField<String>();
			cutoff.setFieldLabel("截断指数");
			paramMap.put("Cobweb_cutoff", cutoff);
			mainPanel.add(cutoff, formData);
			
			final SimpleComboBox<String> saveInstanceData = new SimpleComboBox<String>();
			saveInstanceData.setTriggerAction(TriggerAction.ALL);
			saveInstanceData.setFieldLabel("保存数据");
			saveInstanceData.add("否");
			saveInstanceData.add("是");
			saveInstanceData.setSimpleValue("否");
			paramMap.put("Cobweb_saveInstanceData", saveInstanceData);
			mainPanel.add(saveInstanceData, formData);
			
			final TextField<String> seedCobweb = new TextField<String>();
			seedCobweb.setFieldLabel("随机数种子");
			paramMap.put("Cobweb_seed", seedCobweb);
			mainPanel.add(seedCobweb, formData);
			
		} else if ("DBScan".equals(getClustererName())) {
			
			final TextField<String> database_type = new TextField<String>();
			database_type.setFieldLabel("数据库类型");
			paramMap.put("DBScan_databaseType", database_type);
			mainPanel.add(database_type, formData);
			
			final TextField<String> database_distanceType = new TextField<String>();
			database_distanceType.setFieldLabel("数据库数据集类型");
			paramMap.put("DBScan_databaseDistanceType", database_distanceType);
			mainPanel.add(database_distanceType, formData);
			
			final TextField<String> epsilon = new TextField<String>();
			epsilon.setFieldLabel("误差范围值");
			paramMap.put("DBScan_epsilon", epsilon);
			mainPanel.add(epsilon, formData);
			
			final TextField<String> minPoints = new TextField<String>();
			minPoints.setFieldLabel("最小点数");
			paramMap.put("DBScan_minPoints", minPoints);
			mainPanel.add(minPoints, formData);
		
		} else if ("CLOPE".equals(getClustererName())) {
			
			TextField<String> repulsion = new TextField<String>();
			repulsion.setFieldLabel("排斥系数");
			paramMap.put("CLOPE_repulsion", repulsion);
			mainPanel.add(repulsion, formData);
		} else if ("EM".equals(getClustererName())) {
			
			System.out.println("EM");
			
			SimpleComboBox<String> displayModelInOldFormat = new SimpleComboBox<String>();
			displayModelInOldFormat.setFieldLabel("以旧格式输出");
			displayModelInOldFormat.add("否");
			displayModelInOldFormat.add("是");
			displayModelInOldFormat.setSimpleValue("否");
			paramMap.put("EM_displayModelInOldFormat", displayModelInOldFormat);
			mainPanel.add(displayModelInOldFormat, formData);
			
			TextField<String> maxIterations = new TextField<String>();
			maxIterations.setFieldLabel("最大迭代次数");
			paramMap.put("EM_maxIterations", maxIterations);
			mainPanel.add(maxIterations, formData);
			
			TextField<String> minStdDev = new TextField<String>();
			minStdDev.setFieldLabel("最小标准差");
			paramMap.put("EM_minStdDev", minStdDev);
			mainPanel.add(minStdDev, formData);
			
			TextField<String> numClusters = new TextField<String>();
			numClusters.setFieldLabel("聚类数目");
			paramMap.put("EM_numClusters", numClusters);
			mainPanel.add(numClusters, formData);
			
			TextField<String> seed = new TextField<String>();
			seed.setFieldLabel("随机数种子");
			paramMap.put("EM_seed", seed);
			mainPanel.add(seed, formData);
		} else if ("FarthestFirst".equals(getClustererName())) {
			TextField<String> numClusters = new TextField<String>();
			numClusters.setFieldLabel("聚类数目");
			paramMap.put("FarthestFirst_numClusters", numClusters);
			mainPanel.add(numClusters);
			
			TextField<String> seed = new TextField<String>();
			seed.setFieldLabel("随机数种子");
			paramMap.put("FarthestFirst_seed", seed);
			mainPanel.add(seed);
		} else if ("FilteredClusterer".equals(getClustererName())) {
			
			HorizontalPanel hPanel1 = new HorizontalPanel();
			TextField<String> clusterer = new TextField<String>();
			clusterer.setFieldLabel("聚类器");
			clusterer.setReadOnly(true);
			paramMap.put("FilteredClusterer_clusterer", clusterer);
			
			Button choose_clusterer = new Button("选择...");
			hPanel1.add(clusterer);
			hPanel1.add(choose_clusterer);
			mainPanel.add(hPanel1, formData);
			
			HorizontalPanel hPanel2 = new HorizontalPanel();
			TextField<String> filter = new TextField<String>();
			filter.setFieldLabel("过滤器");
			filter.setReadOnly(true);
			paramMap.put("FilteredClusterer_filter", filter);
			
			Button choose_filter = new Button("选择...");
			hPanel2.add(filter);
			hPanel2.add(choose_filter);
			mainPanel.add(hPanel2, formData);
			
		} else if ("MakeDensityBasedClusterer".equals(getClustererName())) {
			
			HorizontalPanel hPanel = new HorizontalPanel();
			TextField<String> clusterer = new TextField<String>();
			clusterer.setFieldLabel("聚类器");
			clusterer.setReadOnly(true);
			
			Button choose_clusterer = new Button("选择...");
			
			hPanel.add(clusterer);
			hPanel.add(choose_clusterer);
			mainPanel.add(hPanel, formData);
			
			TextField<String> minStdDev = new TextField<String>();
			minStdDev.setFieldLabel("最小标准差");
			mainPanel.add(minStdDev, formData);
			
		} else if ("OPTICS".equals(getClustererName())) {
			TextField<String> database_type = new TextField<String>();
			database_type.setFieldLabel("数据库类型");
			mainPanel.add(database_type, formData);
			
			TextField<String> database_instanceType = new TextField<String>();
			database_instanceType.setFieldLabel("数据库数据集类型 ");
			mainPanel.add(database_instanceType, formData);
			
			TextField<String> epsilon = new TextField<String>();
			epsilon.setFieldLabel("误差描述");
			mainPanel.add(epsilon, formData);
			
			TextField<String> minPoints = new TextField<String>();
			minPoints.setFieldLabel("最小数据点数目");
			mainPanel.add(minPoints, formData);
			
			SimpleComboBox<String> showGUI = new SimpleComboBox<String>();
			showGUI.setFieldLabel("显示图像");
			showGUI.add("是");
			showGUI.add("否");
			showGUI.setSimpleValue("是");
			mainPanel.add(showGUI, formData);
			
			
		} else if ("SimpleKMeans".equals(getClustererName())) {
			SimpleComboBox<String> displayStdDevs = new SimpleComboBox<String>();
			displayStdDevs.setFieldLabel("显示最小标准差");
			displayStdDevs.add("否");
			displayStdDevs.add("是");
			displayStdDevs.setSimpleValue("否");
			mainPanel.add(displayStdDevs, formData);
			
			HorizontalPanel hPanel = new HorizontalPanel();
			TextField<String> distanceFunction = new TextField<String>();
			distanceFunction.setFieldLabel("距离函数");
			distanceFunction.setReadOnly(true);
			
			Button choose_distance = new Button("选择...");
			
			hPanel.add(distanceFunction);
			hPanel.add(choose_distance);
			
			mainPanel.add(hPanel, formData);
			
			SimpleComboBox<String> dontReplaceMissingValues = new SimpleComboBox<String>();
			dontReplaceMissingValues.setFieldLabel("不替换缺少值");
			dontReplaceMissingValues.add("否");
			dontReplaceMissingValues.add("是");
			dontReplaceMissingValues.setSimpleValue("否");
			mainPanel.add(dontReplaceMissingValues, formData);
			
			TextField<String> maxIterations = new TextField<String>();
			maxIterations.setFieldLabel("最大迭代次数");
			mainPanel.add(maxIterations, formData);
			
			TextField<String> numClusters = new TextField<String>();
			numClusters.setFieldLabel("聚类数目");
			mainPanel.add(numClusters, formData);
			
			SimpleComboBox<String> preserveInstancesOrder = new SimpleComboBox<String>();
			preserveInstancesOrder.setFieldLabel("保留步数顺序");
			preserveInstancesOrder.add("否");
			preserveInstancesOrder.add("是");
			preserveInstancesOrder.setSimpleValue("否");
			mainPanel.add(preserveInstancesOrder, formData);
			
			TextField<String> seed = new TextField<String>();
			seed.setFieldLabel("随机数种子");
			mainPanel.add(seed, formData);
		}
		
		
		
	} // End of create().
	
	@SuppressWarnings("unchecked")
	public void reset() {
		
		if ("Cobweb".equals(getClustererName())) {
			
			TextField<String> acuity = (TextField<String>) paramMap.get("Cobweb_acuity");
			acuity.setValue("1.0");
			
			TextField<String> cutoff = (TextField<String>) paramMap.get("Cobweb_cutoff");
			cutoff.setValue("0.0028209479177387815");
			
			SimpleComboBox<String> saveInstanceData = (SimpleComboBox<String>) paramMap.get("Cobweb_saveInstanceData");
			saveInstanceData.setSimpleValue("否");
			
			TextField<String> seed = (TextField<String>) paramMap.get("Cobweb_seed");
			seed.setValue("42");
			
		} else if ("DBScan".equals(getClustererName())) {
			
			TextField<String> database_type = (TextField<String>) paramMap.get("DBScan_databaseType");
			database_type.setValue("weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase");
			
			TextField<String> database_distanceType = (TextField<String>) paramMap.get("DBScan_databaseDistanceType");
			database_distanceType.setValue("weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclidianDataObject");
			
			TextField<String> epsilon = (TextField<String>) paramMap.get("DBScan_epsilon");
			epsilon.setValue("0.9");
			
			TextField<String> minPoints = (TextField<String>) paramMap.get("DBScan_minPoints");
			minPoints.setValue("6");
			
		} else if ("CLOPE".equals(getClustererName())) {
			
			TextField<String> repulsion = (TextField<String>) paramMap.get("CLOPE_repulsion");
			repulsion.setValue("2.6");
			
		} else if ("EM".equals(getClustererName())) {
			
			SimpleComboBox<String> displayModelInOldFormat = (SimpleComboBox<String>) paramMap.get("EM_displayModelInOldFormat");
			displayModelInOldFormat.setSimpleValue("否");
			
			TextField<String> maxIterations = (TextField<String>) paramMap.get("EM_maxIterations");
			maxIterations.setValue("100");
			
			
		}
		
	} // End of reset().
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) {
			
		} else if (Dialog.CANCEL.equals(button.getItemId())) {
			hide();
		}
	} // End of onButtonPressed().
	
	public String getClustererName() {
		return clustererName;
	}
	
	public void setClustererName(String clustererName) {
		this.clustererName = clustererName;
	}
	
	@Override
	public void show() {
		setClustererName(ClusterPanel.getInstance().getClustererName());
		createPanel();
		//reset();
		super.show();
	}
}
