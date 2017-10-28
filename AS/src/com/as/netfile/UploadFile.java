package com.as.netfile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.*;
import com.as.*;
import com.as.dao.*;
import com.as.function.AS;
import com.as.function.UserSession;
import com.as.vo.NetfileVO;
import com.as.db.DbConnection;
public class UploadFile extends HttpServlet {
     
	// private String uploadPath = "C:\\upload\\"; // 用于存放上传文件的目录
	 private String tempPath = "C:\\temp\\"; // 用于存放临时文件的目录

	/**
	 * Constructor of the object.
	 */
	public UploadFile() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
       
			doPost(request,response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String submitFrom = new String();
		submitFrom = request.getParameter("submitFrom");
System.out.println("in "+submitFrom);
		if("user_file".equals(submitFrom)){
			int netfile_id = 0;
			netfile_id = Integer.parseInt(request.getParameter("netfile_id"));
			System.out.println("file_id:"+netfile_id);
			NetfileDAO netfiledao=new NetfileDAO();
			if(netfiledao.setDelNetfile(netfile_id))
				response.sendRedirect("../netfile/UserFile.jsp");
		}
		else if("user_folder".equals(submitFrom)){
			int folder_id =0;
			folder_id =Integer.parseInt(request.getParameter("folder_id"));
			NetfileDAO netfiledao=new NetfileDAO();
			if(netfiledao.setDelFolder(folder_id))
				response.sendRedirect("../netfile/UserFolder.jsp");
		}
        //DiskFileUpload netfile=new DiskFileUpload();
       DiskFileItemFactory   factory   =   new   DiskFileItemFactory();   
       ServletFileUpload   netfile   =   new   ServletFileUpload(factory);   
        
       netfile.setSizeMax(10485760);
       factory.setSizeThreshold(4096);
       factory.setRepository(new File(tempPath));
       List fileItems=new ArrayList();
       
       try {
	       fileItems = netfile.parseRequest(request);
	       System.out.println("filename:");
		 Iterator i = fileItems.iterator();
	       // 依次处理每一个文件：
	       while(i.hasNext()) {
	           FileItem fi = (FileItem)i.next();
	           // 获得文件名，这个文件名包括路径：
	           if(!fi.isFormField()){
	           String fileName = fi.getName();
	           long size=fi.getSize();
	           int end = fileName.length();
	           int begin = fileName.lastIndexOf("\\");
	           String name = fileName.substring(begin + 1);
    // System.out.println("aaaa:"+test);
	System.out.println("filename:"+fileName);
	           if(fileName!=null) {
	               // 在这里可以记录用户和文件信息
	               // ...
	               // 写入文件a.txt，你也可以从fileName中提取文件名：
				   //1.jpg---->guokai.jpg
	              try {
					fi.write(new File(getServletContext().getRealPath("/")+"disk", fileName.substring(begin+1,end)));
	System.out.println(getServletContext().getRealPath("/")+"disk");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	           }
	           NetfileDAO netfiledao= new NetfileDAO();
	           List fileList =new ArrayList();
	          String  sql="select folder_id from folder where folder_name='41'";
	          DbConnection dbc = new DbConnection();
	          try {
				dbc.rs=dbc.st.executeQuery(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	          try {
				while(dbc.rs.next()){
					int folder_id=dbc.rs.getInt("folder_id");
				String path=getServletContext().getRealPath("/")+"disk"+"\"+"41"+"\"+name;
				  netfiledao.setAddNetfile(folder_id,name,path);
				  System.out.println(folder_id+name+path);
           }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       }
	}
       }catch (FileUploadException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
		    // 文件夹不存在就自动创建：
     /*   if(!new File(uploadPath).isDirectory())
		        new File(uploadPath).mkdirs();
		    if(!new File(tempPath).isDirectory())
		        new File(tempPath).mkdirs();
*/
	}

}
