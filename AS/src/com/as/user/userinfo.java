/**
 * version 1.4 2009-8-29
 * Functions: userinformation update, change password
 * Guo Kai
 */
package com.as.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.as.dao.UserDAO;
import com.as.function.UserSession;
import com.as.vo.UserVO;

public class userinfo extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public userinfo() {
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
		String action = request.getParameter("action");
		if (action.equals("update")) {
			UserVO uservo = new UserVO();
			UserDAO userdao = new UserDAO();

			UserSession usersession = new UserSession();
			HttpSession session = request.getSession();
			usersession = (UserSession) session.getAttribute("usersession");

			uservo.setUser_id(usersession.getUser_id());

			String sex = request.getParameter("user_sex");
			if (sex.equals("1"))
				uservo.setUser_sex(1);
			else if (sex.equals("0"))
				uservo.setUser_sex(0);

			uservo.setUser_birthday(request.getParameter("user_birthday"));
			uservo.setUser_office(request.getParameter("user_office"));
			uservo.setUser_phone(request.getParameter("user_phone"));
			uservo.setUser_fax(request.getParameter("user_fax"));
			uservo.setUser_mail(request.getParameter("user_mail"));
			uservo.setUser_mobile(request.getParameter("user_mobile"));
			uservo.setUser_intro(request.getParameter("user_intro"));

			if (userdao.setUpdateUserInfor(uservo)) {
				response.sendRedirect("./user/info.jsp?result=info_ok");
			} else {
				response.sendRedirect("./user/info.jsp?result=info_failed");
			}
		} else if (action.equals("change_pass")) {
			UserDAO userdao = new UserDAO();
			String pass1 = request.getParameter("oldpswd");
			String pass2 = request.getParameter("newpswd1");

			String user_name = request.getParameter("user_name");

			UserVO uservo = new UserVO();
			if (userdao.setUpdataPswd(user_name, pass1, pass2)) {
				response.sendRedirect("./user/info.jsp?result=pswd_ok");
			} else
				response.sendRedirect("./user/info.jsp?result=pswd_failed");
		} else if (action.equals("newuser")) {
			String user_name = request.getParameter("user_name");
			String user_true_name = request.getParameter("user_true_name");
			String user_pswd = request.getParameter("user_pswd");
			String sex= request.getParameter("user_sex");
			
			int user_sex;
			if(sex.equals("0")) user_sex = 0;
			else user_sex = 1;
			
			String user_birth = request.getParameter("user_birth");
			int dept_id = Integer.parseInt(request.getParameter("user_dept"));
			
			String user_number = request.getParameter("user_number");
			String user_office = request.getParameter("user_office");
			String user_phone = request.getParameter("user_phone");
			String user_mobile = request.getParameter("user_mobile");
			String user_mail = request.getParameter("user_mail");
			String user_fax = request.getParameter("user_fax");
			String user_card = request.getParameter("user_card");
			int user_wage = Integer.parseInt(request.getParameter("user_wage"));
			int user_bonus = Integer.parseInt(request.getParameter("user_bonus"));
			
			String user_intro = request.getParameter("user_intro");
			String user_picture = request.getParameter("user_picture");
			
			UserVO uservo = new UserVO();
			UserDAO userdao = new UserDAO();
			
			uservo.setUser_true_name(user_true_name);
			uservo.setUser_name(user_name);
			uservo.setUser_pswd(user_pswd);
			uservo.setUser_sex(user_sex);
			uservo.setUser_birthday(user_birth);
			uservo.setDept_id(dept_id);
			uservo.setUser_number(user_number);
			uservo.setUser_office(user_office);
			uservo.setUser_phone(user_phone);
			uservo.setUser_mobile(user_mobile);
			uservo.setUser_mail(user_mail);
			uservo.setUser_fax(user_fax);
			uservo.setUser_card(user_card);
			uservo.setUser_wage(user_wage);
			uservo.setUser_bonus(user_bonus);
			uservo.setUser_intro(user_intro);
			uservo.setUser_picture(user_picture);
			
			if(userdao.setAddUser(uservo)){
				response.sendRedirect("./admin/info/adduser.jsp?result=newuser_ok");
			}
			else
				response.sendRedirect("./admin/info/adduser.jsp?result=newuser_failed");
			
			
			
			
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
