package org.bnuminer.server;

import java.io.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.bnuminer.Functions;
import org.bnuminer.UserSession;
import org.bnuminer.dao.ConfigsDAO;
import org.bnuminer.dao.FileDAO;
import org.bnuminer.dao.User_file_infoDAO;
import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;
import org.bnuminer.vo.User_file_infoVO;

public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String savePath;
	

	public String getSavePath() {
		return savePath;
	}
	
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
	
	/**
	 * 上传文件
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html; charset=utf-8");
			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}

		
		// 获取savePath
		ConfigsVO configsvo = new ConfigsVO();
		ConfigsDAO configsdao = new ConfigsDAO();
		configsvo = configsdao.getConfig("UploadPath");
		savePath = configsvo.getContent();
		
		
		// 获取用户ID
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("UserSession");
		System.out.println(userSession.getUser_name());
		String user_id = Integer.toString(userSession.getUser_id());
		savePath = savePath + "/" + user_id;
		String fileName = null;
		
		
		
		ServletFileUpload fileUpload = new ServletFileUpload();
		
		
		try {
			FileItemIterator iter = fileUpload.getItemIterator(request);
			
			
			while (iter.hasNext()) {
				FileItemStream item = (FileItemStream) iter.next();
				InputStream stream = item.openStream();
				
				
				if (! item.isFormField()) {
					
					if (item.getFieldName().equals("file")) { // 对上传文件进行操作
						
						// 若目标文件夹不存在，则创建之
						if (! new File(savePath).isDirectory())
							new File(savePath).mkdirs();
						
						// 获取原始文件名
						fileName = item.getName();
						
						if (fileName.contains("\\")) { // 针对IE浏览器
							fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
						}

						
						// 获取文件类型
						String type = fileName.substring(fileName.lastIndexOf(".") + 1);
						
						// 获取除去扩展名后的文件名
						String fileShortname = fileName.substring(0, fileName.lastIndexOf("."));
						
						// 检查当前用户目录下是否有重名文件？
						File thisFile = new File(savePath + "/" + fileName);
						int count = 1;
						while (thisFile.exists()) {
							fileName = fileShortname + "(" + String.valueOf(count) + ")" + "." + type;
							thisFile = new File(savePath + "/" + fileName);
							System.out.println(thisFile.getName());
							++count;
						}
						
						// 获取输入流
						BufferedInputStream inputStream = new BufferedInputStream(stream);
						
						// 获取文件输出流
						BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(thisFile));
						
						// 上传文件至指定位置
						Streams.copy(inputStream, outputStream, true);
						
						// 写入数据库操作
						// 写入file_info表
						FileInfoVO fileinfovo = new FileInfoVO();
						fileinfovo.setFile_name(fileName);
						fileinfovo.setFile_create_time(Functions.getCurrentDatetime());
						
						File chosen_file = new File(savePath + "/" + fileName);
						java.sql.Date date = new Date(chosen_file.lastModified());
						java.sql.Time time = new Time(chosen_file.lastModified());
						fileinfovo.setFile_modify_time(date.toString() + " " + time.toString());
						
						fileinfovo.setFile_size(new Long(chosen_file.length()).intValue());
						fileinfovo.setFile_type(type);
						
						FileDAO filedao = new FileDAO();
						filedao.setAddFile(fileinfovo);
						
						// 写入user_file_info表
						User_file_infoVO user_file_infovo = new User_file_infoVO();
						int file_id = filedao.getSearchFileId(fileinfovo);
						user_file_infovo.setUser_id(Integer.valueOf(user_id));
						user_file_infovo.setFile_id(file_id);
						user_file_infovo.setIs_owner(1);
						
						User_file_infoDAO user_file_infodao = new User_file_infoDAO();
						if (user_file_infodao.setAddUser_file_info(user_file_infovo)) { // 插入记录成功
							// 返回成功信息
							PrintWriter out = response.getWriter();
							out.print("上传文件成功！");
						}
						
						
						
						
					}
				
					
				} // End of if. 
			
			} // End of while.
			
		}  catch (Exception e) {
			
			PrintWriter out;
			out = response.getWriter();
			
			out.print("很抱歉，上传过程中出现后台错误！");
			
		}
	
	} // End of doPost().
}
