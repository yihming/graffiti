package org.bnuminer.server;
/**
 * r39: 实现挖掘完成后的可视化 (2010-04-27 15:58)
 * 		修复聚类数目未检测是否为负数之Bug (2010-04-28 10:36)
 * 		修正“XML文件路径不存在”之Bug (2010-05-02 19:59)
 */
import java.awt.BorderLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bnuminer.UserSession;
import org.bnuminer.client.service.StudentFlowService;
import org.bnuminer.dao.ConfigsDAO;
import org.bnuminer.dao.FileDAO;
import org.bnuminer.dao.User_file_infoDAO;
import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import weka.clusterers.AbstractClusterer;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.explorer.ClustererAssignmentsPlotInstances;
import weka.gui.visualize.VisualizePanel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class StudentFlowServiceImpl extends RemoteServiceServlet implements StudentFlowService {


	/**
	 * 执行挖掘任务
	 * @return 挖掘结果之文本形式
	 * 
	 */
	@Override
	public String doWekaFlow(int datasetFileId,
			String foldsNum, String seedNum, String displayModelInOldFormat,
			String maxIterations, String minStdDev, String numClusters,
			String seed) {
		
		String result = null;
		String path = "";
		
		ConfigsDAO configsdao = new ConfigsDAO();
		ConfigsVO configsvo = configsdao.getConfig("UploadPath");
		
		UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
		String user_id = String.valueOf(user.getUser_id());
		
		
		// 验证当前用户是否为该文件之所有者
		User_file_infoDAO user_file_infodao = new User_file_infoDAO();
		if (user_file_infodao.checkOwner(Integer.valueOf(user_id), datasetFileId)) {
			path = configsvo.getContent() + "/" + user_id + "/";
		} else {
			int owner_id = user_file_infodao.getOwnerIdByFileId(datasetFileId);
			path = configsvo.getContent() + "/" + owner_id + "/";
		}
		
		
		
		try {
			// 数据集
			FileDAO filedao = new FileDAO();
			FileInfoVO fileinfovo = filedao.getSearchFileById(datasetFileId);
			Instances data = DataSource.read(path + fileinfovo.getFile_name());
			System.out.println(path + fileinfovo.getFile_name());
			
			// 参数选项
			String assemble_options = "";
			
			
			// 聚类器参数校验与设定
			
			// 输出格式设置
			if ("是".equals(displayModelInOldFormat))
				assemble_options += "-O ";
			
			
			// 最大迭代次数
			String regex = "\\d+";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(maxIterations);
			if (m.find()) {
				assemble_options = assemble_options + "-I " + m.group() + " ";
			} else {
				assemble_options = assemble_options + "-I 100 ";
			}
			
			// 最小标准差
			try {
				Double param = Double.valueOf(minStdDev);
				assemble_options = assemble_options + "-M " + String.valueOf(param.doubleValue()) + " ";
				
			} catch (Exception e) {
				assemble_options = assemble_options + "-M 1.0E-6 ";
			}
			
			// 聚类数目
			regex = "-?\\d+";
			p = Pattern.compile(regex);
			m = p.matcher(numClusters);
			if (m.find()) {
				assemble_options = assemble_options + "-N " + m.group() + " ";
			} else {
				assemble_options = assemble_options + "-N -1 ";
			}
			
			// 随机数生成种子
			regex = "\\d+";
			p = Pattern.compile(regex);
			m = p.matcher(seed);
			if (m.find()) {
				assemble_options = assemble_options + "-S " + m.group() + " ";
			} else {
				assemble_options = assemble_options + "-S 100 ";
			}
			
			System.out.println(assemble_options);
			
			String[] options = weka.core.Utils.splitOptions(assemble_options);
			
			// 构建聚类器
			Clusterer clusterer = AbstractClusterer.forName("weka.clusterers.EM", options);
			clusterer.buildClusterer(data);
			
			// Cross Validation参数设置
			int validation_folds;
			int validation_seed;
			
			m = p.matcher(foldsNum);
			if (m.find()) {
				validation_folds = Integer.valueOf(m.group()).intValue();
			} else {
				validation_folds = 10;
			}
			
			m = p.matcher(seedNum);
			if (m.find()) {
				validation_seed = Integer.valueOf(m.group()).intValue();
			} else {
				validation_seed = 1;
			}
			
			
			
			// 构建ClusterEvaluation
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(clusterer);
			eval.evaluateClusterer(data);
			
			result = ClusterEvaluation.crossValidateModel("weka.clusterers.EM", data, validation_folds, null, new Random(validation_seed));
			
			// Visualization
			ClustererAssignmentsPlotInstances plotInstances = new ClustererAssignmentsPlotInstances();
			plotInstances.setClusterer(clusterer);
			plotInstances.setInstances(data);
			plotInstances.setClusterEvaluation(eval);
			plotInstances.setUp();
			
			String name = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
		    String cname = clusterer.getClass().getName();
		    if (cname.startsWith("weka.clusterers."))
		      name += cname.substring("weka.clusterers.".length());
		    else
		      name += cname;
		    name = name + " (" + data.relationName() + ")";
		    VisualizePanel vp = new VisualizePanel();
		    vp.setName(name);
		    vp.addPlot(plotInstances.getPlotData(cname));

		    // display data
		    // taken from: ClustererPanel.visualizeClusterAssignments(VisualizePanel)
		    JFrame jf = new JFrame("Weka Clusterer Visualize: " + vp.getName());
		    jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    jf.setSize(500, 400);
		    jf.getContentPane().setLayout(new BorderLayout());
		    jf.getContentPane().add(vp, BorderLayout.CENTER);
		    jf.setVisible(true);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
		
		
	}

	/**
	 * 动态生成XML配置文档
	 * @return 生成成功则为Boolean.TRUE，否则为Boolean.FALSE
	 * @param taskId 挖掘模块之ID
	 * 
	 */
	@Override
	public Boolean writeToXML(String taskId, String datasetFileName,
			String foldsNum, String seedNum, String displayModelInOldFormat,
			String maxIterations, String minStdDev, String numClusters,
			String seed) {
		
		Boolean result = Boolean.FALSE;
		
		
		
		
		try {
			
			// 建立XML结构
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document document = builder.newDocument();
			StringBuilder memo;
			
			// chart
			Element rootElement = document.createElement("chart");
			document.appendChild(rootElement);
			
			// task
			Element taskElement = document.createElement("task");
			rootElement.appendChild(taskElement);
			
			// category=loader
			Element loaderElement = document.createElement("stage");
			loaderElement.setAttribute("name", "读取数据源");
			loaderElement.setAttribute("category", "loader");
			loaderElement.setAttribute("x", "100");
			loaderElement.setAttribute("y", "50");
			memo = new StringBuilder();
			memo.append("数据来源：");
			if (datasetFileName != null)
				memo.append(datasetFileName);
			else
				memo.append("<未选择>");
			
			loaderElement.setAttribute("memo", memo.toString());
			taskElement.appendChild(loaderElement);
			
			// category=validation
			Element validationElement = document.createElement("stage");
			validationElement.setAttribute("name", "Cross-Validation");
			validationElement.setAttribute("category", "validation");
			validationElement.setAttribute("x", "200");
			validationElement.setAttribute("y", "50");
			memo = new StringBuilder();
			memo.append("Folds: ");
			if (foldsNum != null)
				memo.append(foldsNum + "\n");
			else
				memo.append("<未选择>\n");
			memo.append("Seed: ");
			if (seedNum != null)
				memo.append(seedNum);
			else
				memo.append("<未选择>");
			validationElement.setAttribute("memo", memo.toString());
			taskElement.appendChild(validationElement);
			
			// category=clusterer
			Element clustererElement = document.createElement("stage");
			clustererElement.setAttribute("name", "EM");
			clustererElement.setAttribute("category", "clusterer");
			clustererElement.setAttribute("x", "300");
			clustererElement.setAttribute("y", "50");
			
			memo = new StringBuilder();
			memo.append("输出旧格式：");
			String displayMode;
			if ("是".equals(displayModelInOldFormat)) {
				displayMode = "true";
			} else {
				displayMode = "false";
			}
			memo.append(displayMode + "\n");
			memo.append("最大迭代次数：");
			if (maxIterations != null)
				memo.append(seedNum + "\n");
			else
				memo.append("<未选择>\n");
			memo.append("最小标准差： ");
			if (minStdDev != null)
				memo.append(minStdDev + "\n");
			else
				memo.append("<未选择>\n");
			memo.append("聚类数：");
			if (numClusters != null)
				memo.append(numClusters + "\n");
			else
				memo.append("<未选择>\n");
			memo.append("随机数种子：");
			if (seed != null)
				memo.append(seed);
			else
				memo.append("<未选择>");
			
			clustererElement.setAttribute("memo", memo.toString());
			taskElement.appendChild(clustererElement);
			
			// category=performance
			Element performanceElement = document.createElement("stage");
			performanceElement.setAttribute("name", "聚类器性能检验");
			performanceElement.setAttribute("category", "performance");
			performanceElement.setAttribute("x", "400");
			performanceElement.setAttribute("y", "50");
			taskElement.appendChild(performanceElement);
			
			// category=textviewer
			Element textviewerElement = document.createElement("stage");
			textviewerElement.setAttribute("name", "查看文本结果");
			textviewerElement.setAttribute("category", "textviewer");
			textviewerElement.setAttribute("x", "500");
			textviewerElement.setAttribute("y", "50");
			taskElement.appendChild(textviewerElement);
			
			// Transitions.
			Element transitionsElement = document.createElement("transitions");
			rootElement.appendChild(transitionsElement);
			
			Element transition_firstElement = document.createElement("transition");
			transition_firstElement.setAttribute("end", "Cross-Validation设置");
			transition_firstElement.setAttribute("points", "");
			transition_firstElement.setAttribute("start", "读取数据源");
			transition_firstElement.setAttribute("memo", "数据集");
			transitionsElement.appendChild(transition_firstElement);
			
			Element transition_secondElement = document.createElement("transition");
			transition_secondElement.setAttribute("end", "聚类器");
			transition_secondElement.setAttribute("points", "");
			transition_secondElement.setAttribute("start", "Cross-Validation设置");
			transition_secondElement.setAttribute("memo", "训练数据集；\n测试数据集");
			transitionsElement.appendChild(transition_secondElement);
			
			Element transition_thirdElement = document.createElement("transition");
			transition_thirdElement.setAttribute("end", "聚类器性能检验");
			transition_thirdElement.setAttribute("points", "");
			transition_thirdElement.setAttribute("start", "聚类器");
			transitionsElement.appendChild(transition_thirdElement);
			
			Element transition_fourthElement = document.createElement("transition");
			transition_fourthElement.setAttribute("end", "查看文本结果");
			transition_fourthElement.setAttribute("points", "");
			transition_fourthElement.setAttribute("start", "聚类器性能检验");
			transitionsElement.appendChild(transition_fourthElement);
			
			
			// 生成XML配置文件
			TransformerFactory tfactory = TransformerFactory.newInstance();
			try {
				Transformer transformer = tfactory.newTransformer();
				transformer.setOutputProperty("encoding", "utf-8");
				DOMSource source = new DOMSource(document);
				
				// 获取文件保存路径
				String path;
				
				ConfigsDAO configsdao = new ConfigsDAO();
				ConfigsVO configsvo = configsdao.getConfig("KnowledgeFlowPath");
				path = configsvo.getContent();
				
				UserSession user = (UserSession) getThreadLocalRequest().getSession().getAttribute("UserSession");
				String user_id = String.valueOf(user.getUser_id());
				
				String dir_path = path + "/" + user_id;
				path = path + "/" + user_id + "/" + taskId + ".chart.xml";
				
				// 若目录不存在，则创建之
				if (!new File(dir_path).isDirectory())
					new File(dir_path).mkdirs();
				
				// 若文件不存在，则创建之
				if (!new File(path).isFile()) {
					new File(path).createNewFile();
				}
				
				StreamResult sresult = new StreamResult(new File(path));
				transformer.transform(source, sresult);
				
				result = Boolean.TRUE;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	} // End of writeToXML().

	
	
	
}
