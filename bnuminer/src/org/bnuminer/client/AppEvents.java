package org.bnuminer.client;

import com.extjs.gxt.ui.client.event.EventType;

/**
 * <p>管理全局事件
 * @author John Yung
 *
 */
public class AppEvents {
	/**
	 * <p>登录成功
	 */
	public static final EventType LoginSuccess = new EventType();
	
	/**
	 * <p>注销成功
	 */
	public static final EventType LogoutSuccess = new EventType();
	//public static final EventType AfterSubmit = new EventType();
	public static final EventType UploadSuccess = new EventType();
	public static final EventType OpenSuccess = new EventType();
	public static final EventType DbConnectSuccess = new EventType();
	public static final EventType CheckFormValid = new EventType();
	public static final EventType ChangeClusterer = new EventType();
	
	/**
	 * KnowledgeFlow相关事件
	 */
	
	
}
