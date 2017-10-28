package com.as.role;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.as.*;
import com.as.dao.RoleDAO;
import com.as.role.*;
import com.as.vo.RoleVO;

public class RoleServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public RoleServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

 
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	   doPost(request, response);
	}

 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
        String opflag = request.getParameter("opflag");
    
        RoleVO rolevo = new RoleVO();
        RoleDAO roledao = new RoleDAO();
        int res=0;
        String flag = "0";//返回操作成功还是失败 //成功返回1 失败返回0
        
        if(opflag.equals("del")){
       	 int role_id = Integer.parseInt(request.getParameter("role_id"));
       	 res = roledao.setDelRole(role_id);
       	 if(res == 1){
       		 flag = "1";
       		 request.setAttribute("flag", flag);
       		 request.getRequestDispatcher("/admin/role/index.jsp").forward(request,response);
       	 }
       	 else{
       		 
       		 request.setAttribute("flag", flag);
       		 request.getRequestDispatcher("/admin/role/index.jsp").forward(request,response);
       	 }
        }
        
        else if(opflag.equals("update")){
        	 int role_id = Integer.parseInt(request.getParameter("role_id")); 
       	     String role_name = (String)request.getParameter("role_name");
       	     int meeting_agree=((String)request.getParameter("meeting_agree"))==null?0:1;
       	     int device_mag= ((String)request.getParameter("device_mag"))==null?0:1;
       	     int assign_work= ((String)request.getParameter("assign_work"))==null?0:1;
       	     int account_add_del= ((String)request.getParameter("account_add_del"))==null?0:1;
       	     int account_update= ((String)request.getParameter("account_update"))==null?0:1;
       	     int new_power1= ((String)request.getParameter("new_power1"))==null?0:1;
       	     int new_power2= ((String)request.getParameter("new_power2"))==null?0:1;
       	 
       		 rolevo.setRole_id(role_id);
       		 rolevo.setRole_name(role_name);
       		 rolevo.setMeeting_agree(meeting_agree);
       		 rolevo.setDevice_mag(device_mag);
       		 rolevo.setAssign_work(assign_work);
       		 rolevo.setAccount_add_del(account_add_del);
       		 rolevo.setAccount_update(account_update);
       		 rolevo.setNew_power1(new_power1);
       		 rolevo.setNew_power2(new_power2);
       	
       	 
//       	 System.out.println(Integer.parseInt(request.getParameter("agency_list_select")));
       	 res = roledao.setUpdateRole(rolevo);
       	 if(res == 1){
       		 flag = "1";
       		 request.setAttribute("flag", flag);
       		 request.getRequestDispatcher("/admin/role/index.jsp").forward(request,response);
       	 }
       	 else{
       		 
       		 request.setAttribute("flag", flag);
       		 request.getRequestDispatcher("/admin/role/index.jsp").forward(request,response);
       	 }
        }
        
        else if(opflag.equals("add")){
   	     String role_name = (String)request.getParameter("role_name");
   	     int meeting_agree=((String)request.getParameter("meeting_agree"))==null?0:1;
   	     int device_mag= ((String)request.getParameter("device_mag"))==null?0:1;
   	     int assign_work= ((String)request.getParameter("assign_work"))==null?0:1;
   	     int account_add_del= ((String)request.getParameter("account_add_del"))==null?0:1;
   	     int account_update= ((String)request.getParameter("account_update"))==null?0:1;
   	     int new_power1= ((String)request.getParameter("new_power1"))==null?0:1;
   	     int new_power2= ((String)request.getParameter("new_power2"))==null?0:1;
   	 
   		 rolevo.setRole_name(role_name);
   		 rolevo.setMeeting_agree(meeting_agree);
   		 rolevo.setDevice_mag(device_mag);
   		 rolevo.setAssign_work(assign_work);
   		 rolevo.setAccount_add_del(account_add_del);
   		 rolevo.setAccount_update(account_update);
   		 rolevo.setNew_power1(new_power1);
   		 rolevo.setNew_power2(new_power2);
   	
   	 
//   	 System.out.println(Integer.parseInt(request.getParameter("agency_list_select")));
   	 res = roledao.setInsertRole(rolevo);
   	 if(res == 1){
   		 flag = "1";
   		 request.setAttribute("flag", flag);
   		 request.getRequestDispatcher("/admin/role/index.jsp").forward(request,response);
   	 }
   	 else{
   		 
   		 request.setAttribute("flag", flag);
   		 request.getRequestDispatcher("/admin/role/index.jsp").forward(request,response);
   	    }
      }
        else if(opflag.equals("userrole_del")){
          	 int user_role_rs_id = Integer.parseInt(request.getParameter("user_role_rs_id"));
          	 res = roledao.setDelUserRole(user_role_rs_id);
          	 if(res == 1){
          		 flag = "1";
          		 request.setAttribute("flag", flag);
          		 request.getRequestDispatcher("/admin/role/userrole.jsp").forward(request,response);
          	 }
          	 else{
          		 
          		 request.setAttribute("flag", flag);
          		 request.getRequestDispatcher("/admin/role/userrole.jsp").forward(request,response);
          	 }
           }
	
	 else if(opflag.equals("userrole_add")){
   	 int user_id = Integer.parseInt(request.getParameter("user_id"));
   	 int role_id = Integer.parseInt(request.getParameter("role_id"));
   	 
   	 res = roledao.setInsertUserRole(user_id,role_id);
   	 if(res == 1){
   		 flag = "1";
   		 request.setAttribute("flag", flag);
   		 request.getRequestDispatcher("/admin/role/userrole.jsp").forward(request,response);
   	 }
   	else{
 		 request.setAttribute("flag", flag);
 		 request.getRequestDispatcher("/admin/role/userrole.jsp").forward(request,response);
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
