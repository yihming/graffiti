package org.bnuminer.client.service;

import java.util.List;
import java.util.Map;

import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * GWT-RPC之异步接口
 * @author John Yung
 *
 */
public interface BnuMinerServiceAsync {
	
	void login(String userName, String password, String authCode,
			AsyncCallback<Integer> callback);

	void logout(AsyncCallback<?> callback);

	void getWelcomeinfo(AsyncCallback<String> callback);



	void getUserInfo(AsyncCallback<Boolean> callback);

	void getFilelist(PagingLoadConfig config,
			AsyncCallback<PagingLoadResult<BaseModel>> callback);

	void openFile(int file_id, AsyncCallback<String> callback);

	void getSimpleFilelist(AsyncCallback<List<BaseModel>> callback);

	void prep_statistics(int file_id, AsyncCallback<Map<String, Object>> callback);

	void getCurrentUserUploadPath(AsyncCallback<String> callback);

	void isAdmin(AsyncCallback<Boolean> callback);

	void signup(String username, String password, String truename,
			String email, AsyncCallback<Boolean> callback);

	void checkEmail(String email, AsyncCallback<Boolean> callback);

	void checkUsernameValid(String username, AsyncCallback<Boolean> callback);

	void getUserlist(AsyncCallback<List<BaseModel>> callback);

	void getAllConfigs(AsyncCallback<Map<String, String>> callback);

	void isDirectoryValid(String directory, AsyncCallback<Boolean> callback);

	void changeConfigs(String uploadPath, String dbTempPath, String knowledgeFlowPath,
			AsyncCallback<Boolean> callback);

	void getEditableFilelist(AsyncCallback<List<BaseModel>> callback);

	void getDefaultConfigs(AsyncCallback<Map<String, String>> callback);

	void deleteFiles(List<BaseModel> selectedItemList,
			AsyncCallback<Boolean> callback);

	void getValidUsers(AsyncCallback<List<BaseModel>> callback);

	void getSelectedUsers(int file_id, AsyncCallback<List<BaseModel>> callback);

	void approvalUsers(List<BaseModel> selectedUserList,
			AsyncCallback<Boolean> callback);

	void removeUsers(List<BaseModel> selectedUserList,
			AsyncCallback<Boolean> callback);

	void setSharedUsers(List<BaseModel> selectedUserList, int fileId,
			AsyncCallback<Boolean> callback);

	void getValidTypeList(AsyncCallback<List<String>> callback);

}
