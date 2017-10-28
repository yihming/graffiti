package org.bnuminer.client.sql;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class OpenUrlDialog extends Dialog {
	private static OpenUrlDialog current;
	private String url_base;
	private TextField<String> input_url;
	
	public OpenUrlDialog() {
		current = this;
		setHeading("打开URL");
		setLayout(new FitLayout());
		setModal(true);
		setResizable(false);
		setClosable(false);
		setWidth(400);
		setHeight(300);
		
		VerticalPanel vPanel = new VerticalPanel();
		
		input_url = new TextField<String>();
		input_url.setFieldLabel("源文件的URL: ");
		input_url.setEmptyText("http://");
		input_url.setAllowBlank(false);
		
		vPanel.add(input_url);
		
		add(vPanel);
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("确定");
		getButtonById(Dialog.CANCEL).setText("取消");
		
	}
	
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) { // OK
			
			
			
		} else if (Dialog.CANCEL.equals(button.getItemId())) { // CANCEL
			reset();
			hide();
		}
	}
	
	public OpenUrlDialog getCurrent() {
		return current;
	}
	
	
	public String getUrl_base() {
		return url_base;
	}
	
	public void setUrl_base(String url_base) {
		this.url_base = url_base;
	}
	
	public void reset() {
		url_base = null;
		input_url.setValue(null);
	}
}
