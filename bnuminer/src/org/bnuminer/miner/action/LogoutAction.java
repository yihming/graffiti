package org.bnuminer.miner.action;

import org.bnuminer.client.BnuMiner;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 注销事件的活动，实现自MinerAction
 * @author John Yung
 *
 */
public class LogoutAction implements MinerAction {
	
	/**
	 * 获取图标
	 * @return AbstractImagePrototype 图标对象之抽象类
	 */
	@Override
	public AbstractImagePrototype getIcon() {
		return IconHelper.createPath(GWT.getHostPageBaseURL() + "images/logout.png", 32, 32);
		
	}
	
	/**
	 * 获取活动标题
	 * @return String 活动标题
	 */
	@Override
	public String getText() {
		return "退出登录";
	}
	
	/**
	 * 活动所对应之事件
	 */
	@Override
	public void handleEvent(BaseEvent be) {
		BnuMiner.getCurrent().logout();
	}
}
