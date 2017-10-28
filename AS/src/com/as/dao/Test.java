package com.as.dao;

/********************************************************************
 * Test.java - description
 * 		Our test class.
 * 
 *******************************************************************/

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.as.db.DbConnection;
import com.as.vo.MeetingVO;
import com.as.vo.RoomVO;
import com.as.vo.DeviceVO;

public class Test {
	public DbConnection dbc;
	
	
	public boolean[] get_id() {
		dbc = new DbConnection();
		boolean[] res = {false};
		
		DeviceDAO devicedao = new DeviceDAO();
		List li = new ArrayList();
		
		li = devicedao.getAllDevice();
		Iterator it = li.iterator();
		int count = 1;
		
		while (it.hasNext()) {
			DeviceVO device = new DeviceVO();
			
			device = (DeviceVO) it.next();
			
		}
		
		return res;
	}
	
	public void get_room(String m_start, String m_end, int m_num, int m_pc, String m_start1, String m_end1) {
		dbc = new DbConnection();
		ResultSet rs2 = null;
		
		StringBuffer sql1 = new StringBuffer();
		sql1.append("SELECT * FROM room WHERE room_id IN ")
			.append("(SELECT room_id FROM meeting WHERE m_start < to_date('")
			.append(m_start)
			.append("', 'yyyy-mm-dd hh24-mi') AND m_end < to_date('")
			.append(m_end)
			.append("', 'yyyy-mm-dd hh24-mi') AND room_id IN ")
			.append("(SELECT room_id FROM room WHERE room_size >= ")
			.append(m_num)
			.append(" AND room_pc - pc_broken >= ")
			.append(m_pc)
			.append(" ))");
		
		StringBuffer sql2 = new StringBuffer(); 
		sql2.append("SELECT * FROM room WHERE room_id IN ")
		.append("(SELECT room_id FROM meeting WHERE m_start > to_date('")
		.append(m_start1)
		.append("', 'yyyy-mm-dd hh24-mi') AND m_end > to_date('")
		.append(m_end1)
		.append("', 'yyyy-mm-dd hh24-mi') AND room_id IN ")
		.append("(SELECT room_id FROM room WHERE room_size >= ")
		.append(m_num)
		.append(" AND room_pc - pc_broken >= ")
		.append(m_pc)
		.append(" ))");
		
		try {
			PreparedStatement pst = dbc.conn.prepareStatement(sql1.toString());
			
			List li = new ArrayList();
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					RoomVO room = new RoomVO();
					
					room.setRoom_id(dbc.rs.getInt("room_id"));
					room.setRoom_name(dbc.rs.getString("room_name"));
					room.setRoom_addr(dbc.rs.getString("room_addr"));
					room.setRoom_size(dbc.rs.getInt("room_size"));
					room.setRoom_intro(dbc.rs.getString("room_intro"));
					room.setRoom_pc(dbc.rs.getInt("room_pc"));
					room.setRoom_tv(dbc.rs.getInt("room_tv"));
					room.setRoom_projector(dbc.rs.getInt("room_projector"));
					room.setTv_broken(dbc.rs.getInt("tv_broken"));
					room.setPc_broken(dbc.rs.getInt("pc_broken"));
					room.setPro_broken(dbc.rs.getInt("pro_broken"));
					li.add(room);
				}
			}
			
			
			
			pst = dbc.conn.prepareStatement(sql2.toString());
			
			if (pst.execute()) {
				rs2 = pst.executeQuery();
				
				while (rs2.next()) {
					RoomVO tmp = new RoomVO();
					
					tmp.setRoom_id(rs2.getInt("room_id"));
					tmp.setRoom_name(rs2.getString("room_name"));
					tmp.setRoom_addr(rs2.getString("room_addr"));
					tmp.setRoom_intro(rs2.getString("room_intro"));
					tmp.setRoom_size(rs2.getInt("room_size"));
					tmp.setRoom_pc(rs2.getInt("room_pc"));
					tmp.setRoom_projector(rs2.getInt("room_projector"));
					tmp.setRoom_tv(rs2.getInt("room_tv"));
					tmp.setPc_broken(rs2.getInt("pc_broken"));
					tmp.setPro_broken(rs2.getInt("pro_broken"));
					tmp.setTv_broken(rs2.getInt("tv_broken"));
					
					if (!li.contains(tmp)) {
						li.add(tmp);
					}
				}
				
			}
			
			
			
			Iterator it = li.iterator();
			while (it.hasNext()) {
				RoomVO res = (RoomVO) it.next();
				System.out.println(res.getRoom_id());
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
	}
	
	public void add_meeting() {
		dbc = new DbConnection();
		
		MeetingVO meeting = new MeetingVO();
		
		meeting.setM_end("2009-8-31 13:00");
		meeting.setM_start("2009-8-31 15:00");
		meeting.setM_intro("好哇");
		meeting.setRoom_id(77);
		meeting.setM_title("测试");
		meeting.setM_state(0);
		meeting.setM_num(30);
		int i = 0;
		int res = 0;
		
		try {
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("INSERT INTO meeting(m_id, room_id, m_title, m_start, m_end, m_num, m_intro, m_state) VALUES(null, ")
			   .append(meeting.getRoom_id())
			   .append(", '")
			   .append(meeting.getM_title())
			   .append("', to_date('")
			   .append(meeting.getM_start())
			   .append("', 'yyyy-mm-dd hh24-mi'), to_date('")
			   .append(meeting.getM_end())
			   .append("', 'yyyy-mm-dd hh24-mi'), ")
			   .append(meeting.getM_num())
			   .append(", '")
			   .append(meeting.getM_intro())
			   .append("', ")
			   .append(meeting.getM_state())
			   .append(")");
			
			System.out.println(sql);
			CallableStatement cst = dbc.conn.prepareCall(sql.toString());
			
			i = cst.executeUpdate();
			
			if (i != 0) {
				dbc.st = dbc.conn.createStatement();
				dbc.rs = dbc.st.executeQuery("SELECT MEETING_SEQ.currval AS result FROM dual");
				
				if (dbc.rs.next()) {
					res = dbc.rs.getInt("result");
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		System.out.println(res);
		
		
	}
	
	public void show_string(){
		StringBuffer str = new StringBuffer();
		
		str.append("这是系统消息：\n")
		   .append("\t会议名称：\'");
		
		System.out.println(str.toString());
	}
	
	public static void main(String[] args) {
		Test my_test = new Test();
		
		//my_test.get_room("2009-8-31 13:00", "2009-9-1 00:00", 0, 1, "2009-8-14 13:00", "2009-8-25 00:00");
		my_test.show_string();
	}
}
