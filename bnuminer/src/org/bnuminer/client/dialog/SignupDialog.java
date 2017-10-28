package org.bnuminer.client.dialog;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class SignupDialog extends Dialog {

	private static SignupDialog instance;
	private FormPanel mainPanel;
	private boolean[] flags;
	
	private TextField<String> usernameField;
	private final Label usernameCheck = new Label();
	private final Image usernameCorrect = new Image();
	
	private TextField<String> passwordField;
	private final Label passwordCheck = new Label();
	private final Image passwordCorrect = new Image();
	
	private TextField<String> pwdconfirmField;
	private final Label pwdconfirmCheck = new Label();
	private final Image pwdconfirmCorrect = new Image();
	
	private TextField<String> truenameField;
	private final Label truenameCheck = new Label();
	private final Image truenameCorrect = new Image();
	
	private TextField<String> emailField;
	private final Label emailCheck = new Label();
	private final Image emailCorrect = new Image();
	
	private Listener<BaseEvent> submitListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			
			BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
			service.signup(usernameField.getValue(), passwordField.getValue(), truenameField.getValue(), emailField.getValue(), new AsyncCallbackEx<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					if (result.booleanValue() == true) {
						MessageBox.info("成功", "注册信息输入成功，等待管理员审批", null);
						reset();
						hide();
					} else {
						MessageBox.info("出错", "抱歉，注册时出现后台错误。我们会尽快处理！", null);
					}
					
				}
				
			});
			
		}
		
	};
	
	public static SignupDialog getInstance() {
		if (instance == null)
			instance = new SignupDialog();
		
		return instance;
	}
	
	public SignupDialog() {
		setHeading("用户注册");
		setWidth(600);
		setHeight(500);
		setResizable(false);
		setModal(true);
		setClosable(false);
		setLayout(new FitLayout());
		
		flags = new boolean[5];
		for (int i = 0; i < 5; ++i)
			flags[i] = false;
		
		
		createForm();
		
		BnuMiner.getCurrent().addAppListener(AppEvents.CheckFormValid, submitListener);
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("注册");
		getButtonById(Dialog.CANCEL).setText("取消");
	}
	
	public void createForm() {
		
		mainPanel = new FormPanel();
		mainPanel.setHeaderVisible(false);

		FormData formData = new FormData("-20");
		
		MarginData marginData = new MarginData(5, 0, 5, 20);
		
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(75);
		layout.setLabelSeparator("");
		layout.setLabelAlign(LabelAlign.TOP);
		
		mainPanel.setLayout(layout);
	
		// 用户名
		usernameField = new TextField<String>();
		usernameField.setStyleAttribute("margin-top", "20px");
		usernameField.setLabelSeparator("");
		usernameField.setFieldLabel("用户名");
		mainPanel.add(usernameField, formData);
		
		
		// 检测用户名是否可用
		usernameCheck.setStyleAttribute("color", "red");
		usernameCheck.setStyleAttribute("margin-top", "5px");
		usernameCheck.setStyleAttribute("margin-bottom", "5px");
		usernameCheck.setVisible(false);
		mainPanel.add(usernameCheck, formData);
		
		usernameCorrect.setUrl(GWT.getHostPageBaseURL() + "images/correct.png");
		usernameCorrect.setHeight("16px");
		usernameCorrect.setWidth("16px");
		usernameCorrect.setVisible(false);
		mainPanel.add(usernameCorrect, marginData);
		
		usernameField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (usernameField.getValue() == null) { // 用户名为空
					
					usernameField.setInputStyleAttribute("border-color", "red");
					usernameCorrect.setVisible(false);
					usernameCheck.setText("用户名不能为空");
					usernameCheck.setVisible(true);
					flags[0] = false;
					
				} else {
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.checkUsernameValid(usernameField.getValue(), new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) { // 用户名可用
								usernameCorrect.setVisible(true);
								usernameCheck.setVisible(false);
								usernameField.setInputStyleAttribute("border-color", "#b5b8c8");
								flags[0] = true;
								
							} else { // 用户名不可用
								usernameCheck.setText("用户名已被注册");
								usernameCheck.setVisible(true);
								usernameCorrect.setVisible(false);
								usernameField.setInputStyleAttribute("border-color", "red");
								flags[0] = false;
							}
							
						} // End of onSuccess().
						
					});
				} // End of if.
				
			} // End of handleEvent().
			
		});
		
		passwordField = new TextField<String>();
		passwordField.setLabelSeparator("");
		passwordField.setPassword(true);
		passwordField.setFieldLabel("密    码");
		mainPanel.add(passwordField, formData);
		
		passwordCheck.setStyleAttribute("color", "red");
		passwordCheck.setStyleAttribute("margin-top", "5px");
		passwordCheck.setStyleAttribute("margin-bottom", "5px");
		passwordCheck.setVisible(false);
		mainPanel.add(passwordCheck, formData);
		
		passwordCorrect.setUrl(GWT.getHostPageBaseURL() + "images/correct.png");
		passwordCorrect.setHeight("16px");
		passwordCorrect.setWidth("16px");
		passwordCorrect.setVisible(false);
		mainPanel.add(passwordCorrect, marginData);
		
		passwordField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if ((passwordField.getValue() == null) || (passwordField.getValue().length() < 6)) { // 密码小于6位
					passwordCorrect.setVisible(false);
					passwordCheck.setText("密码应不少于6位");
					passwordCheck.setVisible(true);
					passwordField.setInputStyleAttribute("border-color", "red");
					flags[1] = false;
					
				} else { // 输入正确
					passwordCorrect.setVisible(true);
					passwordCheck.setVisible(false);
					passwordField.setInputStyleAttribute("border-color", "#b5b8c8");
					flags[1] = true;
				}
				
			}
			
		});
		
		// 密码确认
		pwdconfirmField = new TextField<String>();
		pwdconfirmField.setLabelSeparator("");
		pwdconfirmField.setPassword(true);
		pwdconfirmField.setFieldLabel("密码确认");
		mainPanel.add(pwdconfirmField, formData);
		
		pwdconfirmCheck.setStyleAttribute("color", "red");
		pwdconfirmCheck.setStyleAttribute("margin-top", "5px");
		pwdconfirmCheck.setStyleAttribute("margin-bottom", "5px");
		pwdconfirmCheck.setVisible(false);
		mainPanel.add(pwdconfirmCheck, formData);
		
		pwdconfirmCorrect.setUrl(GWT.getHostPageBaseURL() + "images/correct.png");
		pwdconfirmCorrect.setHeight("16px");
		pwdconfirmCorrect.setWidth("16px");
		pwdconfirmCorrect.setVisible(false);
		mainPanel.add(pwdconfirmCorrect, marginData);
	
		pwdconfirmField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (pwdconfirmField.getValue() == null) { // 确认密码为空
					pwdconfirmCheck.setText("须再输入一次密码");
					pwdconfirmCheck.setVisible(true);
					pwdconfirmCorrect.setVisible(false);
					pwdconfirmField.setInputStyleAttribute("border-color", "red");
					flags[2] = false;
					
				} else if (pwdconfirmField.getValue().equals(passwordField.getValue())) { // 两次密码输入一致
					
					pwdconfirmCorrect.setVisible(true);
					pwdconfirmCheck.setVisible(false);
					pwdconfirmField.setInputStyleAttribute("border-color", "#b5b8c8");
					flags[2] = true;
					
				} else { // 两次密码输入不一致
					
					pwdconfirmCheck.setText("两次密码输入不一致");
					pwdconfirmCheck.setVisible(true);
					pwdconfirmCorrect.setVisible(false);
					pwdconfirmField.setInputStyleAttribute("border-color", "red");
					flags[2] = false;
					
				}
				
			}
			
		});
		
		
		// 真实姓名
		truenameField = new TextField<String>();
		truenameField.setLabelSeparator("");
		truenameField.setFieldLabel("真实姓名");
		mainPanel.add(truenameField, formData);
		
		truenameCheck.setVisible(false);
		truenameCheck.setStyleAttribute("color", "red");
		truenameCheck.setStyleAttribute("margin-top", "5px");
		truenameCheck.setStyleAttribute("margin-bottom", "5px");
		mainPanel.add(truenameCheck, formData);
		
		truenameCorrect.setUrl(GWT.getHostPageBaseURL() + "images/correct.png");
		truenameCorrect.setHeight("16px");
		truenameCorrect.setWidth("16px");
		truenameCorrect.setVisible(false);
		mainPanel.add(truenameCorrect, marginData);

		truenameField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (truenameField.getValue() == null) { // 真实姓名为空
					
					truenameCheck.setText("真实姓名不能为空");
					truenameCheck.setVisible(true);
					truenameCorrect.setVisible(false);
					truenameField.setInputStyleAttribute("border-color", "red");
					flags[3] = false;
					
				} else { // 输入正确
					
					truenameCheck.setVisible(false);
					truenameCorrect.setVisible(true);
					truenameField.setInputStyleAttribute("border-color", "#b5b8c8");
					flags[3] = true;
				}
				
			}
			
		});
		
		// E-mail地址
		emailField = new TextField<String>();
		emailField.setLabelSeparator("");
		emailField.setFieldLabel("E-mail地址");
		mainPanel.add(emailField, formData);
		
		emailCheck.setStyleAttribute("color", "red");
		emailCheck.setStyleAttribute("margin-top", "5px");
		emailCheck.setStyleAttribute("margin-bottom", "5px");
		emailCheck.setVisible(false);
		mainPanel.add(emailCheck, formData);
		
		emailCorrect.setUrl(GWT.getHostPageBaseURL() + "images/correct.png");
		emailCorrect.setHeight("16px");
		emailCorrect.setWidth("16px");
		emailCorrect.setStyleName("imageMargin");
		emailCorrect.setVisible(false);
		mainPanel.add(emailCorrect, marginData);
		
		emailField.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				if (emailField.getValue() == null) { // E-mail输入为空
					emailCheck.setText("E-mail地址不能为空");
					emailCheck.setVisible(true);
					emailCorrect.setVisible(false);
					emailField.setInputStyleAttribute("border-color", "red");
					flags[4] = false;
					
				} else {
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.checkEmail(emailField.getValue(), new AsyncCallbackEx<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) { // E-mail地址格式合法
								
								emailCheck.setVisible(false);
								emailCorrect.setVisible(true);
								emailField.setInputStyleAttribute("border-color", "#b5b8c8");
								flags[4] = true;
								
							} else { // E-mail地址格式不合法
								emailCheck.setText("E-mail地址格式错误");
								emailCheck.setVisible(true);
								emailCorrect.setVisible(false);
								emailField.setInputStyleAttribute("border-color", "red");
								flags[4] = false;
								
							} // End of if.
							
						} // End of onSuccess().
						
					});
					
				} // End of if.
				
			} // End of handleEvent().
			
		});
		
	
		add(mainPanel);
		
	} // End of createForm().
	
	public void reset() {
		
		usernameField.setValue("");
		usernameCheck.setVisible(false);
		usernameCorrect.setVisible(false);
		
		passwordField.setValue("");
		passwordCheck.setVisible(false);
		passwordCorrect.setVisible(false);
		
		pwdconfirmField.setValue("");
		pwdconfirmCheck.setVisible(false);
		pwdconfirmCorrect.setVisible(false);
		
		truenameField.setValue("");
		truenameCheck.setVisible(false);
		truenameCorrect.setVisible(false);
		
		emailField.setValue("");
		emailCheck.setVisible(false);
		emailCorrect.setVisible(false);
	}
	
	@Override
	public void onButtonPressed(Button button) {
		if (button == getButtonBar().getItemByItemId(OK)) { // 确认按钮
			
			if (checkAllFields()) { // 输入框内容全部合法
				BnuMiner.getCurrent().fireAppEvent(AppEvents.CheckFormValid, null);
				
			} else {
				Info.display("错误", "表单尚未填完！");
			}
			
			

			
		} else if (button == getButtonBar().getItemByItemId(CANCEL)) {
			reset();
			hide();
		}
	} // End of onButtonPressed().
	
	/**
	 * 是否所有输入域的值均合法
	 * @return
	 */
	public boolean allFieldsValid() {
		boolean result = false;
		int count = 0;
		
			
			
		for (int i = 0; i < flags.length; ++i) {
			if (flags[i] == true)
				++count;
		}
		
		if (count == flags.length)
			result = true;
	

		return result;
	} // End of allFieldsValid().
	
	public boolean checkAllFields() {
		boolean result = true;
		
		// 检查用户名输入框
		if (!flags[0]) {
			
			if (usernameField.getValue() == null) { // 用户名为空
				
				usernameField.setInputStyleAttribute("border-color", "red");
				usernameCorrect.setVisible(false);
				usernameCheck.setText("用户名不能为空");
				usernameCheck.setVisible(true);
				flags[0] = false;
				result = false;
			} else {
				BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
				service.checkUsernameValid(usernameField.getValue(), new AsyncCallbackEx<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						if (result.booleanValue() == true) { // 用户名可用
							usernameCorrect.setVisible(true);
							usernameCheck.setVisible(false);
							usernameField.setInputStyleAttribute("border-color", "#b5b8c8");
							flags[0] = true;
							
						} else { // 用户名不可用
							usernameCheck.setText("用户名已被注册");
							usernameCheck.setVisible(true);
							usernameCorrect.setVisible(false);
							usernameField.setInputStyleAttribute("border-color", "red");
							flags[0] = false;
							result = false;
						}
						
					} // End of onSuccess().
					
				});
			} // End of if.
			
		}
		
		
		// 检查密码输入框
		if (!flags[1]) {
			
			if ((passwordField.getValue() == null) || (passwordField.getValue().length() < 6)) { // 密码小于6位
				passwordCorrect.setVisible(false);
				passwordCheck.setText("密码应不少于6位");
				passwordCheck.setVisible(true);
				passwordField.setInputStyleAttribute("border-color", "red");
				flags[1] = false;
				result = false;
				
			} else { // 输入正确
				passwordCorrect.setVisible(true);
				passwordCheck.setVisible(false);
				passwordField.setInputStyleAttribute("border-color", "#b5b8c8");
				flags[1] = true;
			}
		}
		
		// 检查密码确认框
		if (!flags[2]) {
			
			if (pwdconfirmField.getValue() == null) { // 确认密码为空
				pwdconfirmCheck.setText("须再输入一次密码");
				pwdconfirmCheck.setVisible(true);
				pwdconfirmCorrect.setVisible(false);
				pwdconfirmField.setInputStyleAttribute("border-color", "red");
				flags[2] = false;
				result = false;
				
			} else if (pwdconfirmField.getValue().equals(passwordField.getValue())) { // 两次密码输入一致
				
				pwdconfirmCorrect.setVisible(true);
				pwdconfirmCheck.setVisible(false);
				pwdconfirmField.setInputStyleAttribute("border-color", "#b5b8c8");
				flags[2] = true;
				
			} else { // 两次密码输入不一致
				
				pwdconfirmCheck.setText("两次密码输入不一致");
				pwdconfirmCheck.setVisible(true);
				pwdconfirmCorrect.setVisible(false);
				pwdconfirmField.setInputStyleAttribute("border-color", "red");
				flags[2] = false;
				result = false;
				
			}
		}
		
		// 检查真实姓名输入框
		if (!flags[3]) {
			
			if (truenameField.getValue() == null) { // 真实姓名为空
				
				truenameCheck.setText("真实姓名不能为空");
				truenameCheck.setVisible(true);
				truenameCorrect.setVisible(false);
				truenameField.setInputStyleAttribute("border-color", "red");
				flags[3] = false;
				result = false;
				
			} else { // 输入正确
				
				truenameCheck.setVisible(false);
				truenameCorrect.setVisible(true);
				truenameField.setInputStyleAttribute("border-color", "#b5b8c8");
				flags[3] = true;
			}
		}
		
		// 检查E-mail输入框
		if (!flags[4]) {
			
			if (emailField.getValue() == null) { // E-mail输入为空
				emailCheck.setText("E-mail地址不能为空");
				emailCheck.setVisible(true);
				emailCorrect.setVisible(false);
				emailField.setInputStyleAttribute("border-color", "red");
				flags[4] = false;
				result = false;
				
			} else {
				BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
				service.checkEmail(emailField.getValue(), new AsyncCallbackEx<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						if (result.booleanValue() == true) { // E-mail地址格式合法
							
							emailCheck.setVisible(false);
							emailCorrect.setVisible(true);
							emailField.setInputStyleAttribute("border-color", "#b5b8c8");
							flags[4] = true;
							
						} else { // E-mail地址格式不合法
							emailCheck.setText("E-mail地址格式错误");
							emailCheck.setVisible(true);
							emailCorrect.setVisible(false);
							emailField.setInputStyleAttribute("border-color", "red");
							flags[4] = false;
							result = false;
							
						} // End of if.
						
					} // End of onSuccess().
					
				});
				
			} // End of if.
		}
		
		return result;
	} // End of checkAllFields().
	
	
}
