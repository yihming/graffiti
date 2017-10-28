package org.bnuminer.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bnuminer.UserSession;
import org.bnuminer.dao.ConfigsDAO;
import org.bnuminer.dao.FileDAO;
import org.bnuminer.vo.ConfigsVO;
import org.bnuminer.vo.FileInfoVO;

public class DownloadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		req.setCharacterEncoding("utf-8");
		res.setCharacterEncoding("utf-8");
		
		// 获取文件ID 
		Integer file_id = Integer.valueOf(req.getParameter("id"));
		
		// 获取文件保存路径
		ConfigsDAO configsdao = new ConfigsDAO();
		ConfigsVO configsvo = configsdao.getConfig("UploadPath");
		String path = configsvo.getContent();
		
		UserSession user = (UserSession) req.getSession().getAttribute("UserSession");
		String user_id = String.valueOf(user.getUser_id());
		path = path + "/" + user_id + "/";
		
		FileDAO filedao = new FileDAO();
		FileInfoVO fileinfovo = filedao.getSearchFileById(file_id);
		path = path + fileinfovo.getFile_name();
		
		// 检查文件是否存在
		if (! new File(path).exists()) {
			res.setContentType("text/html;charset=utf-8");
			res.getWriter().println("指定文件不存在！");
			return;
		}
		
		// 读取文件名
		String fileName = fileinfovo.getFile_name();
		
		// 写流文件到前端浏览器
		ServletOutputStream out = res.getOutputStream();
		res.setHeader("Content-disposition", "attachment;filename=" + fileName);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		bis = new BufferedInputStream(new FileInputStream(path));
		bos = new BufferedOutputStream(out);
		byte[] buff = new byte[2048];
		int bytesRead;
		
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
			
		}
		
		if (bis != null)
			bis.close();
		if (bos != null)
			bos.close();
		
	}
}
