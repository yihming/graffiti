package org.bnuminer.miner.action;

import org.bnuminer.client.dialog.FileManageDialog;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class FileManageAction implements MinerAction {
	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/filemanage.png", 32, 32);
	}
	
	@Override
	public String getText() {
		return "在线文档";
	}
	
	@Override
	public void handleEvent(BaseEvent be) {
		FileManageDialog.getInstance().show();
	}
}
