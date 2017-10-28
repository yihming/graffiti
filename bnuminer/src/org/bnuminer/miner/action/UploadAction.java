package org.bnuminer.miner.action;

import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.dialog.UploadDialog;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class UploadAction implements MinerAction {
	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/upload.png", 32, 32);
	}
	
	@Override
	public String getText() {
		return "上传文件";
	}
	
	@Override
	public void handleEvent(BaseEvent be) {
		// 显示文件上传界面;
		BnuMiner.getCurrent().upload();
	}
}
