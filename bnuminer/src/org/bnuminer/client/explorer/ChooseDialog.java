package org.bnuminer.client.explorer;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.MinerReader;
import org.bnuminer.client.model.Folder;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;

public class ChooseDialog extends Dialog {
	private String period;
	private static ChooseDialog instance;
	
	public ChooseDialog getInstance() {
		if (instance == null)
			instance = new ChooseDialog();
		
		return instance;
	}
	
	/**
	 * 构造主函数
	 * @param period
	 */
	public ChooseDialog() {
		//setPeriod(period);
		setHeading(getPeriod());
		setLayout(new FitLayout());
		setModal(true);
		setWidth(300);
		setHeight(400);
		
		
		//createGrid(period);
		
		
		setButtons(Dialog.CANCEL);
		getButtonById(Dialog.CANCEL).setText("关闭");
	}
	
	/**
	 * 根据参数创建相应的Grid
	 * @param period
	 */
	public void createGrid(String period) {
		setPeriod(period);
		Folder main_folder = MinerReader.getTreeModel(period);
		
		if (period.equals("clusterer")) { // 聚类算法选择
		
			TreeStore<ModelData> store = new TreeStore<ModelData>();
			store.add(main_folder.getChildren(), true);
		
			final TreePanel<ModelData> tree = new TreePanel<ModelData>(store);
			tree.setDisplayProperty("name");
			tree.getStyle().setLeafIcon(IconHelper.createPath(GWT.getHostPageBaseURL() + "images/mineritem.png"));
			tree.setAutoExpand(true);
		
			TreePanelSelectionModel<ModelData> tpsm = new TreePanelSelectionModel<ModelData>() {
				protected void onSelectChange(ModelData model, boolean select) {
					super.onSelectChange(model, select);
					if (select) {
						ExplorerWindow.getInstance().setClustererName(model.get("name").toString());
						ExplorerWindow.getInstance().setClustererOptions(model.get("default_options").toString());
						ClusterPanel.getInstance().setClustererName(model.get("name").toString());
						ClusterPanel.getInstance().setClustererOptions(model.get("default_options").toString());
					
						Label clusterer = ExplorerWindow.getInstance().getLabel("cluster_clusterer");
						clusterer.setText("<b>" + ClusterPanel.getInstance().getClustererName() + "</b>" + " " + ClusterPanel.getInstance().getClustererOptions());
						System.out.println("Here: " + ClusterPanel.getInstance().getClustererName());
						BnuMiner.getCurrent().fireAppEvent(AppEvents.ChangeClusterer, null);
					}
				}
			};
		
			tree.setSelectionModel(tpsm);
		
			add(tree, new FlowData(10));
		}
		
	} // End of createGrid().
	
	
	/**
	 * 获取当前period值
	 * @return
	 */
	public String getPeriod() {
		return period;
	}
	
	/**
	 * 设置当前的period值
	 * @param period
	 */
	public void setPeriod(String period) {
		this.period = period;
	}
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.CANCEL.equals(button.getItemId())) { // CANCEL
			hide();
		}
	} // End of onButtonPressed().
	

}
