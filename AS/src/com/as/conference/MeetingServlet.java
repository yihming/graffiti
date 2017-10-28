package com.as.conference;
/***********************************************************************
 * Version 1.2: Create MeetringServlet class. (2009-8-29 12:18 Bauer Yung)
 * Version 1.4: Add "add_first" process partly. (2009-8-30 15:45 Bauer Yung)
 **********************************************************************/

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.as.dao.*;
import com.as.function.PageHelp;
import com.as.function.UserSession;
import com.as.vo.*;

public class MeetingServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public MeetingServlet() {
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
			HttpSession meeting_session = request.getSession();
			String sqlCondition=" ";
			
			if("finish".equals(flag)){	
				boolean result = false;
				
				// Get the meeting information.
				String m_title = request.getParameter("m_title");
				String m_intro = request.getParameter("m_intro");
				MeetingVO meeting = (MeetingVO) meeting_session.getAttribute("partly_meeting");
				meeting.setM_title(m_title);
				meeting.setM_intro(m_intro);
				
				// Get the M_device_rsVO List.
				List m_devices = new ArrayList();
				m_devices = (List) meeting_session.getAttribute("used_device");
				
				// Get the sponser ID.
				UserSession usersession = (UserSession) meeting_session.getAttribute("usersession");
				int sponser_id = usersession.getUser_id();
				//int sponser_id = 41;
				
				// Get the userlist List.
				List users = new ArrayList();
				users = (List) meeting_session.getAttribute("ulist");
				
				// Insert the meeting information.
				MeetingDAO meetingdao = new MeetingDAO();
				int m_id = meetingdao.setAddMeeting(meeting);    // Return the m_id of the inserted meeting.
				
				if (m_id != -1) {
					
					// Insert the M_device_rsVO List.
					M_device_rsDAO m_device_rsdao = new M_device_rsDAO();
					boolean second = m_device_rsdao.setAddRecords(m_id, m_devices);
					
					if (second) {
						
						// Update records in device table.
						DeviceDAO devicedao = new DeviceDAO();
						boolean third = devicedao.setUpdValid(m_devices);
						
						if (third) {
							
							// Insert the sponser ID and the userlist List.
							M_user_rsDAO m_user_rsdao = new M_user_rsDAO();
							result = m_user_rsdao.setAddRecords(sponser_id, users, m_id);
						}
						
					}
				}
				
				if (result) {
					request.setAttribute("succ", "success");
					request.getRequestDispatcher("/AS/conference/meeting_list.jsp").forward(request, response);
					
				} else {
					request.setAttribute("fail", "failure");
					request.getRequestDispatcher("/AS/conference/meeting_list.jsp").forward(request, response);
				}				
				
			}else if("inner_list".equals(flag)){
				String title=(String)request.getParameter("title");
				String intro=(String)request.getParameter("intro");
				UserDAO userdao=new UserDAO();
				List list1=userdao.getAllUserList();
				List list2=new ArrayList();
				Iterator it=list1.iterator();
				while(it.hasNext())
				{
					UserVO uservo=(UserVO)it.next();
					String user_id=""+uservo.getUser_id();
					String s=request.getParameter(user_id);
					if(s!=null){
						list2.add(uservo);
					}
				}
				request.setAttribute("title", title);
				request.setAttribute("intro", intro);				
				request.setAttribute("userlist", list2);
				request.getRequestDispatcher("./finish_meeting.jsp").forward(request, response);
				
			}else if("edit_applyer".equals(flag)){
				List list=new ArrayList();
				list=(List)request.getSession().getAttribute("ulist");
				String title=(String)request.getParameter("title");
				String intro=(String)request.getParameter("intro");
				request.setAttribute("title", title);
				request.setAttribute("intro", intro);
				request.setAttribute("ulist", list);
				
				request.getRequestDispatcher("./inner_list.jsp").forward(request, response);
							
			}else if ("add_first".equals(flag)) { // The first part of add meeting process.
				
				// Get start time of meeting.
				String m_start = request.getParameter("mstart_date") + " " + request.getParameter("mstart_hour") + ":" + request.getParameter("mstart_minute");
				// Get end time of meeting.
				String m_end = request.getParameter("mend_date") + " " + request.getParameter("mend_hour") + ":" + request.getParameter("mend_minute");
				
				int m_num = Integer.parseInt(request.getParameter("m_num"));		// Get the total number of participants of meeting.
				
				// Get number of projector the meeting required.
				String tmp = request.getParameter("projector");	
				int m_projector = 0;
				if ("".equals(tmp)) {
					m_projector = 0;
				} else {
					m_projector = Integer.parseInt(tmp);
				}
				
				// Get number of TV the meeting required.
				tmp = request.getParameter("tv");
				int m_tv = 0;
				if ("".equals(tmp)) {
					m_tv = 0;
				} else {
					m_tv = Integer.parseInt(tmp);
				}
				
				// Get number of PC the meeting required.
				tmp = request.getParameter("pc");
				int m_pc = 0;
				if ("".equals(tmp)) {
					m_pc = 0;
				} else {
					m_pc = Integer.parseInt(tmp);
				}
				
				// Cache partly information into the meeting record.
				MeetingVO meeting = new MeetingVO();
				meeting.setM_start(m_start);
				meeting.setM_end(m_end);
				meeting.setM_num(m_num);
				meeting.setM_state(0);		// Set m_state to be Waiting for approval.
				
				meeting_session.setAttribute("partly_meeting", meeting);   // Set this MeetingVO as an attribute of meeting_session.
				
				
				DeviceDAO devicedao = new DeviceDAO();
				int count = 1;	// Initialize the index for adding parameters.
				
				List device_list = new ArrayList();
				device_list = devicedao.getDeviceIds();		// Get ID List of records in table "device" corresponding to the sequence in page "add_meeting.jsp".
				Iterator it = device_list.iterator();
				
				List used_device = new ArrayList();			// List to store M_Device_RS records for the meeting.
				
				while (it.hasNext()) {
					// Get a device record.
					Integer device_id = new Integer(0);
					device_id = (Integer) it.next();
					
					// Get a parameter from request.
					String num = String.valueOf(count);
					String index = "device_" + num + "_num";
					int device_num = Integer.parseInt(request.getParameter(index));
					
					
					if (device_num != 0) {
						//Add a created M_Device_RS record into List "used_device".
						M_device_rsVO m_device_rs = new M_device_rsVO();
						m_device_rs.setDevice_id(device_id.intValue());
						m_device_rs.setUsed_device_num(device_num);
						used_device.add(m_device_rs);
					}
					
					++count;
					
				} // End of while.
				
				meeting_session.setAttribute("used_device", used_device);   // Set the "used_device" List as an attribute of meeting_session.
				
				List room_id_available = new ArrayList();		// List to store IDs of each available room.
				RoomDAO roomdao = new RoomDAO();
				room_id_available = roomdao.getRooms_User(m_start, m_end, m_num, m_projector, m_tv, m_pc);
				
				meeting_session.setAttribute("room_show", room_id_available);		// Set the "room_id_available" List as an attribute of meeting_session.
				
				request.getRequestDispatcher("./order_room.jsp").forward(request, response);
				
				
				
			} else if ("order".equals(flag)) { // The second part of adding meeting process.
				int room_id = Integer.parseInt(request.getParameter("room_id"));
				MeetingVO meeting = (MeetingVO) meeting_session.getAttribute("partly_meeting");
				meeting.setRoom_id(room_id);
				meeting_session.setAttribute("partly_meeting", meeting);
				
				request.getRequestDispatcher("./finish_meeting.jsp").forward(request, response);
				
				
			} else if ("changePage_of_rooms".equals(flag)) { // Change to a new page of room list.
				
				String str_currentpage = request.getParameter("currentPage");
				int currentpage = Integer.parseInt(str_currentpage);
				
				List room_ids = new ArrayList();
				room_ids = (List) request.getSession().getAttribute("room_show");   // May generate some Bugs.
				
				RoomDAO roomdao = new RoomDAO();
				PageHelp pagehelp_of_rooms = new PageHelp();
				
				pagehelp_of_rooms = roomdao.getRooms_Available(room_ids, currentpage);
				
				meeting_session.setAttribute("pagehelp_of_rooms", pagehelp_of_rooms);
				request.getRequestDispatcher("./order_room.jsp").forward(request, response);
				
			} else if("query".equals(flag)){
				String tmp=(String)request.getSession().getAttribute("user_id");
				int uid=0;
				if(tmp!=null){
					uid=Integer.parseInt(tmp);
					uid=52;/////测试用
					sqlCondition+=" ";
				}						
				//System.out.println(sqlCondition);
				int currentPage=1;
				MeetingDAO meetingdao=new MeetingDAO();
				PageHelp pagehelp;
				try {
					pagehelp = meetingdao.queryMeeting(sqlCondition, currentPage);
					request.setAttribute("pagehelp",pagehelp);
					request.getRequestDispatcher("../conference/edit_meeting.jsp").forward(request, response);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if("changePage".equals(flag)){
				sqlCondition=request.getParameter("condi");
				String currentpage=request.getParameter("currentPage");
				System.out.println(currentpage);
				int  intcurrentpage=Integer.parseInt(currentpage);
				MeetingDAO meetingdao=new MeetingDAO();
				PageHelp pagehelp=new PageHelp();
					try {
						pagehelp=meetingdao.queryMeeting(sqlCondition,intcurrentpage);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				request.setAttribute("pagehelp",pagehelp);
				request.getRequestDispatcher("../conference/edit_meeting.jsp").forward(request,response);
				
			} else if ("del".equals(flag)) {
				int m_id = Integer.parseInt(request.getParameter("m_id"));
				
				// Delete m_user_rs records based on m_id.
				M_user_rsDAO m_user_rsdao = new M_user_rsDAO();
				boolean result = false;
				
				boolean first = m_user_rsdao.setDelRecords(m_id);
				if (first) {
					
					// Delete m_device_rs records based on m_id, and return a List of DeviceVO objects.
					List released_devices = new ArrayList();
					M_device_rsDAO m_device_rsdao = new M_device_rsDAO();
					released_devices = m_device_rsdao.getUsedDeviceByM_id(m_id);
					boolean second = m_device_rsdao.setDelRecords(m_id);
					
					if (second) {
						
						// Update information of device table.
						DeviceDAO devicedao = new DeviceDAO();
						boolean third = devicedao.setUpdValid(released_devices);
						
						if (third) {
							
							// Delete this meeting record in meeting table.
							MeetingDAO meetingdao = new MeetingDAO();
							result = meetingdao.setDelMeeting(m_id);
						}
						
					}
				}
				
				if (result) {
					request.setAttribute("del_succ", "success");
					request.getRequestDispatcher("/AS/conference/meeting_list.jsp").forward(request, response);
				} else {
					request.setAttribute("del_fail", "failed");
					request.getRequestDispatcher("/AS/conference/meeting_list.jsp").forward(request, response);
				}
			} else if ("pass".equals(flag)) {
				int m_id = Integer.parseInt(request.getParameter("m_id"));
				boolean result = false;
				
				// Get the meeting information.
				MeetingDAO meetingdao = new MeetingDAO();
				MeetingVO meeting = meetingdao.getMeetingById(m_id);
				
				// Get the user_id List.
				M_user_rsDAO m_user_rsdao = new M_user_rsDAO();
				List user_ids = new ArrayList();
				user_ids = m_user_rsdao.getPartner_id(m_id);
				
				// Alter the m_state of the meeting record.
				if (meetingdao.setMeetingApproval(m_id, "Accepted")) {
					msgDAO msgdao = new msgDAO();
					
					// Get the ID of sponser.
					int sponser_id = m_user_rsdao.getSponser_ID(m_id);
					
					if (msgdao.setSendNotice(user_ids, sponser_id, meeting)) {
						request.setAttribute("pass_succ", "success");
						request.getRequestDispatcher("/AS/admin/conference/approve_meeting.jsp").forward(request, response);
					} else {
						request.setAttribute("pass_fail", "failed");
						request.getRequestDispatcher("/AS/admin/conference/approve_meeting.jsp").forward(request, response);
					}
				} else {
					request.setAttribute("pass_fail", "failed");
					request.getRequestDispatcher("/AS/admin/conference/approve_meeting.jsp").forward(request, response);
				}
				
			} else if ("refused".equals(flag)) {
				
				MeetingDAO meetingdao = new MeetingDAO();
				M_user_rsDAO m_user_rsdao = new M_user_rsDAO();
				msgDAO msgdao = new msgDAO();
				int m_id = Integer.parseInt(request.getParameter("m_id"));
				MeetingVO meeting = meetingdao.getMeetingById(m_id);
				int sponser_id = m_user_rsdao.getSponser_ID(m_id);
				
				if (meetingdao.setMeetingApproval(m_id, "Refused")) {
					if (msgdao.sendRefuseToSponser(sponser_id, meeting)) {
						request.setAttribute("refuse_succ", "success");
						request.getRequestDispatcher("/AS/admin/conference/approve_meeting.jsp").forward(request, response);
					} else {
						request.setAttribute("refuse_fail", "failed");
						request.getRequestDispatcher("/AS/admin/conference/approve_meeting.jsp").forward(request, response);
					}
				} else {
					request.setAttribute("refuse_fail", "failed");
					request.getRequestDispatcher("/AS/admin/conference/approve_meeting.jsp").forward(request, response);
				}
			}
			
	} // End of doPost().

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
