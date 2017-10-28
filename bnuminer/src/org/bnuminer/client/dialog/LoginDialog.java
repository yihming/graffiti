package org.bnuminer.client.dialog;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;

/**
 * 登录框类，派生自Dialog
 * @author John Yung
 *
 */
public class LoginDialog extends Dialog {
	/**
	 * 用户名输入框
	 */
	private TextField<String> userNameField;
	
	/**
	 * 密码输入框
	 */
	private TextField<String> passwordField;
	
	/**
	 * 验证码图像显示
	 */
	private Image authImage;
	
	/**
	 * 验证码输入框
	 */
	private TextField<String> validateCodeField;
	
	/**
	 * 系统LOGO
	 */
	private Image logo;
	
	
	/**
	 * 默认构造函数：创建登录框，设置格式，添加内容
	 */
	public LoginDialog() {
		setHeading("登录");
		setLayout(new VBoxLayout());
		setModal(true);
		setResizable(false);
		setClosable(false);
		setWidth(350);
		setHeight(300);
		
		// 系统LOGO
		logo = new Image();
		logo.setUrl(GWT.getHostPageBaseURL() + "images/logo.png");
		logo.setHeight("80px");
		logo.setWidth("300px");
		add(logo);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		formPanel.setLabelWidth(50);
		
		// 用户名输入框
		userNameField = new TextField<String>();
		userNameField.setFieldLabel("用户名");
		userNameField.focus();
		formPanel.add(userNameField, new FormData("90%"));
		
		// 密码输入框
		passwordField = new TextField<String>();
		passwordField.setFieldLabel("密码");
		passwordField.setPassword(true);
		formPanel.add(passwordField, new FormData("90%"));
		
		HorizontalPanel hPanel = new HorizontalPanel();
		
		// 校验码输入框
		validateCodeField = new TextField<String>();
		validateCodeField.setFieldLabel("验证码");
		hPanel.add(validateCodeField);
		
		// 校验码图像
		authImage = new Image();
		authImage.setUrl(GWT.getHostPageBaseURL() + "first.authimage");
		hPanel.add(authImage);
		
		formPanel.add(hPanel, new FormData("100%"));
		
		// 更换校验码链接
		Anchor changeImage = new Anchor("看不清楚？换一张");
		changeImage.setHeight("10px");
		changeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setAuthImage();
			}
		});
		formPanel.add(changeImage);
		
		
		
		add(formPanel);
		
		getButtonById(Dialog.OK).setText("登录");
		
		// 用户注册按钮
		Button signUp = new Button("加入我们");
		signUp.setStyleAttribute("background", "url(" + GWT.getHostPageBaseURL() + "images/signup.png" + ") left top repeat");
		signUp.setStyleAttribute("color", "#ffffff");
		signUp.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				SignupDialog.getInstance().show();
			}
			
		});
		
		getButtonBar().add(signUp);
	}
	
	/**
	 * 重置所有内容
	 */
	public void reset() {
		userNameField.setValue(null);
		passwordField.setValue(null);
		validateCodeField.setValue(null);
	}
	
	public void setAuthImage() {
		authImage.setUrl(GWT.getHostPageBaseURL() + (Math.round(Math.random() * 10000)) + ".authimage");
	}
	
	
	
	/**
	 * 点击按钮后的响应函数
	 * @param button - 按钮对象
	 */
	@Override
	protected void onButtonPressed(Button button) {
		if (button == getButtonBar().getItemByItemId(OK)) { // 按下OK键
			BnuMiner.getCurrent().mask();
			hide();
			BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
			
			// 调用GWT-RPC的login()
			service.login(userNameField.getValue(), passwordField.getValue(), validateCodeField.getValue(), new AsyncCallbackEx<Integer>() {
				@Override
				public void onSuccess(Integer result) {
					BnuMiner.getCurrent().unmask();
					if (result.intValue() == 0) { // 登录成功
						reset();
						BnuMiner.getCurrent().fireAppEvent(AppEvents.LoginSuccess, null);
					} else if (result.intValue() == 1) { // 用户名或密码错误
						show();
						Window.alert("登录失败！用户名或密码错误！");
					} else if (result.intValue() == 2) { // 验证码错误
						passwordField.setValue(null);
						validateCodeField.setValue(null);
						show();
						Window.alert("登录失败！验证码错误！");
					}
				}
			});
		}
	}
	
	/**
	 * 若第一次打开主页，则显示登录框；否则，不显示
	 */
	@Override
	public void show() {
		BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
		
		service.getUserInfo(new AsyncCallbackEx<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (!result) {
					
					LoginDialog.super.show();
				}
			}
		});
	}
	
}
