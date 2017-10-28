package org.bnuminer.client;
/**
 * r39: 添加根据登录用户权限显示管理面板功能（2010-04-28 11:47）
 */
import org.bnuminer.client.dialog.LoginDialog;
import org.bnuminer.client.dialog.UploadDialog;
import org.bnuminer.client.filelist.FilelistGrid;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;
import org.bnuminer.client.widget.LoadingPanel;
import org.bnuminer.miner.action.ExperimenterAction;
import org.bnuminer.miner.action.ExplorerAction;
import org.bnuminer.miner.action.FileManageAction;
import org.bnuminer.miner.action.LogoutAction;
import org.bnuminer.miner.action.PreferencesAction;
import org.bnuminer.miner.action.SimpleCLIAction;
import org.bnuminer.miner.action.StudentFlowAction;
import org.bnuminer.miner.action.SystemAction;
import org.bnuminer.miner.action.UploadAction;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BnuMiner implements EntryPoint {
	private String current_user_name;
	private String last_login_datetime;
	private Viewport viewport;
	private MinerPanel minerPanel;
	private static BnuMiner current;
	private LoadingPanel loadingPanel;
	private LoginDialog loginDialog;
	private HeaderPanel headerPanel;
	private UploadDialog uploadDialog;
	private FilelistGrid filelistGrid;
	//private ExplorerWindow explorer;	
	//private ContentPanel desktop;
	
	//private int desktop_state;
	
	/*private Listener<BaseEvent> uploadListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			unmask();
			MessageBox.alert("成功！", "文件上传成功！", null);
		}
	};
	*/
	
	private Listener<BaseEvent> adminButtonListener = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			
			BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
			service.isAdmin(new AsyncCallbackEx<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					if (result.booleanValue() == true) {
						// 文件上传
						minerPanel.addButton("管理", new UploadAction(), true);
						// 文件管理
						minerPanel.addButton("管理", new FileManageAction(), true);
						// 用户首选项
						minerPanel.addButton("管理", new PreferencesAction(), false);
						// 管理员权限
						minerPanel.addButton("管理", new SystemAction(), true);
						// 退出登录
						minerPanel.addButton("管理", new LogoutAction(), true);
						
					} else {
						
						// 文件上传
						minerPanel.addButton("管理", new UploadAction(), true);
						// 文件管理
						minerPanel.addButton("管理", new FileManageAction(), true);
						// 用户首选项
						minerPanel.addButton("管理", new PreferencesAction(), false);
						// 退出登录
						minerPanel.addButton("管理", new LogoutAction(), true);
					}
				}
				
			});
			
			
			
		}
		
	};
	
	/**
	 * EntryPoint入口处
	 */
	public void onModuleLoad() {
		current = this;
		current_user_name = new String();
		last_login_datetime = new String();
		loginDialog = new LoginDialog();
				
		loginDialog.show();
		
		viewport = new Viewport();
		viewport.setLayout(new BorderLayout());
		loadingPanel = new LoadingPanel(viewport);
		loadingPanel.loadingBegin();
		createHeader();
		setWelcome();
		
		addAppListener(AppEvents.LoginSuccess, adminButtonListener);
		createMinerBar();
		
		createFilelist();
		RootPanel.get().add(viewport);
		loadingPanel.loadingEnd();
	}
	
	/**
	 * 功能：创建主界面的顶部
	 */
	private void createHeader() {
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 100);
		northData.setMargins(new Margins(0, 5, 0, 5));
		headerPanel = new HeaderPanel();
		headerPanel.setImage("./images/logo.jpg");
		headerPanel.setSiteTitle("北师大计算机系数据挖掘网络平台");
		headerPanel.addWelcomeLabel();
		headerPanel.setLabels();
		
		viewport.add(headerPanel, northData);
	}
	
	/**
	 * 创建主界面的桌面
	 */
	/*private void createDesktop(){
		BnuMiner.getCurrent().addAppListener(AppEvents.UploadSuccess, uploadListener);
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(5));
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		// Add the operation buttons.
		HorizontalPanel hPanel1 = new HorizontalPanel();
		Button openButton = new Button("打开...");
		Button editButton = new Button("编辑...");
		Button saveButton = new Button("保存...");
		hPanel1.add(openButton);
		hPanel1.add(editButton);
		hPanel1.add(saveButton);
		vPanel.add(hPanel1);
		
		// Create the status statistics.
		HorizontalPanel hPanel2 = new HorizontalPanel();
		
		// The Current Relation Part.
		Window window = new Window();
		window.setLayout(new VBoxLayout());
		window.setSize(200, 200);
		window.setHeading("当前关系");
		window.setClosable(false);
		window.setResizable(false);

		HorizontalPanel first_leftPanel = new HorizontalPanel();
		Label relation = new Label("关系名称：");
		Label rel_name = new Label();
		Label attributes = new Label("属性总数：");
		Label rel_attributes = new Label();
		first_leftPanel.add(relation);
		first_leftPanel.add(rel_name);
		first_leftPanel.add(attributes);
		first_leftPanel.add(rel_attributes);
		window.add(first_leftPanel);
		HorizontalPanel second_leftPanel = new HorizontalPanel();
		Label instances = new Label("样本个数：");
		Label rel_instances = new Label();
		Label weights = new Label("权重总和：");
		Label sum_of_weights = new Label();
		second_leftPanel.add(instances);
		second_leftPanel.add(rel_instances);
		second_leftPanel.add(weights);
		second_leftPanel.add(sum_of_weights);
		window.add(second_leftPanel);
		hPanel2.add(window);
		
		vPanel.add(hPanel2);
		
		
		panel.add(vPanel);
		viewport.add(panel, centerData);
	}*/
	
	
	/*public void createDesktop(String flag) {
		if (flag == null) { // 初始登录时
			
			createFilelist();
			desktop_state = 1;
			
		} else if (flag.equals("Explorer")) { // 选择Explorer界面
			
			// 除去文件列表s
			desktop.removeAll();
			
			BorderLayoutData borderData = new BorderLayoutData(LayoutRegion.CENTER);
			borderData.setMargins(new Margins(5));
			
			// 总版面
			desktop = new ContentPanel();
			
			// 工具栏(可设为BnuMiner之全局对象，以免每次触发事件均加载一次)
			MinerToolBar toolBar = new MinerToolBar();
			toolBar.addButton(new UndoOperation(), false);
			toolBar.addButton(new RedoOperation(), false);
			toolBar.addSeparatorToolItem();
			toolBar.addButton(new CopyOperation(), false);
			toolBar.addButton(new CutOperation(), false);
			toolBar.addButton(new PasteOperation(), false);
			desktop.setTopComponent(toolBar);
			
			// 主界面 
			explorer = new ExplorerDesktop();
				
			// 预处理
			explorer.addTabItem("预处理", true);
			ContentPanel preprocessPanel = explorer.addContentPanel("预处理");
			VerticalPanel mainPanel = new VerticalPanel();
			HorizontalPanel topPanel = new HorizontalPanel();
			Button openFile = new Button("打开在线文档...");
			topPanel.add(openFile);
			
			openFile.addListener(Events.Select, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
					service.openFile("test.arff", new AsyncCallbackEx<String>() {
						@Override
						public void onSuccess(String result) {
							if (result != null) {
								try {
									explorer.setInstances(DataSource.read(result));
									explorer.enableTabItem("聚类");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								MessageBox.alert("错误", "文件不存在", null);
							}
						}
					});
				}
			});
			topPanel.add(openFile);
			
			mainPanel.add(topPanel);
			preprocessPanel.add(mainPanel);
			
			explorer.addTabItem("分类", false);
			explorer.addTabItem("聚类", false);
			explorer.addTabItem("Associate", false);
			explorer.addTabItem("特征提取", false);
			explorer.addTabItem("可视化", false);
			desktop.add(explorer);
			
			viewport.add(desktop, borderData);
			desktop_state = 2;
			
		} else if (flag.equals("Experimenter")) { // 选择Experimenter界面
			desktop_state = 3;
		} else if (flag.equals("KnowledgeFlow")) { // 选择KnowledgeFlow界面
			desktop_state = 4;
		} else if (flag.equals("Simple CLI")) { // 选择Simple CLI界面
			desktop_state = 5;
		} // End of if.
	} // End of createDesktop().
	*/
	private void createFilelist() {
		//BnuMiner.getCurrent().addAppListener(AppEvents.UploadSuccess, uploadListener);
		
		BorderLayoutData borderData = new BorderLayoutData(LayoutRegion.CENTER);
		borderData.setMargins(new Margins(5));
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		filelistGrid = FilelistGrid.create();
		panel.add(filelistGrid);
		PagingToolBar toolBar = new PagingToolBar(50);
		final PagingLoader<?> loader = (PagingLoader<?>) filelistGrid.getStore().getLoader();
		toolBar.bind(loader);
		panel.setBottomComponent(toolBar);
		
		/*filelistGrid.addListener(Events.Attach, new Listener<GridEvent<BaseModel>>() {

			@Override
			public void handleEvent(GridEvent<BaseModel> be) {
				// TODO Auto-generated method stub
				PagingLoadConfig config = new BasePagingLoadConfig();
				config.setOffset(0);
				config.setLimit(50);
				
				Map<String, Object> state = filelistGrid.getState();
				if (state.containsKey("offset")) {
					int offset = (Integer) state.get("offset");
					int limit = (Integer) state.get("limit");
					config.setOffset(offset);
					config.setLimit(limit);
				}
				
				if (state.containsKey("sortField")) {
					config.setSortField((String) state.get("sortField"));
					config.setSortDir(SortDir.valueOf((String) state.get("sortDir")));
				}
				loader.load(config);
			}
			
		});*/
		
		viewport.add(panel, borderData);
	}

	public FilelistGrid getFileListGrid() {
		return filelistGrid;
	}
	
	/**
	 * 创建主界面的左侧工具栏
	 */
	private void createMinerBar() {
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 120);
		westData.setMargins(new Margins(5));
		westData.setSplit(true);
		westData.setCollapsible(true);
		
		minerPanel = new MinerPanel("功能面板");
		minerPanel.setBodyBorder(false);
		minerPanel.setAutoHeight(false);
		minerPanel.setHeight(200);
		
		minerPanel.addPanel("工作方式");
		// Explorer Application
		minerPanel.addButton("工作方式", new ExplorerAction(), true);
		// Experimenter Application
		minerPanel.addButton("工作方式", new ExperimenterAction(), false);
		// KnowledgeFlow Application
		//minerPanel.addButton("工作方式", new KnowledgeFlowAction(), true);
		// Simple CLI Application
		minerPanel.addButton("工作方式", new SimpleCLIAction(), false);
		
		minerPanel.addPanel("挖掘模块");
		// 学生信息挖掘模块
		minerPanel.addButton("挖掘模块", new StudentFlowAction(), true);
		
		minerPanel.addPanel("管理");
		
		
		viewport.add(minerPanel, westData);
	}
	
	
	
	/**
	 * 功能：获取当前之BnuMiner对象
	 * @return 当前的BnuMiner对象
	 */
	public static BnuMiner getCurrent() {
		return current;
	}
	
	/**
	 * 功能：触发系统全局事件
	 * @param type - 事件类型
	 * @param event - 事件名称
	 */
	public void fireAppEvent(EventType type, BaseEvent event) {
		if (event == null) {
			viewport.fireEvent(type);
		} else {
			viewport.fireEvent(type, event);
		}
	}
	
	/**
	 * 功能：注册全局事件的监听函数
	 * @param type - 事件类型
	 * @param listener - 监听函数
	 */
	public void addAppListener(EventType type, Listener<BaseEvent> listener) {
		viewport.addListener(type, listener);
	}
	/**
	 * 功能：添加遮蔽效果
	 */
	public void mask() {
		viewport.el().mask("请稍候...");
	}
	
	/**
	 * 功能：去除遮蔽效果
	 */
	public void unmask() {
		viewport.el().unmask();
	}
	
	/**
	 * 功能：退出登录
	 */
	public void logout() {
		if (Window.confirm("确认退出吗？")) {
			
			BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
			service.logout(new AsyncCallbackEx<Object>() {
				@Override
				public void onSuccess(Object result) {
					headerPanel.setWelcomeInfo(null);
					loginDialog.setAuthImage();
					loginDialog.show();
					minerPanel.clearPanel("管理");
					BnuMiner.getCurrent().fireAppEvent(AppEvents.LogoutSuccess, null);
				}
			});
		} // End of if.
		
	} // End of logout().
	
	public String getCurrent_user_name() {
		return current_user_name;
	}
	
	public void setCurrent_user_name(String current_user_name) {
		this.current_user_name = current_user_name;
	}
	
	public String getLast_login_datetime() {
		return last_login_datetime;
	}
	
	public void setLast_login_datetime(String last_login_datetime) {
		this.last_login_datetime = last_login_datetime;
	}
	
	public void setWelcome() {
		BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
		service.getWelcomeinfo(new AsyncCallbackEx<String>() {
			@Override
			public void onSuccess(String result) {
				headerPanel.setWelcomeInfo(result);
			}
		});
	}
	
	public void upload() {
		uploadDialog = new UploadDialog();
		uploadDialog.show();
	}
	
	public MinerPanel getMinerPanel() {
		return minerPanel;
	}
	
	
}
