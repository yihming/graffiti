package com.as.conference;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.as.dao.DeviceDAO;
import com.as.vo.DeviceVO;
import com.as.vo.RoomVO;
import com.as.function.*;
public class DeviceServlet extends HttpServlet {
	String sqlCondition=" ";
	/**
	 * Constructor of the object.
	 */
	public DeviceServlet() {
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
		request.setCharacterEncoding("UTF-8");
		String flag=(String)request.getParameter("opflag");
		if("add".equals(flag)){
			String device_name=request.getParameter("equip_name");
			String tmp=request.getParameter("equip_count");
			int device_count = Integer.parseInt(tmp);
			String device_intro=request.getParameter("equip_intro");
			
			DeviceVO devicevo = new DeviceVO();
			devicevo.setDevice_name(device_name);
			devicevo.setDevice_count(device_count);
			devicevo.setDevice_damage(0);
			devicevo.setDevice_intro(device_intro);
			boolean b=false;
			DeviceDAO devicedao = new DeviceDAO();
			if(devicedao.setAddDevice(devicevo)){
				request.setAttribute("success", "add_success");
				request.getRequestDispatcher("../admin/conference/add_device.jsp").forward(request, response);
			}
		}else if("update".equals(flag)){
			String tmp=request.getParameter("equip_id");
			int device_id=Integer.parseInt(tmp);
			String device_name=request.getParameter("equip_name");
			tmp=request.getParameter("equip_count");
			int device_count = Integer.parseInt(tmp);
			tmp=request.getParameter("equip_damage");
			int device_damage = Integer.parseInt(tmp);
			String device_intro=request.getParameter("equip_intro");
			DeviceDAO devicedao=new DeviceDAO();
			DeviceVO devicevo = new DeviceVO();
			devicevo=devicedao.getDeviceById(device_id);
			int device_valid=devicevo.getDevice_valid()+(device_count-devicevo.getDevice_count())-(device_damage-devicevo.getDevice_damage());
			devicevo.setDevice_id(device_id);
			devicevo.setDevice_name(device_name);
			devicevo.setDevice_count(device_count);
			devicevo.setDevice_damage(device_damage);
			devicevo.setDevice_valid(device_valid);
			devicevo.setDevice_intro(device_intro);
			if(devicedao.setUdpDevice(devicevo)){
				request.setAttribute("success", "update_success");
				request.getRequestDispatcher("../admin/conference/update_device.jsp").forward(request, response);
			}					
		}else if("del".equals(flag)){
			int device_id=Integer.parseInt(request.getParameter("id"));
			DeviceDAO devicedao=new DeviceDAO();
			if(devicedao.setDelDevice(device_id)){
				request.setAttribute("success", "del_success");				
				request.getRequestDispatcher("../admin/conference/device_list.jsp").forward(request, response);
			}		
		}else if("query".equals(flag)){
			String en=request.getParameter("equip_name");
			if(en!=null){
				sqlCondition+=" and device_name like '%"+en+"%'";
			}						
			int currentPage=1;
			DeviceDAO devicedao=new DeviceDAO();
			PageHelp pagehelp;
			try {
				pagehelp = devicedao.queryDevice(sqlCondition, currentPage);
				request.setAttribute("pagehelp",pagehelp);
				request.getRequestDispatcher("../admin/conference/device_list.jsp").forward(request, response);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}else if("changePage".equals(flag)){
			sqlCondition=request.getParameter("condi");
			String currentpage=request.getParameter("currentPage");
			System.out.println(currentpage);
			int  intcurrentpage=Integer.parseInt(currentpage);
			DeviceDAO devicedao=new DeviceDAO();
			PageHelp pagehelp=new PageHelp();
				try {
					pagehelp=devicedao.queryDevice(sqlCondition,intcurrentpage);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			request.setAttribute("pagehelp",pagehelp);
			request.getRequestDispatcher("../admin/conference/device_list.jsp").forward(request,response);
			
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
