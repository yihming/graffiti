package com.as.agency;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.as.*;
import com.as.dao.AgencyDAO;
import com.as.vo.AgencyVO;
import com.as.vo.Agency_listVO;

import com.sun.java_cup.internal.internal_error;

public class AgencyServletAdmin extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public AgencyServletAdmin() {
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
       doPost(request, response);
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
		request.setCharacterEncoding("UTF-8");
         String opflag = request.getParameter("opflag");
        
         AgencyVO agencyvo = new AgencyVO();
         AgencyDAO agencydao = new AgencyDAO();
         Agency_listVO agency_listvo = new Agency_listVO();
         int res=0;
         String flag = "0";//返回操作成功还是失败 //成功返回1 失败返回0
         if(opflag.equals("del")){
        	 int agency_list_id = Integer.parseInt(request.getParameter("agency_list_id"));
        	 System.out.println(agency_list_id);
        	 res = agencydao.setDelAgencyList(agency_list_id);
        	 if(res == 1){
        		 flag = "1";
        		 request.setAttribute("flag", flag);
        		 request.getRequestDispatcher("/admin/agency/index.jsp").forward(request,response);
        	 }
        	 else{
        		 
        		 request.setAttribute("flag", flag);
        		 request.getRequestDispatcher("/admin/agency/index.jsp").forward(request,response);
        	 }
         }
         
         else if(opflag.equals("update")){
        	 int ag_list_add;
        	 int ag_list_delete;
        	 int ag_list_update;
        	 int ag_list_select;
        	 
        		  ag_list_add = ((String)request.getParameter("ag_list_add"))==null?0:1;
        		  ag_list_delete =((String)request.getParameter("ag_list_delete"))==null?0:1;
        		  ag_list_update = ((String)request.getParameter("ag_list_update"))==null?0:1;
        		  ag_list_select =((String)request.getParameter("ag_list_select"))==null?0:1;
        	
        	 agency_listvo.setAgency_list_id(Integer.parseInt((String)request.getParameter("agency_list_id")));
        	 agency_listvo.setAg_list_name((String)request.getParameter("ag_list_name"));
        	 agency_listvo.setAg_list_url((String)request.getParameter("ag_list_url"));
        	 agency_listvo.setAg_list_add(ag_list_add);
        	 agency_listvo.setAg_list_delete(ag_list_delete);
        	 agency_listvo.setAg_list_update(ag_list_update);
        	 agency_listvo.setAg_list_select(ag_list_select);
        	
        	 
        	 
//        	 System.out.println(Integer.parseInt(request.getParameter("agency_list_select")));
        	 res = agencydao.setUpdateAgencyList(agency_listvo);
        	 if(res == 1){
        		 flag = "1";
        		 request.setAttribute("flag", flag);
        		 request.getRequestDispatcher("/admin/agency/index.jsp").forward(request,response);
        	 }
        	 else{
        		 
        		 request.setAttribute("flag", flag);
        		 request.getRequestDispatcher("/admin/agency/index.jsp").forward(request,response);
        	 }
         }
         
         else if(opflag.equals("add")){
        	 int ag_list_add;
        	 int ag_list_delete;
        	 int ag_list_update;
        	 int ag_list_select;
        	 
        		  ag_list_add = ((String)request.getParameter("ag_list_add"))==null?0:1;
        		  ag_list_delete =((String)request.getParameter("ag_list_delete"))==null?0:1;
        		  ag_list_update = ((String)request.getParameter("ag_list_update"))==null?0:1;
        		  ag_list_select =((String)request.getParameter("ag_list_select"))==null?0:1;
        	
        	 agency_listvo.setAg_list_name((String)request.getParameter("ag_list_name"));
        	 agency_listvo.setAg_list_url((String)request.getParameter("ag_list_url"));
        	 agency_listvo.setAg_list_add(ag_list_add);
        	 agency_listvo.setAg_list_delete(ag_list_delete);
        	 agency_listvo.setAg_list_update(ag_list_update);
        	 agency_listvo.setAg_list_select(ag_list_select);
        	
        	 
        	 
//        	 System.out.println(Integer.parseInt(request.getParameter("agency_list_select")));
        	 res = agencydao.setInsertAgencyList(agency_listvo);
        	 if(res == 1){
        		 flag = "1";
        		 request.setAttribute("flag", flag);
        		 request.getRequestDispatcher("/admin/agency/index.jsp").forward(request,response);
        	 }
        	 else{
        		 
        		 request.setAttribute("flag", flag);
        		 request.getRequestDispatcher("/admin/agency/index.jsp").forward(request,response);
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
