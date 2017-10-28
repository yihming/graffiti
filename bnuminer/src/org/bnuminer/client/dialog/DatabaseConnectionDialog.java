package org.bnuminer.client.dialog;


import com.extjs.gxt.ui.client.widget.Dialog;

import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


public class DatabaseConnectionDialog extends Dialog {
	private DatabaseConnectionDialog current;
	private TextField<String> m_DbURLText;
	private TextField<String> m_UsernameText;
	private TextField<String> m_PasswordText;
	
	private int m_ReturnValue;
	
	public DatabaseConnectionDialog() {
		this("", "");
	}
	
	public DatabaseConnectionDialog(String url, String uname) {
		current = this;
		m_ReturnValue = 0;
		setHeading("数据库连接参数");
		setLayout(new FitLayout());
		setResizable(false);
		setClosable(false);
		setModal(true);
		setWidth(600);
		setHeight(700);
		
		setConnection(url, uname);
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("确定");
		getButtonById(Dialog.CANCEL).setText("取消");
		
		
	}
	
	public DatabaseConnectionDialog getCurrent() {
		return current;
	}
	
	public String getURL() {
		return m_DbURLText.getValue();
	}
	
	public String getUsername() {
		return m_UsernameText.getValue();
	}
	
	public String getPassword() {
		return m_PasswordText.getValue();
	}
	
	public void setConnection(String url, String uname) {
		VerticalPanel DbP = new VerticalPanel();
		
		m_DbURLText = new TextField<String>();
		m_DbURLText.setWidth(50);
		m_DbURLText.setValue(url);
		m_DbURLText.setFieldLabel("数据库URL ");
		
		m_UsernameText = new TextField<String>();
		m_UsernameText.setWidth(25);
		m_UsernameText.setValue(uname);
		m_UsernameText.setFieldLabel("用户名 ");
		
		m_PasswordText = new TextField<String>();
		m_PasswordText.setWidth(25);
		m_PasswordText.setFieldLabel("密码 ");
		m_PasswordText.setPassword(true);
		
		DbP.add(m_DbURLText);
		DbP.add(m_UsernameText);
		DbP.add(m_PasswordText);
		
	}
	
	public int getReturnValue() {
		return m_ReturnValue;
	}
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) { // OK
			m_ReturnValue = 1;
			hide();
			
		} else if (Dialog.CANCEL.equals(button.getItemId())) { // CANCEL
			m_ReturnValue = 0;
			hide();
		}
	}
	
	
}
