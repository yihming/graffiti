package com.as.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.as.dao.FriendDAO;
import com.as.vo.FriendClassVO;
import com.as.vo.FriendVO;

public class friendoperation extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public friendoperation() {
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
		String operation = "";
		operation = request.getParameter("operation");
		System.out.println(operation);

		
		FriendDAO frienddao = new FriendDAO();
		
		if("addfriend".equals(operation)){
			String friend_name = request.getParameter("friend_name");
			
			int f_class_id=-1;
			String class_id = request.getParameter("friend_class");
			f_class_id = Integer.parseInt(class_id);			
			
			String friend_birth = request.getParameter("friend_birth");
			
			String f_sex = request.getParameter("friend_sex");
			int friend_sex = -1;
			if(f_sex.equals("0")) friend_sex = 0;
			else friend_sex = 1;
			
			String friend_phone = request.getParameter("friend_phone");
			
			String friend_mobile = request.getParameter("friend_mobile");
			
			String friend_fax = request.getParameter("friend_fax");
			String friend_mail = request.getParameter("friend_mail");
			String friend_addr = request.getParameter("friend_addr");
			String friend_intro = request.getParameter("friend_intro");
			
			FriendVO friendvo = new FriendVO();
			friendvo.setF_class_id(f_class_id);
			friendvo.setFriend_addr(friend_addr);
			friendvo.setFriend_birth(friend_birth);
			friendvo.setFriend_intro(friend_intro);
			friendvo.setFriend_mail(friend_mail);
			friendvo.setFriend_name(friend_name);
			friendvo.setFriend_phone(friend_phone);
			friendvo.setFriend_sex(friend_sex);
			
			if(frienddao.setaddFriend(friendvo)) 
				response.sendRedirect("./card/friendlist.jsp?result=add_ok");
				
			else
				response.sendRedirect("./card/friendlist.jsp?result=add_failed");		
		}else if("updatefriend".equals(operation)){
			String friend_name = request.getParameter("friend_name");
			
			int f_class_id=-1;
			String class_id = request.getParameter("friend_class");
			f_class_id = Integer.parseInt(class_id);			
			
			String friend_birth = request.getParameter("friend_birth");
			
			String f_sex = request.getParameter("friend_sex");
			int friend_sex = -1;
			if(f_sex.equals("0")) friend_sex = 0;
			else friend_sex = 1;
			
			String friend_phone = request.getParameter("friend_phone");
			
			String friend_mobile = request.getParameter("friend_mobile");
			
			String friend_fax = request.getParameter("friend_fax");
			String friend_mail = request.getParameter("friend_mail");
			String friend_addr = request.getParameter("friend_addr");
			String friend_intro = request.getParameter("friend_intro");
			
			
			FriendVO friendvo = new FriendVO();
			friendvo.setF_class_id(f_class_id);
			friendvo.setFriend_addr(friend_addr);
			friendvo.setFriend_birth(friend_birth);
			friendvo.setFriend_intro(friend_intro);
			friendvo.setFriend_mail(friend_mail);
			friendvo.setFriend_name(friend_name);
			friendvo.setFriend_phone(friend_phone);
			friendvo.setFriend_sex(friend_sex);
			int friend_id = Integer.parseInt(request.getParameter("friend_id"));
			friendvo.setFriend_id(friend_id);
			if(frienddao.setUpdateFriend(friendvo))			
				response.sendRedirect("./card/friendlist.jsp?result=update_ok");
			else
				response.sendRedirect("./card/friendlist.jsp?result=update_failed");
		}else if("deletefriend".equals(operation)){
			
			int friend_id = Integer.parseInt(request.getParameter("friend_id"));
			if(frienddao.setdelFriendById(friend_id))
			response.sendRedirect("./card/friendlist.jsp?result=delete_ok");
			else
			response.sendRedirect("./card/friendlist.jsp?result=delete_failed");
		}
		else if("newclass".equals(operation)){
			String u_id = request.getParameter("user_id");
			String f_name = request.getParameter("newclassname");
			System.out.println(u_id);
			System.out.println(f_name);
		}else if("updateclass".equals(operation)){
			String f_class_name = request.getParameter("f_class_name");		
			String f_class_idd = request.getParameter("f_class_id");
			FriendClassVO friendclassvo = new FriendClassVO();
			int f_class_id = Integer.parseInt(f_class_idd);
			
			friendclassvo.setF_class_id(f_class_id);
			friendclassvo.setF_class_name(f_class_name);
			
			if(frienddao.setUpdateFriendClass(friendclassvo)){
				response.sendRedirect("./card/classmanage.jsp?result=update_ok");
			}else{
				response.sendRedirect("./card/classmanage.jsp?result=update_failed");
			}
		}else if("deleteclass".equals(operation)){
			String f_class_idd = request.getParameter("f_class_id");
			int f_class_id = Integer.parseInt(f_class_idd);
			if(frienddao.setDelFriendClass(f_class_id)){
				response.sendRedirect("./card/classmanage.jsp?result=delete_ok");
			}else{
				response.sendRedirect("./card/classmanage.jsp?result=delete_failed");
			}
		}else if("addclass".equals(operation)){
			String user_id = request.getParameter("user_id");
			String f_class_name = request.getParameter("f_class_name");
			FriendClassVO friendclassvo = new FriendClassVO();
			friendclassvo.setF_class_name(f_class_name);
			friendclassvo.setUser_id(Integer.parseInt(user_id));
			if(frienddao.setaddFriendClas(friendclassvo)){
				response.sendRedirect("./card/classmanage.jsp?result=add_ok");
			}else{
				response.sendRedirect("./card/classmanage.jsp?result=add_failed");
			}
		}
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
