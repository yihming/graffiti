package org.bnuminer.client.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LoadingPanel extends SimplePanel {
	private int loadCount = 0;
	private static LoadingPanel instance;
	
	public static LoadingPanel getInstance() {
		if (instance == null)
			instance = new LoadingPanel();
		return instance;
	}
	
	public LoadingPanel() {
		setStyleName("gwtapps-LoadingPanel");
		setVisible(false);
	}
	
	public LoadingPanel(Widget child) {
		this();
		setWidget(child);
	}
	
	public void loadingBegin() {
		if (loadCount == 0) {
			setVisible(true);
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "progress");
			setPosition();
		}
		loadCount++;
	}
	
	public void loadingEnd() {
		loadCount--;
		if (loadCount == 0) {
			setVisible(false);
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "");
		}
	}
	
	public void setPosition() {
		Widget child = getWidget();
		int top = DOM.getElementPropertyInt(RootPanel.getBodyElement(), "scrollTop");
		int left = Window.getClientWidth() - child.getOffsetWidth() + DOM.getElementPropertyInt(RootPanel.getBodyElement(), "scrollLeft");
		DOM.setStyleAttribute(getElement(), "position", "absolute");
		DOM.setStyleAttribute(getElement(), "top", Integer.toString(top));
		DOM.setStyleAttribute(getElement(), "left", Integer.toString(left));
	}
}
