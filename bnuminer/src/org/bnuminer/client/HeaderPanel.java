package org.bnuminer.client;


import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Image;

/**
 * 主界面顶部类，派生自ContentPanel
 * @author John Yung
 *
 */
public class HeaderPanel extends ContentPanel {
	/**
	 * 系统LOGO
	 */
	private Image logo;
	
	/**
	 * 系统标题
	 */
	private Label site_title;
	
	/**
	 * 登录成功后知欢迎信息
	 */
	private Label welcome_label;
	
	/**
	 * 主布局面板
	 */
	private HorizontalPanel hPanel;
	
	/**
	 * 辅助布局面板
	 */
	private VerticalPanel vPanel;
	
	/**
	 * 登录成功事件的监听
	 */
	private Listener<BaseEvent> welcomeListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			// 添加事件响应
			BnuMiner.getCurrent().setWelcome();
			
		}
	};
	
	/**
	 * 注销成功事件的监听
	 */
	private Listener<BaseEvent> logoutListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			// 添加事件响应
			
		}
	};
	
	/**
	 * 默认构造函数
	 */
	public HeaderPanel() {
		BnuMiner.getCurrent().addAppListener(AppEvents.LoginSuccess, welcomeListener);
		BnuMiner.getCurrent().addAppListener(AppEvents.LogoutSuccess, logoutListener);
		setHeaderVisible(false);
		setLayout(new FitLayout());
		hPanel = new HorizontalPanel();
		vPanel = new VerticalPanel();
	}
	
	/**
	 * 添加系统LOGO，并设置格式
	 * @param url - LOGO所存放的地址
	 */
	public void setImage(String url) {
		logo = new Image();
		logo.setUrl(url);
		logo.setHeight("100px");
		hPanel.add(logo);
	}
	
	/**
	 * 设置系统标题
	 * @param title - 标题内容
	 */
	public void setSiteTitle(String title) {
		site_title = new Label(title);
		vPanel.add(site_title);
	}
	
	public void addWelcomeLabel() {
		welcome_label = new Label();
		vPanel.add(welcome_label);
	}
	
	/**
	 * 添加欢迎信息
	 */
	public void setWelcomeInfo(String welcomeinfo) {
		welcome_label.setText(welcomeinfo);
		
	}
	
	/**
	 * 将所有Label均添加至主布局面板
	 */
	public void setLabels() {
		hPanel.add(vPanel);
		add(hPanel);
	}

}
