package org.bnuminer.client.service;

import java.util.List;
import java.util.Map;

import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * GWT-RPC之接口
 * @author John Yung
 *
 */
@RemoteServiceRelativePath("bnuminer")
public interface BnuMinerService extends RemoteService {
	/**
	 * 处理登录事件
	 * @param userName - 用户名
	 * @param password - 密码
	 * @return - 成功则为true, 失败则为false
	 */
	Integer login(String userName, String password, String authCode);
	
	/**
	 * 处理注销事件
	 */
	void logout();
	
	/**
	 * 登录成功后，添加欢迎信息内容
	 * @return String - HeaderPanel.welcome_information的内容
	 */
	String getWelcomeinfo();
	
	/**
	 * 获取当前会话中的用户信息
	 * @return Boolean - True: 当前用户信息存在
	 *                   False: 当前用户信息不存在
	 */
	Boolean getUserInfo();
	
	/**
	 * 检查当前用户是否为超级管理员
	 * @return 是则返回Boolean.TRUE，否则返回Boolean.FALSE
	 */
	Boolean isAdmin();
	
	PagingLoadResult<BaseModel> getFilelist(PagingLoadConfig config);
	
	String openFile(int file_id);
	
	List<BaseModel> getSimpleFilelist();
	
	Map<String, Object> prep_statistics(int file_id);
	
	String getCurrentUserUploadPath();
	
	/**
	 * 用户注册
	 */
	Boolean signup(String username, String password, String truename, String email);
	
	Boolean checkEmail(String email);
	
	Boolean checkUsernameValid(String username);
	
	List<BaseModel> getUserlist();
	
	Map<String, String> getAllConfigs();
	
	Boolean isDirectoryValid(String directory);
	
	Boolean changeConfigs(String uploadPath, String dbTempPath, String knowledgeFlowPath);
	
	/**
	 * 选取用户可编辑文件之列表
	 * @return
	 */
	List<BaseModel> getEditableFilelist();
	
	/**
	 * 从配置文件中读取系统路径之默认值
	 * @return
	 */
	Map<String, String> getDefaultConfigs();
	
	/**
	 * 删除在线文档
	 */
	Boolean deleteFiles(List<BaseModel> selectedItemList);
	
	List<BaseModel> getValidUsers();
	
	List<BaseModel> getSelectedUsers(int file_id);
	
	Boolean setSharedUsers(List<BaseModel> selectedUserList, int file_id);
	
	Boolean approvalUsers(List<BaseModel> selectedUserList);
	
	Boolean removeUsers(List<BaseModel> selectedUserList);
	
	List<String> getValidTypeList();
}
