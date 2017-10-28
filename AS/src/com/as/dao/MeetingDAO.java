package com.as.dao;
/**************************************************************************
 * Version 1.7: Create MeetingDAO class. (2009-8-29 12:17 Bauer Yung)
 * Version 1.13: Add setDelMeeting(). (2009-8-31 17:24 Bauer Yung)
 * Version 1.15: Add setMeetingApproval(). (2009-9-1 0:39 Bauer Yung)
 *************************************************************************/
//import java.sql.Clob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.as.db.DbConnection;
import com.as.function.AS;
import com.as.function.PageHelp;
//import com.as.function.AS;
import com.as.vo.DeviceVO;
import com.as.vo.M_user_rsVO;
import com.as.vo.MeetingVO;

public class MeetingDAO {
	private DbConnection dbc;
	
	public MeetingVO getMeetingById(int m_id){
		dbc=new DbConnection();
		MeetingVO meetingvo=new MeetingVO();
	//	AS as_clob = new AS();
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement(" select * from meeting where m_id = ?");
			pst.setInt(1,m_id);
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();				
				if (dbc.rs.next()) {
					meetingvo.setM_id(m_id);
					meetingvo.setRoom_id(dbc.rs.getInt("room_id"));
					meetingvo.setM_title(dbc.rs.getString("m_title"));
					meetingvo.setM_start(dbc.rs.getString("m_start"));
					meetingvo.setM_end(dbc.rs.getString("m_end"));
					meetingvo.setM_num(dbc.rs.getInt("m_mum"));
					meetingvo.setM_state(dbc.rs.getInt("m_state"));
					meetingvo.setM_intro(dbc.rs.getString("m_intro"));
				}
			}			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		return meetingvo;		
	}
	
	/*
	 * Function: Insert the chosen meeting information into "meeting" table.
	 * Parameter: the chosen MeetingVO object.
	 * Return: m_id of the inserted meeting.
	 */
	public int setAddMeeting(MeetingVO meeting) {
		int id = -1;
		dbc = new DbConnection();
		
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
		
		try {
			CallableStatement cst = dbc.conn.prepareCall(sql.toString());
			int i = cst.executeUpdate();
			
			if (i != 0) {
				PreparedStatement pst = dbc.conn.prepareStatement("SELECT MEETING_SEQ.currval AS result FROM dual");
				dbc.rs = pst.executeQuery();
				
				if (dbc.rs.next()) {
					id = dbc.rs.getInt("result");
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
		
		return id;
	} // End of setAddMeeting().
	
	public boolean setMeetingApproval(int m_id, String decision) {
		dbc = new DbConnection();
		boolean flag = false;
		
		if ("Accepted".equals(decision)) {
			try {
				PreparedStatement pst = dbc.conn.prepareStatement("UPDATE meeting SET m_state = 1 WHERE m_id = ? ");
				pst.setInt(1, m_id);
				
				int lines = pst.executeUpdate();
				
				if (lines != 0) {
					flag = true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else if ("Refused".equals(decision)) {
			try {
				PreparedStatement pst = dbc.conn.prepareStatement("UPDATE meeting SET m_state = 2 WHERE m_id = ? ");
				pst.setInt(1, m_id);
				
				int lines = pst.executeUpdate();
				
				if (lines != 0) {
					flag = true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if (dbc.conn != null) {
			dbc.closeConnection();
		}
		
		return flag;
	} // End of setMeetingApproval().
	
	/*
	 * 功能：获取所有的移动设备的列表
	 * 参数：空
	 * 返回值：数据库中移动设备的列表
	 * */
	public List getAllMeeting(){
		dbc=new DbConnection();
		List list = new ArrayList();
		MeetingVO meetingvo;
		
		try { 
			dbc.st = dbc.conn.createStatement();
			dbc.rs = dbc.st.executeQuery("SELECT * FROM meeting ORDER BY meeting_id");

			while (dbc.rs.next()) {
				meetingvo = new MeetingVO();
				meetingvo.setM_id(dbc.rs.getInt("m_id"));
				meetingvo.setRoom_id(dbc.rs.getInt("room_id"));
				meetingvo.setM_title(dbc.rs.getString("m_title"));
				meetingvo.setM_start(dbc.rs.getString("m_start"));
				meetingvo.setM_end(dbc.rs.getString("m_end"));
				meetingvo.setM_num(dbc.rs.getInt("m_num"));
				meetingvo.setM_state(dbc.rs.getInt("m_state"));
				meetingvo.setM_intro(dbc.rs.getString("m_intro"));
				list.add(meetingvo);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list;				
	}

	public PageHelp queryMeeting(String condi,int currentPage) throws SQLException {
		dbc=new DbConnection();
		dbc.st =dbc.conn.createStatement();

		int recordcount=0;
		String sql="select count(*) recordcount from meeting where 1=1 "+condi+"";
		dbc.rs=dbc.st.executeQuery(sql);
		if(dbc.rs.next()){
			recordcount=dbc.rs.getInt("recordcount");
		}
		int startNum=(currentPage - 1) * PageHelp.pagesize+1;//由于数据库中没有第0条记录所以要进行+1修正
		int endNum= currentPage* PageHelp.pagesize+1;//查询结束行号
		String pagesql="select * from (" +
										"select a.* ,rownum rc from(" +
																	"select * from meeting where 1=1 "+condi+" order by m_id desc" +
																") a where rownum<"+endNum+"" +
										") b where rc >="+startNum+"";	
		dbc.rs=dbc.st.executeQuery(pagesql);
		List meetings=new ArrayList();
		MeetingVO meetingvo =null;
		while(dbc.rs.next()){
			meetingvo= new MeetingVO();
			meetingvo.setM_id(dbc.rs.getInt("m_id"));
			meetingvo.setRoom_id(dbc.rs.getInt("room_id"));
			meetingvo.setM_title(dbc.rs.getString("m_title"));
			meetingvo.setM_start(dbc.rs.getString("m_start"));
			meetingvo.setM_end(dbc.rs.getString("m_end"));
			meetingvo.setM_num(dbc.rs.getInt("m_num"));
			meetingvo.setM_state(dbc.rs.getInt("m_state"));
			meetingvo.setM_intro(dbc.rs.getString("m_intro"));

			meetings.add(meetingvo);
		}
		PageHelp pagehelp= new PageHelp(currentPage,recordcount,condi,meetings);
		return pagehelp;
	}
	
	//SELECT meeting.* FROM m_user_rs, meeting WHERE meeting.m_id = m_user_rs.m_id AND user_id = ? AND applier_state = 1
	//SELECT userinfo.* FROM userinfo, m_user_rs WHERE m_user_rs.user_id = userinfo.user_id AND m_user_rs.m_id = ? AND m_user_rs.applier_state = 0
	
	public boolean setDelMeeting(int m_id) {
		boolean flag = false;
		dbc = new DbConnection();
		
		CallableStatement cst;
		try {
			cst = dbc.conn.prepareCall("DELETE FROM meeting WHERE m_id = ? ");
			cst.setInt(1, m_id);
			
			int lines = cst.executeUpdate();
			if (lines != 0) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return flag;
		
		
	} // End of setDelMeeting().
	
	public List getAllWaitingMeeting(){
		dbc = new DbConnection();
		List waitingmeeting = new ArrayList();
		
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT meeting.*, room.room_name FROM meeting, room WHERE meeting.room_id = room.room_id AND meeting.m_state = 0 ");
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				
				while (dbc.rs.next()) {
					MeetingVO meeting = new MeetingVO();
					meeting.setM_id(dbc.rs.getInt("m_id"));
					meeting.setM_title(dbc.rs.getString("m_title"));
					meeting.setRoom_id(dbc.rs.getInt("room_id"));
					meeting.setM_num(dbc.rs.getInt("m_num"));
					meeting.setM_start(dbc.rs.getString("m_start"));
					meeting.setM_end(dbc.rs.getString("m_end"));
					waitingmeeting.add(meeting);
				}
			} else {
				waitingmeeting = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return waitingmeeting;
		
	} // End of getAllWaitingMeeting().
}
