package org.bnuminer.client;

import java.util.Date;

import com.extjs.gxt.ui.client.core.DomQuery;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;

public class KnowledgeFlowViewContainer {
	private El knowledgeFlowViewContainer = null;
	private static KnowledgeFlowViewContainer instance;
	
	public static KnowledgeFlowViewContainer getInstance() {
		if (instance == null)
			instance = new KnowledgeFlowViewContainer();
		
		return instance;
		
	} // End of getInstance().
	
	/**
	 *	 获取KnowledgeFlowViewContainer对象
	 */
	private KnowledgeFlowViewContainer() {
		knowledgeFlowViewContainer = El.fly(DomQuery.selectNode("DIV[id=knowledgeFlowViewContainer]"));
	}
	
	/**
	 * JSNI函数，设置Flex对象的显示内容
	 */
	private native void setData(String url) /*-{
		$wnd.knowledgeFlowView.setData(url);
	}-*/;
	
	public void show(int x, int y, int w, int h, String taskId) {
		knowledgeFlowViewContainer.makePositionable();
		knowledgeFlowViewContainer.setLeft(x);
		knowledgeFlowViewContainer.setTop(y);
		knowledgeFlowViewContainer.setWidth(w);
		knowledgeFlowViewContainer.setHeight(h);
		knowledgeFlowViewContainer.setVisible(true);
		knowledgeFlowViewContainer.updateZIndex(XDOM.getTopZIndex());
		String cache = Long.toString(new Date().getTime()) + "." + Double.toString(Math.random());
		
		setData("bnuminer/chart?id=" + taskId + "&cache=" + cache);
		
	}
	
	public void hide() {
		
		knowledgeFlowViewContainer.makePositionable(false);
		knowledgeFlowViewContainer.setWidth(0);
		knowledgeFlowViewContainer.setHeight(0);
	}
}
