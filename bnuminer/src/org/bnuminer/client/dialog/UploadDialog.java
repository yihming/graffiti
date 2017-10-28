package org.bnuminer.client.dialog;

import java.util.List;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.AsyncCallbackEx;
import org.bnuminer.client.BnuMiner;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class UploadDialog extends Dialog {
	
	private FileUploadField fileUpload;
	private final FormPanel formPanel;
	private List<String> validType;
	private final Label fileUploadCheck = new Label();
	private final Image fileUploadCorrect = new Image();
	private final Label tips = new Label();
	
	public UploadDialog() {
		setStyleAttribute("margin", "10px");
		initValidType();
		setHeading("文件上传");
		setLayout(new VBoxLayout());
		setModal(true);
		setResizable(false);
		setClosable(false);
		setWidth(400);
		setHeight(300);
		
		formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setFrame(true);
		formPanel.setLayout(new FormLayout());
		formPanel.setLabelWidth(50);
		formPanel.setHeaderVisible(false);
		formPanel.setAction(GWT.getHostPageBaseURL() + "bnuminer/uploadfile");
		formPanel.setMethod(Method.POST);
		formPanel.setEncoding(Encoding.MULTIPART);
		
		
		FormData formData = new FormData("-20");
		
		MarginData marginData = new MarginData(5, 0, 5, 20);
		
		
		//String tips_content = "上传文件格式须为xls, arff, xrff, csv或data格式";
		tips.setStyleAttribute("font-size", "13");
		tips.setStyleAttribute("color", "grey");
		tips.setVisible(false);
		
		// 上传文件框
		fileUpload = new FileUploadField();
		fileUpload.setLabelSeparator("");
		fileUpload.setAllowBlank(false);
		fileUpload.setName("file");
		fileUpload.setFieldLabel("文件上传路径");

		// 校验上传文件用
		fileUploadCheck.setStyleAttribute("color", "red");
		fileUploadCheck.setVisible(false);
		
		// 校验正确图像
		fileUploadCorrect.setUrl(GWT.getHostPageBaseURL() + "images/correct.png");
		fileUploadCorrect.setHeight("16px");
		fileUploadCorrect.setWidth("16px");
		fileUploadCorrect.setVisible(false);
		
		
		formPanel.add(tips, formData);
		formPanel.add(fileUpload, formData);
		formPanel.add(fileUploadCheck, formData);
		formPanel.add(fileUploadCorrect, marginData);
		
		add(formPanel, formData);
		
		// 添加上传完成后的事件处理
		formPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			
			@Override
			public void handleEvent(FormEvent fe) {
				hide();
				MessageBox.info("上传结果", fe.getResultHtml(), null);
				BnuMiner.getCurrent().fireAppEvent(AppEvents.UploadSuccess, null);
				
			}
			
		});
		
		setButtons(Dialog.OKCANCEL);
		getButtonById(Dialog.OK).setText("开始上传");
		getButtonById(Dialog.CANCEL).setText("取消");
	} // End of constructor.
	
	/**
	 * 初始化允许上传的文件类型
	 */
	private void initValidType() {
		
		BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
		service.getValidTypeList(new AsyncCallbackEx<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				validType = result;
				
				StringBuilder tips_content = new StringBuilder();
				tips_content.append("上传文件须为  ");
				for (int i = 0; i < validType.size(); ++i) {
					if (i != validType.size() - 1) {
						tips_content.append(validType.get(i)).append(", ");
					} else {
						tips_content.append(validType.get(i)).append(" ");
					}
				}
				tips_content.append("格式");
				tips.setText(tips_content.toString());
				tips.setVisible(true);
			}
			
		});
		
	} // End of initValidType().
	
	/**
	 * 添加合法文件类型
	 * @param type
	 * @return boolean - true:插入成功
	 *                   false:已存在该类型
	 */
	public boolean addValidType(String type) {
		if (!validType.contains(type)) {
			validType.add(type);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 去除指定的合法文件类型
	 * @param type
	 * @return boolean - true:删除成功
	 *                   false:不存在该类型
	 */
	public boolean removeValidType(String type) {
		return validType.remove(type);
	}
	
	/**
	 * 重置上传文件
	 */
	public void reset() {
		fileUpload.setValue(null);
	}
	
	/**
	 * 设置按钮反应事件函数
	 */
	@Override
	public void onButtonPressed(Button button) {
		if (Dialog.OK.equals(button.getItemId())) { // 开始上传
			
			
			if (fileUpload.getValue() == null) { // 未指定文件
				
				fileUploadCorrect.setVisible(false);
				fileUploadCheck.setText("请选择一个文件");
				fileUploadCheck.setVisible(true);
				fileUpload.setInputStyleAttribute("border-color", "red");
				
				return;
				
			}
			
			if (fileUpload.getValue().contains(".")) { // 存在扩展名
				
				int typeIndex = fileUpload.getValue().lastIndexOf(".");
				String type = fileUpload.getValue().substring(typeIndex + 1);
				
				if (validType.contains(type)) { // 文件类型合法
					
					fileUploadCheck.setVisible(false);
					fileUpload.setInputStyleAttribute("border-color", "#b5b8c8");
					fileUploadCorrect.setVisible(true);
					
					formPanel.submit();
					
					
				} else { // 文件类型不合法
					fileUploadCheck.setText("文件格式不合法，请重新选择");
					fileUploadCheck.setVisible(true);
					fileUploadCorrect.setVisible(false);
					fileUpload.setInputStyleAttribute("border-color", "red");
				}
				
				
			} else { // 无扩展名
				
				fileUploadCheck.setText("文件格式不合法，请重新选择");
				fileUploadCheck.setVisible(true);
				fileUploadCorrect.setVisible(false);
				fileUpload.setInputStyleAttribute("border-color", "red");
				
			} // End of if.
			
			
		} else if (Dialog.CANCEL.equals(button.getItemId())) { // 取消上传
			reset();
			hide();
		} // End of if.
		
	} // End of onButtonPressed().
	

}
