package org.bnuminer.miner.action;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 管理系统中所有之活动，派生自接口Listener
 * @author John Yung
 *
 */
public interface MinerAction extends Listener<BaseEvent> {
	AbstractImagePrototype getIcon();
	String getText();
}
