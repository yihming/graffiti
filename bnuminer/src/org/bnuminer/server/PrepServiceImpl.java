package org.bnuminer.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bnuminer.UserSession;
import org.bnuminer.client.service.PrepService;
import org.bnuminer.dao.ConfigsDAO;
import org.bnuminer.dao.FileDAO;
import org.bnuminer.dao.User_file_infoDAO;
import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;

import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.InstanceQuery;
import weka.gui.sql.DbUtils;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class PrepServiceImpl extends RemoteServiceServlet implements PrepService {

	/**
	 * ConnectionPanel之数据库连接配置信息的初始化
	 */
	@Override
	public Map<String, String> db_init_configs() {
		Map<String, String> result = new HashMap<String, String>();
		try {
			DbUtils m_dbUtils = new DbUtils();
			result.put("database_url", m_dbUtils.getDatabaseURL());
			result.put("user_name", m_dbUtils.getUsername());
			result.put("password", m_dbUtils.getPassword());
		} catch (Exception e) {
			
			e.printStackTrace();
			result.put("database_url", "");
			result.put("user_name", "");
			result.put("password", "");
		}
		
		return result;
	
	} // End of db_init_configs().
	
	public Boolean db_connect_test(String url, String name, String password) {
		Boolean result = Boolean.FALSE;
		try {
			DbUtils m_DbUtils = new DbUtils();
			m_DbUtils.setDatabaseURL(url);
			m_DbUtils.setUsername(name);
			m_DbUtils.setPassword(password);
			m_DbUtils.connectToDatabase();
			result = Boolean.TRUE;
			
			if (m_DbUtils.isConnected())
				m_DbUtils.disconnectFromDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	} // End of db_connect().
	
	/**
	 * 获取历史记录
	 */
	public List<BaseModel> get_History(List<String> history, String option) {
		List<BaseModel> result = new ArrayList<BaseModel>();
		
		if (option.equals("connect")) { // 获取连接历史记录
			
			for (String item : history) {
				BaseModel m = new BaseModel();
				if (item.indexOf("@") > -1) {
					m.set("content", item);
					m.set("user", item.subSequence(0, item.indexOf("@")));
					m.set("url", item.substring(item.indexOf("@") + 1));
				} else {
					m.set("content", item);
					m.set("user", "");
					m.set("url", item);
				}
				
				result.add(m);
			} // End of for.
			
		} else if (option.equals("query")) { // 获取查询历史记录
			
			for (String item : history) {
				BaseModel m = new BaseModel();
				m.set("content", item);
				
				result.add(m);
			} // End of for.
			
		}
		
		
		
		return result;
		
	} // End of get_Connect_History().
	
	
	/**
	 * 执行SQL语句查询数据库，并保存至用户临时文件
	 * @return 是否执行成功
	 * @param url 数据库JDBC地址
	 * @param name 用户名
	 * @param password 密码
	 * @param sql 查询语句
	 */
	public Map<String, Object> db_query(String url, String name, String password, String sql) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<BaseModel> metaDataList = new ArrayList<BaseModel>();
		List<BaseModel> dataList = new ArrayList<BaseModel>();
		
		
		try {
			// 连接数据库，执行查询
			InstanceQuery query = new InstanceQuery();
			query.setDatabaseURL(url);
			query.setUsername(name);
			query.setPassword(password);
			query.setQuery(sql);
			query.connectToDatabase();
			
			// 获取查询结果
			if (query.execute(sql)) {
				ResultSet rs = query.getResultSet();
				ResultSetMetaData resultData = rs.getMetaData();
				
				// 获取字段信息
				for (int i = 1; i <= resultData.getColumnCount(); ++i) {
					BaseModel m = new BaseModel();
					
					m.set("column_name", resultData.getColumnName(i));
					metaDataList.add(m);
				}
				
				// 获取数据
				int count = 1;
				while (rs.next()) {
					BaseModel m = new BaseModel();
					
					m.set("dataNo", count);
					++count;
					for (int i = 1; i < resultData.getColumnCount(); ++i) {
						switch (resultData.getColumnType(i)) {
							case Types.DECIMAL:
								m.set(resultData.getColumnName(i), rs.getDouble(resultData.getColumnName(i)));
								break;
							case Types.VARCHAR:
								m.set(resultData.getColumnName(i), rs.getString(resultData.getColumnName(i)));
								break;
							default:
									break;
						}// End of switch.
					} // End of for.
					
					dataList.add(m);
				} // End of while.
				
				query.close(rs);
			} // End of if. 
			
			result.put("metaDataList", metaDataList);
			result.put("dataList", dataList);
			
			// 将查询结果生成保存为arff格式文件
			Instances data = query.retrieveInstances();
			
			
			// 读取保存文件路径
			UserSession usersession = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			String user_id = Integer.valueOf(usersession.getUser_id()).toString();
			
			ConfigsVO configsvo = new ConfigsVO();
			ConfigsDAO configsdao = new ConfigsDAO();
			configsvo = configsdao.getConfig("DbTempPath");
			String db_path = configsvo.getContent();
			
			// 若该文件夹不存在，则创建之
			if (! new File(db_path).isDirectory())
				new File(db_path).mkdirs();
			
			db_path = db_path + "/" + user_id + "_DbTemp.arff";
			
			// 保存为arff格式文件
			File save_file = new File(db_path);
			BufferedWriter writer = new BufferedWriter(new FileWriter(save_file));
			writer.write(data.toString());
			writer.flush();
			writer.close();
			
			// 关闭数据库连接
			
			query.close();
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	} // End of db_query().
	
	/**
	 * 将所打开文件的统计信息传回ExplorerWindow并显示出来
	 * @return 以String作为key之Map
	 * @param open_mode 打开模式
	 * @param file_id 文件ID值（仅对打开上传文件模式有效）
	 */
	public Map<String, Object> prep_statistics(int open_mode, int file_id) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		Instances data = null;
		
		if (open_mode == 1) { // 打开上传文件模式
			
			// 获取文件上传路径
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("UploadPath");
			
			// 获取源文件内容
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(file_id);
			
			String path = "";
			User_file_infoDAO user_file_infodao = new User_file_infoDAO();
			
			// 设置文件路径
			if (user_file_infodao.checkOwner(user_id, file_id)) { // 当前用户是该文件之所有者
				
				path = configsvo.getContent() + "/" + String.valueOf(user_id) + "/" + fileinfovo.getFile_name();
				
				
				
			} else { // 当前用户非该文件之所有者
				
				int owner_id = user_file_infodao.getOwnerIdByFileId(file_id);
				path = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/" + fileinfovo.getFile_name();
			
			}

			try {
				// 读取源文件
				data = DataSource.read(path);
				
				// 设置统计内容
				result.put("preprocess_relation", data.relationName());
				result.put("preprocess_attributes", Integer.valueOf(data.numAttributes()));
				result.put("preprocess_instances", Integer.valueOf(data.numInstances()));
				result.put("preprocess_weight", Double.valueOf(data.sumOfWeights()));
				
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else if (open_mode == 2) { // 打开URL模式
			
		} else if (open_mode == 3) { // 打开数据库模式
			
			// 获取数据库内容保存路径
			UserSession usersession = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			String user_id = Integer.valueOf(usersession.getUser_id()).toString();
			
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("DbTempPath");
			String save_path = configsvo.getContent();
			
			save_path = save_path + "/" + user_id + "_DbTemp.arff";
			
			
			try {
				// 读取数据库内容
				data = DataSource.read(save_path);
				
				// 设置统计内容
				// 当前Relation之信息
				result.put("preprocess_relation", data.relationName());
				result.put("preprocess_attributes", Integer.valueOf(data.numAttributes()));
				result.put("preprocess_instances", Integer.valueOf(data.numInstances()));
				result.put("preprocess_weight", Double.valueOf(data.sumOfWeights()));
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 属性信息
		List<BaseModel> attributeList = new ArrayList<BaseModel>();
		for (int i = 0; i < data.numAttributes(); ++i) {
			Attribute attr = data.attribute(i);
			BaseModel m = new BaseModel();
			m.set("attr_No", attr.index() + 1);
			m.set("attr_name", attr.name());
	
			attributeList.add(m);
		}
		
		result.put("attr_list", attributeList);
		
		return result;
		
	} // End of prep_statistics().

	/**
	 * 获取指定属性的统计信息
	 * @param attributeIndex 属性序号
	 * @param open_mode Explorer打开模式
	 * @param file_id 上传文件ID号（仅对打开上传文件模式有效）
	 * @return 属性统计信息的Hash表
	 */
	@Override
	public Map<String, Object> displaySelectedAttribute(int attributeIndex, int open_mode, int file_id) {
		Map<String, Object> result = new HashMap<String, Object>();
		Instances data = null;
		
		if (open_mode == 1) { // 打开上传文件模式
			
			// 获取文件上传路径
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("UploadPath");
			
			// 获取源文件内容
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(file_id);
			
			String path = "";
			
			User_file_infoDAO user_file_infodao = new User_file_infoDAO();
			
			// 验证当前用户是否为该文件之所有者
			if (user_file_infodao.checkOwner(user_id, file_id)) {
				
				path = configsvo.getContent() + "/" + String.valueOf(user_id) + "/" + fileinfovo.getFile_name();
			
			} else {
				
				int owner_id = user_file_infodao.getOwnerIdByFileId(file_id);
				
				path = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/" + fileinfovo.getFile_name();
			}
			
			// 读取文件内容
			try {
				data = DataSource.read(path);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (open_mode == 2) { // 打开URL模式
			
		} else if (open_mode == 3) { // 打开数据库模式
			
			// 获取数据库内容保存路径
			UserSession usersession = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			String user_id = Integer.valueOf(usersession.getUser_id()).toString();
			
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("DbTempPath");
			String save_path = configsvo.getContent();
			
			save_path = save_path + "/" + user_id + "_DbTemp.arff";
			
			try {
				data = DataSource.read(save_path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} // End of if.
		
		
		// 获取属性
		Attribute attr = data.attribute(attributeIndex);
		AttributeStats as = data.attributeStats(attributeIndex);
		
		// 属性总体统计信息
		result.put("preprocess_attribute_name", attr.name());
		result.put("preprocess_attribute_type", Attribute.typeToString(attr.type()));
		result.put("preprocess_attribute_missing", String.valueOf(as.missingCount) + " (" + String.valueOf((double)as.missingCount / (double)as.totalCount) + "%)");
		result.put("preprocess_attribute_distinct", String.valueOf(as.distinctCount));
		result.put("preprocess_attribute_unique", String.valueOf(as.uniqueCount) + " (" + String.valueOf(Double.valueOf(as.uniqueCount / as.totalCount)) + "%)");
		
		// 根据属性类型，获取详细统计信息
		if (attr.isNumeric()) {
			
			// 该属性是NUMERIC类型
			List<BaseModel> attributeStatList = new ArrayList<BaseModel>();
			
			BaseModel bm = new BaseModel();
			bm.set("stat_name", "最小值");
			bm.set("stat_value", as.numericStats.min);
			attributeStatList.add(bm);
			
			bm = new BaseModel();
			bm.set("stat_name", "最大值");
			bm.set("stat_value", as.numericStats.max);
			attributeStatList.add(bm);
			
			bm = new BaseModel();
			bm.set("stat_name", "平均值");
			bm.set("stat_value", as.numericStats.mean);
			attributeStatList.add(bm);
			
			bm = new BaseModel();
			bm.set("stat_name", "标准差");
			bm.set("stat_value", as.numericStats.stdDev);
			attributeStatList.add(bm);

			result.put("preprocess_attribute_statistics", attributeStatList);
			
			
		} else if (attr.isNominal()) {
			
			// 该属性是NOMINAL类型
			List<BaseModel> attributeStatList = new ArrayList<BaseModel>();
			
			
			for (int i = 0; i < attr.numValues(); ++i) {
				BaseModel m = new BaseModel();
				
				int valueNo = i + 1;
				String valueLabel = attr.value(i);
				int valueCount = as.nominalCounts[i];
				double valueWeight = as.nominalWeights[i];
				
				m.set("valueNo", valueNo);
				m.set("valueLabel", valueLabel);
				m.set("valueCount", valueCount);
				m.set("valueWeight", valueWeight);
				
				attributeStatList.add(m);
				
			} // End of for.
			
			result.put("preprocess_attribute_statistics", attributeStatList);
			
		}
		
		

		
		return result;
		
	} // End of displaySelectedAttribute().

	@Override
	public List<BaseModel> getAttributeList(int open_mode, int file_id) {
		List<BaseModel> result = new ArrayList<BaseModel>();
		Instances data = null;
		
		
		if (open_mode == 1) { // 打开上传文件模式
			
			// 获取上传文件路径
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("UploadPath");
			String path = null;
			
			UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			int user_id = user.getUser_id();
			
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(file_id);
			
			User_file_infoDAO user_file_infodao = new User_file_infoDAO();
			
			if (user_file_infodao.checkOwner(user_id, file_id)) {
				
				path = configsvo.getContent() + "/" + String.valueOf(user_id) + "/" + fileinfovo.getFile_name();
				
			} else {
				
				int owner_id = user_file_infodao.getOwnerIdByFileId(file_id);
				path = configsvo.getContent() + "/" + String.valueOf(owner_id) + "/" + fileinfovo.getFile_name();
				
			}
			

			
			// 读取源文件内容
			try {
				data = DataSource.read(path);
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else if (open_mode == 2) { // 打开URL模式
			
		} else if (open_mode == 3) { // 打开数据库模式
			
			// 获取数据库内容保存路径
			UserSession usersession = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
			String user_id = Integer.valueOf(usersession.getUser_id()).toString();
			
			ConfigsDAO configsdao = new ConfigsDAO();
			ConfigsVO configsvo = configsdao.getConfig("DbTempPath");
			String save_path = configsvo.getContent();
			
			save_path = save_path + "/" + user_id + "_DbTemp.arff";
			
			try {
				data = DataSource.read(save_path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		// 获取属性列表
		for (int i = 0; i < data.numAttributes(); ++i) {
			BaseModel m = new BaseModel();
			
			Attribute attr = data.attribute(i);
			m.set("attrNo", i + 1);
			m.set("attrName", attr.name());
			m.set("attrType", Attribute.typeToString(attr));
			
			result.add(m);
		}
		
		
		return result;
		
	} // End of getAttributeList().
	
}
