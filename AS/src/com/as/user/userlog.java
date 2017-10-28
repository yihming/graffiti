package com.as.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.as.dao.UserDAO;
import com.as.function.UserSession;

public class userlog extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public userlog() {
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
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String user_name = (String) request.getParameter("user_name");
		String user_pswd = (String) request.getParameter("user_pswd");
		String action = (String) request.getParameter("action");
//		System.out.println(action);
//		System.out.println(user_name);
		UserDAO userdao = new UserDAO();
		
		if(action.equals("login")){
			HttpSession session = request.getSession();
			String rand = (String)session.getAttribute("rand");
//			System.out.println(rand);
			
			String rand1 = request.getParameter("rand");
//			System.out.println(rand1);
			
			if (userdao.login(user_name, user_pswd)) {
				if(rand.equals(rand1)){
					UserSession usersession = new UserSession();
					
					int id = userdao.getUserIdByName(user_name);
					usersession.setUser_id(id);
					usersession.setUser_name(user_name);
					if(userdao.getUserTrueNameById(id)!=null)
					usersession.setUser_true_name(userdao.getUserTrueNameById(id));
					usersession.setUser_group(userdao.getUserGroupById(id));
					
					session.setAttribute("usersession", usersession);
					//session有60分钟的时间
					 session.setMaxInactiveInterval(60*60);
					 
//					System.out.println(user_name);
					
					// session.setAttribute("user_id",id);
					response.sendRedirect("./index.jsp");	
				}else{
					response.sendRedirect("./login.jsp?flag=rand_wrong");				
				}
	
			} else {
				response.sendRedirect("./login.jsp?flag=pass_wrong");
			}
		}else if(action.equals("logout")){
			HttpSession session = request.getSession();
			UserSession usersession = (UserSession)session.getAttribute("usersession");
			usersession.setUser_id(0);
			usersession.setUser_name("");
			usersession.setUser_true_name("");
			response.sendRedirect("login.jsp?flag=close");
			
		}
		
		

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
