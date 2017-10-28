package org.bnuminer.miner.action;

import org.bnuminer.client.dialog.SystemDialog;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class SystemAction implements MinerAction {
	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/system.png", 32, 32);
	}
	
	@Override
	public String getText() {
		return "系统管理";
	}
	
	@Override
	public void handleEvent(BaseEvent be) {
		SystemDialog.getInstance().show();
	}
}
