package org.bnuminer.server;
/**
 * r39: 添加isAdmin()函数（2010-04-28 11:48）
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bnuminer.DbConnection;
import org.bnuminer.Functions;
import org.bnuminer.UserSession;
import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.dao.ConfigsDAO;
import org.bnuminer.dao.FileDAO;
import org.bnuminer.dao.UserDAO;
import org.bnuminer.dao.User_file_infoDAO;
import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;
import org.bnuminer.vo.UserInfoVO;
import org.bnuminer.vo.User_file_infoVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class BnuMinerServiceImpl extends RemoteServiceServlet implements BnuMinerService {
	
	/**
	 * 用户输入登录信息后，系统进行处理
	 * @return Integer - 0: 登录成功
	 *                   1: 用户名或密码错误
	 *                   2: 验证码错误
	 */
	@Override
	public Integer login(String userName, String password, String authCode) {
		UserSession userSession = new UserSession();
		String password_md5 = Functions.md5(password);
		DbConnection dbc = new DbConnection();
		PreparedStatement pst;
		Integer result = new Integer(1);
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_info WHERE user_name = ? AND user_pwd = ? AND user_pri != 'pending' ");
			pst.setString(1, userName);
			pst.setString(2, password_md5);
			
			
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();
				if (dbc.rs.next()) { // 用户信息输入正确
					String correctAuthCode = (String) getThreadLocalRequest().getSession().getAttribute("rand");
			
					
					if (authCode.equals(correctAuthCode)) { // 登录成功，设置欢迎信息
						
						userSession.setUser_id(dbc.rs.getInt("user_id"));
						userSession.setUser_name(dbc.rs.getString("user_name"));
						userSession.setUser_true_name(dbc.rs.getString("user_true_name"));
						userSession.setUser_pri(dbc.rs.getString("user_pri"));
						userSession.setUser_signup(dbc.rs.getString("user_signup").substring(0, dbc.rs.getString("user_signup").lastIndexOf(".")));
						
						getThreadLocalRequest().getSession().setAttribute("UserSession", userSession);
						String login_time = Functions.getCurrentDatetime();
						String welcomeinfo = "欢迎    " + userSession.getUser_true_name() + "  的到来 ！您的登录时间为    " + login_time + "  .";
						getThreadLocalRequest().getSession().setAttribute("welcomeinfo", welcomeinfo);
						result = Integer.valueOf(0);
						
					} else { // 验证码错误
						result = Integer.valueOf(2);
					}
					
					
				} else { // 用户信息输入错误
					result = Integer.valueOf(1);
				}
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	
		
	} // End of login().
	
	/**
	 * 用户注销时，进行处理
	 */
	@Override
	public void logout() {
		
		getThreadLocalRequest().getSession().invalidate();
	}
	
	@Override
	public String getWelcomeinfo() {
		String welcomeinfo = (String) getThreadLocalRequest().getSession().getAttribute("welcomeinfo");
		return welcomeinfo;
	}

	
	/**
	 * 检查用户信息是否在Session中，用以显示相应的欢迎信息
	 * @return Boolean - true: 用户信息已存在
	 * 				     false: 用户信息不存在
	 */
	@Override
	public Boolean getUserInfo() {
		if (getThreadLocalRequest().getSession().getAttribute("UserSession") != null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	} // End of getUserInfo().
	
	/**
	 * 检查当前用户是否为超级管理员
	 */
	@Override
	public Boolean isAdmin() {
		Boolean result = Boolean.FALSE;
		
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		String user_pri = user.getUser_pri();
		
		if ("admin".equals(user_pri))
			result = Boolean.TRUE;
		
		return result;
		
	} // End of isAdmin().
	
	/**
	 * 分页显示文件列表信息
	 */
	public BasePagingLoadResult<BaseModel> getFilelist(PagingLoadConfig config) {
		List<User_file_infoVO> fileList = new ArrayList<User_file_infoVO>();
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		int user_id = user.getUser_id();
		User_file_infoDAO user_file_infodao = new User_file_infoDAO();
		fileList = user_file_infodao.getSearchItemsByUserId(user_id);
		
		List<User_file_infoVO> subFilelist = new ArrayList<User_file_infoVO>();
		List<BaseModel> result = new ArrayList<BaseModel>();
		
		for (int i = config.getOffset(); i < fileList.size(); ++i) {
			subFilelist.add(fileList.get(i));
			if (subFilelist.size() > config.getOffset()) 
				break;
		}
		
		for (User_file_infoVO user_file_infovo : subFilelist) {
			BaseModel m = new BaseModel();
			
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(user_file_infovo.getFile_id());
			
			m.set("file_id", user_file_infovo.getFile_id());
			m.set("user_file_info_id", user_file_infovo.getUser_file_info_id());
			m.set("is_owner", user_file_infovo.getIs_owner());
			/*m.set("file_name", user_file_infovo.getFileinfovo().getFile_name());
			m.set("file_create_time", user_file_infovo.getFileinfovo().getFile_create_time().substring(0, user_file_infovo.getFileinfovo().getFile_create_time().lastIndexOf(".")));
			m.set("file_modify_time", user_file_infovo.getFileinfovo().getFile_modify_time().substring(0, user_file_infovo.getFileinfovo().getFile_modify_time().lastIndexOf(".")));
			m.set("file_size", user_file_infovo.getFileinfovo().getFile_size());
			m.set("file_type", user_file_infovo.getFileinfovo().getFile_type());*/
			m.set("file_name", fileinfovo.getFile_name());
			m.set("file_create_time", fileinfovo.getFile_create_time().substring(0, fileinfovo.getFile_create_time().lastIndexOf(".")));
			m.set("file_modify_time", fileinfovo.getFile_modify_time().substring(0, fileinfovo.getFile_modify_time().lastIndexOf(".")));
			m.set("file_size", fileinfovo.getFile_size());
			m.set("file_type", fileinfovo.getFile_type());
			m.set("owner_name", user_file_infodao.getOwnerName(user_file_infovo.getFile_id()));
			
			result.add(m);
		}
		
		return new BasePagingLoadResult<BaseModel>(result, config.getOffset(), fileList.size());
	}
	
	/**
	 * 在ExplorerWindow中打开上传文件
	 * @deprecated 请使用PrepServiceImpl中的对应方法
	 * @param file_id - 文件ID
	 * @return 打开文件的内容
	 */
	@Override
	public String openFile(int file_id) {
		String return_info = new String();
		
		if (file_id == -1)
			return null;
		
		UserSession user_info = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		Integer user_id = Integer.valueOf(user_info.getUser_id());
		
		ConfigsDAO configsdao = new ConfigsDAO();
		ConfigsVO configsvo = configsdao.getConfig("UploadPath");
		
		FileDAO filedao = new FileDAO();
		FileInfoVO fileinfovo = filedao.getSearchFileById(file_id);
		String fileName = fileinfovo.getFile_name();
		
		String path = "";
		
		User_file_infoDAO user_file_infodao = new User_file_infoDAO();
		
		if (user_file_infodao.checkOwner(user_id, file_id)) { // 当前用户是该文件之所有者
			
			path = configsvo.getContent() + "/" + user_id.toString() + "/" + fileName;
			
			try {
				Instances data = DataSource.read(path);
				return_info = data.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else { // 当前用户不是该文件之所有者
			int owner_id = user_file_infodao.getOwnerIdByFileId(file_id);
			
			path = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/" + fileName;
			
			try {
				Instances data = DataSource.read(path);
				return_info = data.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return return_info;
	} // End of openFile().
	
	/**
	 * 为OpenFileDialog获取文件列表
	 * @return 文件列表List<BaseModel>
	 */
	@Override
	public List<BaseModel> getSimpleFilelist() {
		List<BaseModel> result = new ArrayList<BaseModel>();
		List<User_file_infoVO> fileList = new ArrayList<User_file_infoVO>();
		
		UserSession user_info = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		int user_id = user_info.getUser_id();
		
		User_file_infoDAO user_file_infodao = new User_file_infoDAO();
		fileList = user_file_infodao.getSearchItemsByUserId(user_id);
		
		for (User_file_infoVO user_file_infovo : fileList) {
			BaseModel m = new BaseModel();
			
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(user_file_infovo.getFile_id());
			
			m.set("file_id", user_file_infovo.getFile_id());
			m.set("user_file_id", user_file_infovo.getUser_file_info_id());
			m.set("is_owner", user_file_infovo.getIs_owner());
			/*m.set("file_name", user_file_infovo.getFileinfovo().getFile_name());
			m.set("file_create_time", user_file_infovo.getFileinfovo().getFile_create_time().substring(0, user_file_infovo.getFileinfovo().getFile_create_time().lastIndexOf(".")));
			m.set("file_modify_time", user_file_infovo.getFileinfovo().getFile_modify_time().substring(0, user_file_infovo.getFileinfovo().getFile_modify_time().lastIndexOf(".")));
			m.set("file_size", user_file_infovo.getFileinfovo().getFile_size());
			m.set("file_type", user_file_infovo.getFileinfovo().getFile_type());*/
			m.set("file_name", fileinfovo.getFile_name());
			m.set("file_create_time", fileinfovo.getFile_create_time().substring(0, fileinfovo.getFile_create_time().lastIndexOf(".")));
			m.set("file_modify_time", fileinfovo.getFile_modify_time().substring(0, fileinfovo.getFile_modify_time().lastIndexOf(".")));
			m.set("file_size", fileinfovo.getFile_size());
			m.set("file_type", fileinfovo.getFile_type());
			m.set("owner_name", user_file_infodao.getOwnerName(user_file_infovo.getFile_id()));
			
			result.add(m);
		}
		
		return result;
	} // End of getSimpleFilelist().
	
	/**
	 * 返回打开文件的统计信息
	 */
	@Override
	public Map<String, Object> prep_statistics(int file_id) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		int user_id = user.getUser_id();
		
		ConfigsDAO configsdao = new ConfigsDAO();
		ConfigsVO configsvo = configsdao.getConfig("UploadPath");
		
		
		FileDAO filedao = new FileDAO();
		FileInfoVO fileinfovo = filedao.getSearchFileById(file_id);
		
		String path = configsvo.getContent() + "/" + String.valueOf(user_id) + "/" + fileinfovo.getFile_name();
		
		try {
			Instances data = DataSource.read(path);
			
			result.put("preprocess_relation", data.relationName());
			result.put("preprocess_attributes", Integer.valueOf(data.numAttributes()));
			result.put("preprocess_instances", Integer.valueOf(data.numInstances()));
			result.put("preprocess_weight", Double.valueOf(data.sumOfWeights()));
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 获取当前用户的文件上传目录
	 * @return 路径
	 */
	@Override
	public String getCurrentUserUploadPath() {
		String path;
		
		UserSession usersession = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		String user_id = String.valueOf(usersession.getUser_id());
		
		ConfigsDAO configsdao = new ConfigsDAO();
		ConfigsVO configsvo = configsdao.getConfig("UploadPath");
		
		path = configsvo.getContent();
		path += "/" + user_id + "/";
		
		return path;
		
	} // End of getCurrentUserUploadPath().

	/**
	 * 执行用户注册
	 */
	@Override
	public Boolean signup(String username, String password, String truename,
			String email) {
		
		Boolean result = Boolean.FALSE;
		
		UserInfoVO userinfovo = new UserInfoVO();
		UserDAO userdao = new UserDAO();
		
		userinfovo.setUser_name(username);
		userinfovo.setUser_pwd(password);
		userinfovo.setUser_true_name(truename);
		userinfovo.setUser_email(email);
		userinfovo.setUser_signup(Functions.getCurrentDatetime());
		
		// 设置状态为等待审批
		userinfovo.setUser_pri("pending");
		
		if (userdao.setAddUser(userinfovo)) { // 插入记录成功
			result = Boolean.TRUE;
			
		}
		
		return result;
	} // End of signup().

	/**
	 * 检查E-mail地址格式是否正确
	 */
	@Override
	public Boolean checkEmail(String email) {
		Boolean result = Boolean.FALSE;
		
		String regex = "[\\w|\\.]+@[\\w|\\.]+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		if (m.find()) {
			result = Boolean.TRUE;
		}

		
		return result;
		
	} // End of checkEmail().

	/**
	 * 检查用户名是否可用
	 */
	@Override
	public Boolean checkUsernameValid(String username) {
		Boolean result = Boolean.FALSE;
		
		UserDAO userdao = new UserDAO();
		if (!userdao.checkUserByName(username)) { // 不存在该用户名
			result = Boolean.TRUE;
		}
		
		return result;
		 
	} // End of checkUsernameValid().

	@Override
	public List<BaseModel> getUserlist() {
		List<BaseModel> result = new ArrayList<BaseModel>();
		List<UserInfoVO> userinfolist = new ArrayList<UserInfoVO>();
		
		UserDAO userdao = new UserDAO();
		userinfolist = userdao.getSearchAllUsers();
		
		for (UserInfoVO userinfovo : userinfolist) {
			BaseModel m = new BaseModel();
			
			m.set("user_id", userinfovo.getUser_id());
			m.set("user_name", userinfovo.getUser_name());
			m.set("user_pwd", userinfovo.getUser_pwd());
			m.set("user_true_name", userinfovo.getUser_true_name());
			m.set("user_email", userinfovo.getUser_email());
			if ("user".equals(userinfovo.getUser_pri())) {
				m.set("user_pri", "普通用户");
			} else if ("pending".equals(userinfovo.getUser_pri())) {
				m.set("user_pri", "待审核");
			}
			
			m.set("user_signup", userinfovo.getUser_signup().substring(0, userinfovo.getUser_signup().lastIndexOf(".")));
			
			
			result.add(m);
		}
		
		return result;
		
	} // End of getUserlist().

	@Override
	public Map<String, String> getAllConfigs() {
		Map<String, String> result = new HashMap<String, String>();
		List<ConfigsVO> configsList = new ArrayList<ConfigsVO>();
		
		ConfigsDAO configsdao = new ConfigsDAO();
		configsList = configsdao.getAllConfigs();
		
		for (ConfigsVO configsvo : configsList) {
			String content = configsvo.getContent();
			String item = configsvo.getItem();
			
			result.put(item, content);
		}
		
		return result;
	}

	@Override
	public Boolean isDirectoryValid(String directory) {
		Boolean result = Boolean.FALSE;
		
		if (new File(directory).isDirectory()) {
			
			// 路径存在
			result = Boolean.TRUE;
			
		} else if (new File(directory).mkdirs()) {
			
			// 路径不存在但可创建
			result = Boolean.TRUE;
		}
		
		return result;
	} // End of isDrectoryValid().

	@Override
	public Boolean changeConfigs(String uploadPath, String dbTempPath, String knowledgeFlowPath) {
		Boolean result = Boolean.FALSE;
		
		int modify_count = 0;
		int same_count = 0;
		
		Map<String, String> initMap = getAllConfigs();
		ConfigsDAO configsdao = new ConfigsDAO();
		
		// 更改上传文件存储目录
		if (!uploadPath.equals(initMap.get("UploadPath"))) { 
			
			// 移动文件至新目录
			ConfigsVO configsvo = configsdao.getConfig("UploadPath");
			String oldDirectory = configsvo.getContent();
			Functions.moveAllFiles(oldDirectory, uploadPath);
			
			// 更改数据库记录
			configsvo = new ConfigsVO();
			configsvo.setItem("UploadPath");
			configsvo.setContent(uploadPath);
			
			if (configsdao.setUpdateContent(configsvo))
				++modify_count;
		} else {
			++same_count;
		}
		
		// 更改数据库临时文件存储目录
		if (!dbTempPath.equals(initMap.get("DbTempPath"))) {
			
			// 移动文件至新目录
			ConfigsVO configsvo = configsdao.getConfig("DbTempPath");
			String oldDirectory = configsvo.getContent();
			Functions.moveAllFiles(oldDirectory, dbTempPath);
			
			// 更新数据库记录
			configsvo = new ConfigsVO();
			configsvo.setItem("DbTempPath");
			configsvo.setContent(dbTempPath);
			
			if (configsdao.setUpdateContent(configsvo))
				++modify_count;
		} else {
			++same_count;
		}
		
		// 更改挖掘模块配置文件存储目录
		if (!knowledgeFlowPath.equals(initMap.get("KnowledgeFlowPath"))) {
			
			// 移动文件至新目录
			ConfigsVO configsvo = configsdao.getConfig("KnowledgeFlowPath");
			String oldDirectory = configsvo.getContent();
			Functions.moveAllFiles(oldDirectory, knowledgeFlowPath);
			
			// 更新数据库记录
			configsvo = new ConfigsVO();
			configsvo.setItem("KnowledgeFlowPath");
			configsvo.setContent(knowledgeFlowPath);
			
			if (configsdao.setUpdateContent(configsvo))
				++modify_count;
		} else {
			++same_count;
		}
		
		if ((same_count + modify_count) == 3)
			result = Boolean.TRUE;
		
		return result;
		
	} // End of changeConfigs().

	/**
	 * 文件管理专用——获取用户可操作之文件列表
	 * @return 文件列表List$<$BaseModel$>$
	 */
	@Override
	public List<BaseModel> getEditableFilelist() {
		List<BaseModel> result = new ArrayList<BaseModel>();
		
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		int user_id = user.getUser_id();
		
		// 获取可编辑文件之列表
		FileDAO filedao = new FileDAO();
		List<FileInfoVO> fileinfovoList = filedao.getSearchEditableByUserId(user_id);
		
		for (FileInfoVO fileinfovo : fileinfovoList) {
			BaseModel m = new BaseModel();
			
			m.set("file_id", fileinfovo.getFile_id());
			m.set("file_name", fileinfovo.getFile_name());
			m.set("file_size", fileinfovo.getFile_size());
			m.set("file_type", fileinfovo.getFile_type());
			m.set("file_create_time", fileinfovo.getFile_create_time().substring(0, fileinfovo.getFile_create_time().lastIndexOf(".")));
			m.set("file_modify_time", fileinfovo.getFile_modify_time().substring(0, fileinfovo.getFile_modify_time().lastIndexOf(".")));
			
			User_file_infoDAO user_file_infodao = new User_file_infoDAO();
			List<String> shared_usernameList = user_file_infodao.getSearchSharedUserNameByFileId(fileinfovo.getFile_id());
			
			if (shared_usernameList.isEmpty()) { // 无分享者
				m.set("shared_users", "");
			} else {
				StringBuilder sBuilder = new StringBuilder();
				for (int i = 0; i < shared_usernameList.size(); ++i) {
					if (i != shared_usernameList.size() - 1)
						sBuilder.append(shared_usernameList.get(i)).append(", ");
					else
						sBuilder.append(shared_usernameList.get(i));
				}
				
				m.set("shared_users", sBuilder.toString());
			}
			
			result.add(m);
			
		} // End of for.
		
		return result;
		
	} // End of getEditableFilelist().

	@Override
	public Map<String, String> getDefaultConfigs() {
		Map<String, String> result = new HashMap<String, String>();
		
			
		// 读取文件内容
		try {
			InputStream is = getClass().getResourceAsStream("/conf/sys.config");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			List<String> listLines = new ArrayList<String>();
			String line;
			do {
				line = reader.readLine();
				listLines.add(line);
			} while (line != null);
				
			// 寻找指定参数值
			for (int i = 0; i < listLines.size(); ++i) {
				
				// 若已到最后一行，则退出循环
				if (listLines.get(i) == null)
					break;
				
				// 忽略空格行和注释行
				String regex = "^#|^\\s*$";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(listLines.get(i));
				if (m.find())
					continue;
				else { // 不是空格和注释行
						
					// 取得参数
					String[] options = listLines.get(i).split("\\s+");
						
					result.put(options[0], options[1]);
						
						
				}
			} // End of for.
				
		} catch (Exception e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
		return result;
			
	} // End of getDefaultConfigs().

	@Override
	public Boolean deleteFiles(List<BaseModel> selectedItemList) {
		Boolean result = Boolean.FALSE;
		
		// 获取用户的上传文件夹路径
		ConfigsDAO configsdao = new ConfigsDAO();
		ConfigsVO configsvo = configsdao.getConfig("UploadPath");
		String path = configsvo.getContent();
		
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		String user_id = String.valueOf(user.getUser_id());
		path = path + "/" + user_id + "/";
		
		int count = 0;
		
		for (BaseModel m : selectedItemList) {
			String fileName = m.get("file_name");
			String filePath = path + fileName;
			
			// 若文件存在，则删除之
			if (new File(filePath).isFile()) {
				if (new File(filePath).delete()) { // 成功删除后，进行相应数据库记录之删除
					FileDAO filedao = new FileDAO();
					int file_id = m.get("file_id");
					
					if (filedao.setDeleteFileById(file_id)) { // 删除成功
						++count;
					}
				}
					
			} // End of if.
			
				
		} // End of for.
		
		if (count == selectedItemList.size())
			result = Boolean.TRUE;
		
		return result;
		
	} // End of deleteFiles().

	@Override
	public List<BaseModel> getValidUsers() {
		List<BaseModel> result = new ArrayList<BaseModel>();
		
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		int user_id = user.getUser_id();
		
		UserDAO userdao = new UserDAO();
		List<UserInfoVO> userinfovoList = userdao.getUsersExceptSelf(user_id);
		
		for (UserInfoVO userinfovo : userinfovoList) {
			BaseModel m = new BaseModel();
			
			m.set("user_id", userinfovo.getUser_id());
			m.set("user_name", userinfovo.getUser_name());
			m.set("user_true_name", userinfovo.getUser_true_name());
			m.set("user_email", userinfovo.getUser_email());
			m.set("user_pri", userinfovo.getUser_pri());
			
			result.add(m);
		}
		
		return result;
	} // End of getValidUsers().

	/**
	 * 获取指定文件的分享用户
	 * @return 分享用户列表
	 */
	@Override
	public List<BaseModel> getSelectedUsers(int file_id) {
		List<BaseModel> result = new ArrayList<BaseModel>();
		
		User_file_infoDAO user_file_infodao = new User_file_infoDAO();
		List<Integer> userIdList = user_file_infodao.getSearchSharedUserIdByFileId(file_id);
		
		UserDAO userdao = new UserDAO();
		for (Integer userId : userIdList) {
			BaseModel m = new BaseModel();
			
			UserInfoVO userinfovo = userdao.getUserById(userId.intValue());
			
			m.set("user_id", userId.intValue());
			m.set("user_name", userinfovo.getUser_name());
			m.set("user_true_name", userinfovo.getUser_true_name());
			m.set("user_email", userinfovo.getUser_email());
			m.set("user_pri", userinfovo.getUser_pri());
			
			result.add(m);
		}
		
		return result;
	}

	/**
	 * 审批用户通过
	 */
	@Override
	public Boolean approvalUsers(List<BaseModel> selectedUserList) {
		Boolean flag = false;
		int count = 0;
		
		for (int i = 0; i < selectedUserList.size(); ++i) {
			int user_id = selectedUserList.get(i).get("user_id");
			
			UserDAO userdao = new UserDAO();
			if (userdao.approvalUserById(user_id))
				++count;
		}
		
		if (count == selectedUserList.size())
			flag = true;
		
		return flag;
	} // End of approvalUsers().

	@Override
	public Boolean removeUsers(List<BaseModel> selectedUserList) {
		Boolean flag = Boolean.FALSE;
		int count = 0;
		
		for (int i = 0; i < selectedUserList.size(); ++i) {
			int user_id = selectedUserList.get(i).get("user_id");
			
			UserDAO userdao = new UserDAO();
			if (userdao.setDeleteUserById(user_id))
				++count;
		}
		
		if (count == selectedUserList.size())
			flag = Boolean.TRUE;
		
		return flag;
	} // End of removeUsers().

	@Override
	public Boolean setSharedUsers(List<BaseModel> selectedUserList, int file_id) {
		Boolean flag = false;
		int count = 0;
		User_file_infoDAO user_file_infodao = new User_file_infoDAO();
		
		for (int i = 0; i < selectedUserList.size(); ++i) { // 插入操作
			
			if (!user_file_infodao.getSearchItemByIds((Integer)selectedUserList.get(i).get("user_id"), file_id)) { // 数据库中不存在该项
				
				User_file_infoVO user_file_infovo = new User_file_infoVO();
				
				user_file_infovo.setUser_id((Integer)selectedUserList.get(i).get("user_id"));
				user_file_infovo.setFile_id(file_id);
				user_file_infovo.setIs_owner(0);
				
				if (user_file_infodao.setAddUser_file_info(user_file_infovo))
					++count;
			} else {
				++count;
			}
		} // End of for.
		
		// 删除操作
		List<User_file_infoVO> user_file_infovoList = user_file_infodao.getSearchItemsByFileId(file_id);
		
		if (user_file_infovoList.size() > selectedUserList.size()) { // 删除掉需要取消分享的用户
			boolean judge = false;
			for (int i = 0; i < user_file_infovoList.size(); ++i) {
				for (int j = 0; j < selectedUserList.size(); ++j) {
					// 查找条目是否存在于selectedUserList中
					if (selectedUserList.get(j).get("user_id").equals(user_file_infovoList.get(i).getUser_id())) {
						judge = true;
					}
				} // End of for.
				
				if (!judge) { // selectedUserList中无该条目，删除之
					user_file_infodao.setDeleteItemByIds(user_file_infovoList.get(i).getUser_id(), file_id);
					
				}
			} // End of for.
		}
		
		if (count == selectedUserList.size())
			flag = true;
		
		return flag;
		
	} // End of setSharedUsers().

	/**
	 * 获取合法上传文件类型列表
	 */
	@Override
	public List<String> getValidTypeList() {
		List<String> result = new ArrayList<String>();
		
		InputStream is = null;
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			// 读取配置文档内容
			builder = dbfactory.newDocumentBuilder();
			is = getClass().getResourceAsStream("/conf/config.xml");
			Document document = builder.parse(is);
			document.getDocumentElement().normalize();
			Element rootElement = document.getDocumentElement();
			
			NodeList nodeList = rootElement.getElementsByTagName("valid-file-type");
			Element typeElem = (Element)nodeList.item(0);
			
			NodeList list = typeElem.getElementsByTagName("item");
			for (int i = 0; i < list.getLength(); ++i) {
				Node curNode = list.item(i);
				
				String typeName = curNode.getTextContent().trim();
				result.add(typeName);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	} // End of getValidTypeList().
}
