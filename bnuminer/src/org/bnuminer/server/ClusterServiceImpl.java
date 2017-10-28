package org.bnuminer.server;
/**
 * r39: 完善各聚类算法调用过程 (2010-04-27 15:56)
 */
import java.util.ArrayList;
import java.util.List;

import org.bnuminer.UserSession;
import org.bnuminer.client.service.ClusterService;
import org.bnuminer.dao.ConfigsDAO;
import org.bnuminer.dao.FileDAO;
import org.bnuminer.dao.User_file_infoDAO;
import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;

import weka.clusterers.*;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ClusterServiceImpl extends RemoteServiceServlet implements ClusterService {

	/**
	 * 进行聚类
	 */
	@Override
	public String doing_cluster(String clustererName, String clustererOptions, int explorer_open_mode,
			int fileId, String[] ignoreAttributes) {
		
		String result = null;
		String path = "";
		
		
		
		if (explorer_open_mode == 1) { // 上传文件模式
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(fileId);
			
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("UploadPath");
			
			// 验证当前用户是否为该文件之所有者
			User_file_infoDAO user_file_infodao = new User_file_infoDAO();
			if (user_file_infodao.checkOwner(user_id, fileId)) {
				path = configsvo.getContent() + "/" + String.valueOf(user_id) + "/" + fileinfovo.getFile_name();
			} else {
				int owner_id = user_file_infodao.getOwnerIdByFileId(fileId);
				path = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/" + fileinfovo.getFile_name();
			}
			
		} else if (explorer_open_mode == 2) { // URL模式
			
		} else if (explorer_open_mode == 3) { // 数据库模式
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("DbTempPath");
			
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			
			path = configsvo.getContent() + "/" + String.valueOf(user_id) + "_DbTemp.arff";
		}
		
		
		// 进行聚类挖掘
		try {
			Instances data = DataSource.read(path);
			
			if (ignoreAttributes != null) {
				for (int i = 0; i < ignoreAttributes.length; ++i) {
					Attribute attr = data.attribute(ignoreAttributes[i]);
					data.deleteAttributeAt(attr.index());
					System.out.println(data);
				}
			}
			
			
			Clusterer clusterer = null;
			String[] options = weka.core.Utils.splitOptions(clustererOptions);
			
			
			
			if (clustererName.equals("EM")) { // 选用EM算法
				clusterer = AbstractClusterer.forName("weka.clusterers.EM", options);
			
				clusterer.buildClusterer(data);
				
				
				
			} else if (clustererName.equals("CLOPE")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.CLOPE", options);
				
				clusterer.buildClusterer(data);
				
				
				
				
				
			} else if (clustererName.equals("Cobweb")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.Cobweb", options);
			
				clusterer.buildClusterer(data);
				
				
				
			} else if (clustererName.equals("DBScan")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.DBScan", options);
			
				clusterer.buildClusterer(data);
				
				
				
			} else if (clustererName.equals("FarthestFirst")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.FarthestFirst", options);
				
				clusterer.buildClusterer(data);
				
				
				
			} else if (clustererName.equals("FilteredClusterer")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.FilteredClusterer", options);
				
				clusterer.buildClusterer(data);
				
				
				
			} else if (clustererName.equals("MakeDensityBasedClusterer")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.MakeDensityBasedClusterer", options);
				
				clusterer.buildClusterer(data);
				
				
				
			} else if (clustererName.equals("OPTICS")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.OPTICS", options);
				
				clusterer.buildClusterer(data);
				
				
				
			} else if (clustererName.equals("SimpleKMeans")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.SimpleKMeans", options);
				
				clusterer.buildClusterer(data);
				
				
				
			} // End of if.
			
			// 评估聚类结果，按照聚类模式的默认选项，选取训练样本集作为评估样本集
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(clusterer);
			eval.evaluateClusterer(data);
			result = eval.clusterResultsToString();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = "发生错误";
		}
		
		return result;
		
		
	}

	
	@Override
	public List<BaseModel> getAttributes(int openMode, int fileId) {
		List<BaseModel> result = new ArrayList<BaseModel>();
		
		
		String path = "";
		
		if (openMode == 1) { // 打开上传文件模式
			
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("UploadPath");
			
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			
			// 判断当前用户是否为该文件之所有者
			User_file_infoDAO user_file_infodao = new User_file_infoDAO();
			if (user_file_infodao.checkOwner(user_id, fileId)) {
				path = configsvo.getContent() + "/" + String.valueOf(user_id) + "/";
			} else {
				int owner_id = user_file_infodao.getOwnerIdByFileId(fileId);
				path = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/";
			}
			
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(fileId);
			path += fileinfovo.getFile_name();
			
			
		} else if (openMode == 3) { // 打开数据库模式
			
		} else if (openMode == 2) { // 打开URL模式
			
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("DbTempPath");
			
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			
			path = configsvo.getContent() + "/" + String.valueOf(user_id) + "_DbTemp.arff";
		} // End of if.
		
		// 获取属性列表
		try {
			Instances data = DataSource.read(path);
			for (int i = 0; i < data.numAttributes(); ++i) {
				BaseModel m = new BaseModel();
				Attribute attr = data.attribute(i);
				
				m.set("attr_id", attr.index());
				m.set("attr_name", attr.name());
				m.set("attr_type", Attribute.typeToString(attr.type()));
				
				result.add(m);
				
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return result;
		
	} // End of getAttributes().


	@Override
	public String doing_cluster_with_testdataset(String clustererName,
			String clustererOptions, int explorerOpenMode, int trainingFileId,
			int testingFileId, String[] ignoreAttributes) {
		String result = null;
		String trainingFilePath = "";
		
		// 获取训练集文件路径
		if (explorerOpenMode == 1) { // 上传文件模式
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(trainingFileId);
			
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("UploadPath");
			
			// 验证当前用户是否为该文件之所有者
			User_file_infoDAO user_file_infodao = new User_file_infoDAO();
			if (user_file_infodao.checkOwner(user_id, trainingFileId)) {
				trainingFilePath = configsvo.getContent() + "/" + String.valueOf(user_id) + "/" + fileinfovo.getFile_name();
			} else {
				int owner_id = user_file_infodao.getOwnerIdByFileId(trainingFileId);
				trainingFilePath = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/" + fileinfovo.getFile_name();
			}
			
		} else if (explorerOpenMode == 2) { // URL模式
			
		} else if (explorerOpenMode == 3) { // 数据库模式
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("DbTempPath");
			
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			
			trainingFilePath = configsvo.getContent() + "/" + String.valueOf(user_id) + "_DbTemp.arff";
		}
		
		// 获取测试集文件路径
		String testingFilePath = "";
		
		FileDAO filedao = new FileDAO();
		FileInfoVO fileinfovo = filedao.getSearchFileById(testingFileId);
		
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		int user_id = user.getUser_id();
		
		ConfigsDAO configsdao = new ConfigsDAO();
		ConfigsVO configsvo = configsdao.getConfig("UploadPath");
		
		// 验证当前用户是否为该文件之所有者
		User_file_infoDAO user_file_infodao = new User_file_infoDAO();
		if (user_file_infodao.checkOwner(user_id, testingFileId)) {
			testingFilePath = configsvo.getContent() + "/" + String.valueOf(user_id) + "/" + fileinfovo.getFile_name();
		} else {
			int owner_id = user_file_infodao.getOwnerIdByFileId(testingFileId);
			testingFilePath = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/" + fileinfovo.getFile_name();
		}
		
		
		// 进行聚类挖掘
		try {
			Instances trainingData = DataSource.read(trainingFilePath);
			Instances testingData = DataSource.read(testingFilePath);
			
			if (ignoreAttributes != null) {
				for (int i = 0; i < ignoreAttributes.length; ++i) {
					Attribute attr = trainingData.attribute(ignoreAttributes[i]);
					trainingData.deleteAttributeAt(attr.index());
				}
			}
			
			
			
			Clusterer clusterer = null;
			String[] options = weka.core.Utils.splitOptions(clustererOptions);
			
			if (clustererName.equals("EM")) { // 选用EM算法
				clusterer = AbstractClusterer.forName("weka.clusterers.EM", options);
			
				clusterer.buildClusterer(trainingData);
				
				
				
			} else if (clustererName.equals("CLOPE")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.CLOPE", options);
				
				clusterer.buildClusterer(trainingData);
				
				
				
				
			} else if (clustererName.equals("Cobweb")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.Cobweb", options);
			
				clusterer.buildClusterer(trainingData);
				
				
				
			} else if (clustererName.equals("DBScan")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.DBScan", options);
			
				clusterer.buildClusterer(trainingData);
				
				
				
			} else if (clustererName.equals("FarthestFirst")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.FarthestFirst", options);
				
				clusterer.buildClusterer(trainingData);
				
				
				
			} else if (clustererName.equals("FilteredClusterer")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.FilteredClusterer", options);
				
				clusterer.buildClusterer(trainingData);
				
				
				
			} else if (clustererName.equals("MakeDensityBasedClusterer")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.MakeDensityBasedClusterer", options);
				
				clusterer.buildClusterer(trainingData);
				
				
				
			} else if (clustererName.equals("OPTICS")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.OPTICS", options);
				
				clusterer.buildClusterer(trainingData);
				
				
				
			} else if (clustererName.equals("SimpleKMeans")) {
				
				clusterer = AbstractClusterer.forName("weka.clusterers.SimpleKMeans", options);
				
				clusterer.buildClusterer(trainingData);
				
				
				
			} // End of if.
			
			// 评估聚类结果，选取测试集作为评估样本集
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(clusterer);
			eval.evaluateClusterer(testingData);
			result = eval.clusterResultsToString();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = "发生错误";
		}
		
		return result;
		
		
	} // End of doing_cluster_with_testingdataset().

}
