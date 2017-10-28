package com.as.conference;
/********************************************************************
 * Version 1.3: Implement the insert room function.  (2009-8-27 15:48 Bauer Yung)
 * Version 1.4: Fail to implement the check input function. But realize that 
 * 				HttpServletRequest.getRequestDispatcher().forward() can only be executed successfully
 * 				when response has not been changed. (2009-8-27 19:10 Bauer Yung)
 * Version 1.8: Implement the searchAllRoom and delete room function. (2009-8-28 21:22 Bauer Yung)
 * Version 1.10: Implement the update room function. (2009-8-29 11:47 Bauer Yung)
 *******************************************************************/
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.as.dao.RoomDAO;
import com.as.function.PageHelp;
import com.as.vo.RoomVO;

public class RoomServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public RoomServlet() {
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
		// Set the CharacterSet of both response and request.
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		// Get the operation flag.
		String opflag = request.getParameter("opflag");
		
		if ("add".equals(opflag)) { // Execute the add operation.
			
			// Get room_name, room_addr and room_intro.
			String rname = request.getParameter("roomname");
			String raddr = request.getParameter("roomaddr");
			String rintro = request.getParameter("roomintro");
		
			// Get room_size, converting to Integer.
			String tmp = request.getParameter("roomsize");
			int rsize = Integer.parseInt(tmp);
		
			// Get room_tv, converting to Integer.
			tmp = request.getParameter("roomtv");
			int rtv = Integer.parseInt(tmp);
		
			// Get room_pc, converting to Integer.
			tmp = request.getParameter("roompc");
			int rpc = Integer.parseInt(tmp);
			
			// Get room_projector, converting to Integer.
			tmp = request.getParameter("roomprojector");
			int rprojector = Integer.parseInt(tmp);
			
			
			// Set these attributions into "roomvo".			
			RoomVO roomvo = new RoomVO();
			
			roomvo.setRoom_name(rname);
			roomvo.setRoom_addr(raddr);
			roomvo.setRoom_intro(rintro);
			roomvo.setRoom_size(rsize);
			roomvo.setRoom_tv(rtv);
			roomvo.setRoom_pc(rpc);
			roomvo.setRoom_projector(rprojector);
			
			//System.out.println(roomvo.getRoom_name());
			
			// "line" aims to counting the number of lines influenced by INSERT operation.
			int line = 0;
			RoomDAO roomdao = new RoomDAO();
			
			line = roomdao.setAddRoom(roomvo);
			
			if (line != 0) {
				// Success! Go to room_list.jsp.
				request.setAttribute("succ", "success");
				request.getRequestDispatcher("../admin/conference/room_list.jsp").forward(request, response);
			} else {
				// Failed! Return to add_room.jsp.
				request.setAttribute("fail", "failure");
				request.getRequestDispatcher("../admin/conference/add_room.jsp").forward(request, response);
			}
		} else if ("del".equals(opflag)) {
			int id = Integer.parseInt(request.getParameter("id"));
			RoomDAO roomdao = new RoomDAO();
			
			int count = roomdao.delRoom(id);
			if (count != 0) {
				request.getRequestDispatcher("../admin/conference/room_list.jsp").forward(request, response);
			} 
			
			
		} else if ("upd".equals(opflag)) {
			
			int id = Integer.parseInt(request.getParameter("id"));
			String rname = request.getParameter("roomname");
			String raddr = request.getParameter("roomaddr");
			int rsize = Integer.parseInt(request.getParameter("roomsize"));
			int rprojector = Integer.parseInt(request.getParameter("roomprojector"));
			int rprobroken = Integer.parseInt(request.getParameter("probroken"));
			int rtv = Integer.parseInt(request.getParameter("roomtv"));
			int rtvbroken = Integer.parseInt(request.getParameter("tvbroken"));
			int rpc = Integer.parseInt(request.getParameter("roompc"));
			int rpcbroken = Integer.parseInt(request.getParameter("pcbroken"));
			String rintro = request.getParameter("roomintro");
			
			RoomVO roomvo = new RoomVO();
			roomvo.setRoom_id(id);
			roomvo.setRoom_name(rname);
			roomvo.setRoom_addr(raddr);
			roomvo.setRoom_intro(rintro);
			roomvo.setRoom_size(rsize);
			roomvo.setRoom_projector(rprojector);
			roomvo.setPro_broken(rprobroken);
			roomvo.setRoom_pc(rpc);
			roomvo.setPc_broken(rpcbroken);
			roomvo.setRoom_tv(rtv);
			roomvo.setTv_broken(rtvbroken);
			
			int count = 0;
			RoomDAO roomdao = new RoomDAO();
			count = roomdao.updRoom(roomvo);
			
			if (count != 0) {
				request.setAttribute("res", "upd_succ");
				request.getRequestDispatcher("../admin/conference/room_list.jsp").forward(request, response);
			}
			
			
		} else if ("query".equals(opflag)) {
			
			
			
			
			
		} else if ("changePage".equals(opflag)) {
			String sqlCondition = request.getParameter("cond");
			
			String str_currentpage = request.getParameter("currentPage");
			int currentpage = Integer.parseInt(str_currentpage);
			
			RoomDAO roomdao = new RoomDAO();
			PageHelp pagehelp = new PageHelp();
			
			pagehelp = roomdao.getRoom_PageHelp(sqlCondition, currentpage);
			
			request.setAttribute("pagehelp", pagehelp);
			request.getRequestDispatcher("../admin/conference/room_list.jsp").forward(request, response);
		}
		 
			// Close the Out Pipeline.
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
