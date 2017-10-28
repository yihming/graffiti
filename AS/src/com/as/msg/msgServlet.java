package com.as.msg;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.as.*;
import com.as.dao.*;
//import com.as.dao.msgDAO;
import com.as.function.AS;
import com.as.function.UserSession;

public class msgServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public msgServlet() {
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

		int user_id = 49;//attention 以后要动态获取
		try{
			HttpSession session = request.getSession();
			//session = request.getSession();
			UserSession usersession = (UserSession) session.getAttribute("usersession");
			user_id = usersession.getUser_id();
System.out.println("msgServlet user_id:"+user_id);			
		}catch(Exception e){
			response.sendRedirect("login.jsp");
		}
		  				
		String submitFrom = new String();
		submitFrom = request.getParameter("submitFrom");	
		if("sendMessage".equals(submitFrom)){			
			int msg_count = 1;
			int msg_reply_to_id = 0;
			String msg_rece = " ",msg_title = " ",msg_intro = " ";			
			msg_rece = request.getParameter("msg_rece");
			msg_title = request.getParameter("msg_title");
			msg_intro = request.getParameter("msg_intro");		
			StringTokenizer tokens = new StringTokenizer(msg_rece,";");
			String[] receiver = new String[tokens.countTokens()];			
			if(tokens.countTokens()>0){
				msg_count = tokens.countTokens();
			}
			int temp_msg_reply_to_id = 0;
			temp_msg_reply_to_id = Integer.parseInt(request.getParameter("msg_reply_to_id"));			
			if(temp_msg_reply_to_id>0){
				msg_reply_to_id = temp_msg_reply_to_id; 
			}		
			msgDAO msgdao = new msgDAO();
			int msg_id = 0;
			msg_id = msgdao.setSendMsg(user_id, msg_title, msg_intro, msg_count, msg_reply_to_id);
			int i = 0;
			if(msg_id>0){
				while(tokens.hasMoreTokens()){
					UserDAO userdao = new UserDAO();
					userdao.getAllUserList();		
					int receiver_id = 0;
					receiver[i] = tokens.nextToken();				
					receiver_id = userdao.getUserIdByName(receiver[i]);
					if(receiver_id>0){
						msgdao.setSendRece(msg_id,receiver_id);
					}else{
						response.sendRedirect("../message/fail.html");
					}
					i++;
				}
			}else{
				response.sendRedirect("../message/fail.html");
			}
			//如何打印成功？
			//request.getRequestDispatcher("../message/sendMessage.jsp").forward(request, response);
			try{
				response.sendRedirect("../message/sendMessage.jsp?success=true");
			}catch(Exception e){
				response.sendRedirect("../message/sendMessage.jsp");
			}
		}else if("receiveBox".equals(submitFrom) ||"unReadBox".equals(submitFrom)){
			int msg_id = 0;
			msg_id = Integer.parseInt(request.getParameter("msg_id"));
			msgDAO msgdao = new msgDAO();
			if(msgdao.setDelMsg(msg_id, user_id, "msg_rece")){
				if("receiveBox".equals(submitFrom)){
					response.sendRedirect("../message/receiveBox.jsp");
				}else{
					response.sendRedirect("../message/unReadBox.jsp");
				}
			}else{
				response.sendRedirect("../message/fail.html");
			}
		}else if("sendBox".equals(submitFrom)){		
			int msg_id = 0;
			msg_id = Integer.parseInt(request.getParameter("msg_id"));
			msgDAO msgdao = new msgDAO();			
			if(msgdao.setDelMsg(msg_id, user_id, "msg"))
			response.sendRedirect("../message/sendBox.jsp");
		}else{
			response.sendRedirect("../message/fail.html");
		}
		out.close();
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
