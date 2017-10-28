package org.bnuminer.client;
/**
 *  r13: 2010-03-06 10:26 John Yung: Establish the Class.
 */
import java.util.HashMap;
import java.util.Map;

import org.bnuminer.miner.action.MinerAction;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;

/**
 * 主界面之左侧功能面板，派生自ContentPanel
 * @author John Yung
 *
 */
public class MinerPanel extends ContentPanel {
	private Map<String, ContentPanel> panelMap;
	
	/**
	 * 创建功能面板
	 * @param title - 面板标题名
	 */
	public MinerPanel(String title) {
		panelMap = new HashMap<String, ContentPanel>();
		setHeading(title);
		setLayout(new AccordionLayout());
	}
	
	/**
	 * 添加子面板
	 * @param title - 子面板标题
	 * @return - 子面板对象（ContentPanel）
	 */
	public ContentPanel addPanel(String title) {
		ContentPanel panel = new ContentPanel();
		panel.setHeading(title);
		VBoxLayout vLayout = new VBoxLayout();
		vLayout.setPadding(new Padding(5));
		vLayout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
		panel.setLayout(vLayout);
		add(panel);
		panelMap.put(title, panel);
		return panel;
	}
	
	/**
	 * 添加按钮
	 * @param title - 按钮所属子面板之标题
	 * @param action - 按钮所触发之活动
	 */
	public void addButton(String title, MinerAction action, boolean state) {
		ContentPanel panel = panelMap.get(title);
		Button button = new Button();
		button.setEnabled(state);
		button.setIconAlign(IconAlign.TOP);
		button.setScale(ButtonScale.LARGE);
		button.setAutoWidth(false);
		button.setWidth(70);
		button.setIcon(action.getIcon());
		button.setText(action.getText());
		button.addListener(Events.Select, action);
		panel.add(button, new VBoxLayoutData(new Margins(0, 0, 5, 0)));
	}
	
	/**
	 * 清空子面板内容
	 * @param title - 子面板之标题
	 */
	public void clearPanel(String title) {
		ContentPanel panel = panelMap.get(title);
		panel.removeAll();
		
	}
}
