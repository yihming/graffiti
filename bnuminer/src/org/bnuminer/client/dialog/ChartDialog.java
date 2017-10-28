package org.bnuminer.client.dialog;

import org.bnuminer.client.KnowledgeFlowViewContainer;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class ChartDialog extends Dialog {
	private static ChartDialog instance;
	
	private String taskId;
	private ContentPanel chartPanel;
	
	public static ChartDialog getInstance() {
		
		if (instance == null)
			instance = new ChartDialog();
		return instance;
	}
	
	private ChartDialog() {
		this.setDraggable(false);
		setHeading("流程图");
		setModal(true);
		setResizable(false);
		setClosable(false);
		setBodyBorder(false);
		setHideOnButtonClick(true);
		setLayout(new BorderLayout());
		
		chartPanel = new ContentPanel();
		chartPanel.setHeaderVisible(false);
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		data.setMargins(new Margins(1));
		add(chartPanel, data);
		setButtons(Dialog.CLOSE);
	}
	
	public void show(String taskId) {
		this.taskId = taskId;
		show();
		
	}
	
	@Override
	public void show() {
		
		this.setHeight(400);
		this.setWidth(420);
		super.show();
		
		KnowledgeFlowViewContainer.getInstance().show(chartPanel.el().getX() + 1, chartPanel.el().getY() + 1, chartPanel.el().getWidth() - 2, chartPanel.el().getHeight() - 2, taskId);
	}
	
	@Override
	public void hide(Button buttonPressed) {
		KnowledgeFlowViewContainer.getInstance().hide();
		super.hide(buttonPressed);
	}
	
	
}
