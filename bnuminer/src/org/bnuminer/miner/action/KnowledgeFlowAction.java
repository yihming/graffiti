package org.bnuminer.miner.action;

import org.bnuminer.client.dialog.ChartDialog;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class KnowledgeFlowAction implements MinerAction {
	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/knowledgeflow.png", 32, 32);
	}
	
	@Override
	public String getText() {
		return "KnowledgeFlow";
	}
	
	@Override
	public void handleEvent(BaseEvent be) {
		ChartDialog.getInstance().show("1");
	}
}
