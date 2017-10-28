package org.bnuminer.miner.action;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class PreferencesAction implements MinerAction {
	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/preferences.png", 32, 32);
	}
	
	@Override
	public String getText() {
		return "控制面板";
	}
	
	@Override
	public void handleEvent(BaseEvent be) {
		
	}
}
