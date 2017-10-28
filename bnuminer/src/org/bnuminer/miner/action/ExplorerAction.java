package org.bnuminer.miner.action;

import org.bnuminer.client.explorer.ExplorerWindow;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ExplorerAction implements MinerAction {
	
	
	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/explorer.png", 32, 32);
	}
	
	public String getText() {
		return "算法实验";
	}
	
	@Override
	public void handleEvent(BaseEvent be) {
		ExplorerWindow.getInstance().show();
	}
}
