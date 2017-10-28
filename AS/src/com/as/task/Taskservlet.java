package com.as.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Clob;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.as.dao.TaskDAO;
import com.as.dao.UserDAO;
import com.as.dao.msgDAO;
import com.as.function.AS;
import com.as.function.UserSession;

public class Taskservlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public Taskservlet() {
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
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		
		int user_id = 42;
		/*
		HttpSession session = request.getSession();
		try{
			  UserSession usersession = new UserSession();
			  session = request.getSession();
			  usersession = (UserSession)session.getAttribute("usersession");
			  user_id = usersession.getUser_id();
			}catch(Exception e){
			   response.sendRedirect("../login.jsp");
			}
		*/
		AS send_time = new AS();
		
		String submitFrom = new String();
		submitFrom = request.getParameter("submitFrom");
///System.out.println("submitFrom:"+submitFrom);		
		if("addNew".equals(submitFrom)){	
///System.out.println("in submitFrom:"+submitFrom);			
				
	
	
	
				int msg_reply_to_id = 0;		
				String task_title=" ";
				String task_intro=" ";
				String task_end = new String(),task_start = new String();
				String user_name="";

				user_name=request.getParameter("user_name");
				task_title = request.getParameter("task_title");
				task_intro = request.getParameter("task_intro");
				if((request.getParameter("task_start"))!=null)
					task_start = request.getParameter("task_start");
				if((request.getParameter("task_end"))!=null)
					task_end = request.getParameter("task_end");
				if((request.getParameter("task_start_hour"))!=null){
					task_start += " "+request.getParameter("task_start_hour");
					if((request.getParameter("task_start_min"))!=null){
						task_start += ":"+request.getParameter("task_start_min");
					}
				}
				if((request.getParameter("task_end_hour"))!=null){
					task_end += " "+request.getParameter("task_end_hour");
					if((request.getParameter("task_end_min"))!=null){
						task_end += " "+request.getParameter("task_end_min");
					}
				}
				
				
				

				int temp_msg_reply_to_id = 0;
				temp_msg_reply_to_id = Integer.parseInt(request.getParameter("msg_reply_to_id"));			
				if(temp_msg_reply_to_id>0){
					msg_reply_to_id = temp_msg_reply_to_id; 
				}		
				TaskDAO taskdao = new TaskDAO();
				int task_id_temp = 0;
				int tasker_temp=0;
System.out.println("temp_task_start:"+task_start);	
System.out.println("temp_task_end:"+task_end);				
System.out.println("user_id:"+user_id);				
System.out.println("user_name:"+user_name);			
System.out.println("task_title:"+task_title);
System.out.println("task_intro:"+task_intro);
	
	int temp_user_id=0;
	UserDAO userdao=new UserDAO();
	temp_user_id=userdao.getUserIdByName(user_name);
		
System.out.println("get user id: "+temp_user_id);	
		task_id_temp = taskdao.setNewTask(user_id,task_start,task_end, task_title, task_intro);
System.out.println("task_id_temp: "+task_id_temp);
		tasker_temp=taskdao.setTasker(task_id_temp,temp_user_id );
				
				
				//打印成功？
				response.sendRedirect("../task/addnew.jsp");
			}
	
		System.out.println("hello bulbul,this is TaskServlet");
		out
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("--- yes sir! ---  ");
		out.print(this.getClass());
		//out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	//	request.getRequestDispatcher("./task/mytask.jsp").forward(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	

}
