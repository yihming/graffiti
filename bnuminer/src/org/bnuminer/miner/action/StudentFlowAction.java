package org.bnuminer.miner.action;

import org.bnuminer.client.flow.StudentFlow;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class StudentFlowAction implements MinerAction {

	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/studentflow.png", 32, 32);
	}

	@Override
	public String getText() {
		return "学生信息";
	}

	@Override
	public void handleEvent(BaseEvent be) {
		StudentFlow.getInstance().show();
		
	}
	
}
