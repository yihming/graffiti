       package com.as.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.as.db.DbConnection;
import com.as.vo.*;
import com.as.function.*;
public class msgDAO {
	private DbConnection dbc ;	
	//public msgDAO(){
	//	dbc = new DbConnection();//一直连接？not close？
	//}
	
	/**
	 * function:发送msg，把msg存入msg表
	 * 
	 * @param 
	 * 发件人存储用户名
	 * 初始消息（不是针对某封邮件的回复）msg_reply_to_id设置为0
	 * @return 该信息的msg_id
	 */
	
	public int setSendMsg(int user_id,String msg_title,String msg_intro,
			int msg_count,int msg_reply_to_id){
		int sendNum = 0;
		String sql = new String();
		
		String msg_sender = " ";
		sql = "select * from userinfo where user_id="+user_id;
		
//for test
//System.out.println("1:"+sql);
		dbc = new DbConnection();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				msg_sender = dbc.rs.getString("user_name");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AS send_time = new AS();
		String msg_time= send_time.getsqlDateTime();//2009-08-27 12:23:46 包含格式
		
		int msg_sender_del = 0;
		int msg_first_id = 0;
		
		if(msg_reply_to_id>0){
			try {
				dbc.rs = dbc.st.executeQuery("select msg_first_id from msg where msg_reply_to_id='"+msg_reply_to_id+"'");
				int first_id = 0;
				if(dbc.rs.next()){
					first_id = dbc.rs.getInt("msg_first_id");
					}
				if(first_id>0){
					msg_first_id = first_id;
				}else{
					msg_first_id = msg_reply_to_id;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sql = "insert into msg values(null,"+user_id+",'"+msg_sender+"','"+msg_title
			+"','"+msg_intro+"',"+msg_time+","+msg_count+","+msg_sender_del+","
			+msg_reply_to_id+","+msg_first_id+")";
		
//for test
//System.out.println("2:"+sql);
//
		
		int affectedRows = 0;
		try {
			affectedRows = dbc.st.executeUpdate(sql);			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(affectedRows>0){
			try {
				dbc.rs = dbc.st.executeQuery("select msg_seq.currval as max_id from dual");
				while(dbc.rs.next()){
					sendNum = dbc.rs.getInt("max_id");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dbc.closeConnection();
		return sendNum;
	}
	
	/*
	 * Function:
	 * 		Send group mails to partners when a meeting is approved.
	 * 
	 * Parameters: 
	 * 		List users 			--			 List<Integer> storing partners' IDs.
	 * 		MeetingVO meeting 	--			 Information about the meeting.
	 * Return:
	 * 		boolean flag		--			 True: If each partner receives this mail.
	 * 										 False: If no. 
	 */
	public boolean setSendNotice(List users, int sponser_id, MeetingVO meeting) { 
		boolean flag = false;
		boolean first = false;
		dbc = new DbConnection();
		msgDAO msgdao = new msgDAO();
		Iterator it = users.iterator();
		RoomDAO roomdao = new RoomDAO();
		RoomVO room = roomdao.getRoomByM_id(meeting.getM_id());
		
		int partner_count = users.size();
		int count = 0;
		StringBuffer confirm = new StringBuffer();
		confirm.append("这是系统消息：")
			   .append("您所申请的会议已审批通过！会议名称——")
			   .append(meeting.getM_title())
			   .append("；")
			   .append("会议室——")
			   .append(room.getRoom_name())
			   .append("；")
			   .append("开始时间——")
			   .append(meeting.getM_start().substring(0, 16))
			   .append("；")
			   .append("结束时间——")
			   .append(meeting.getM_end().substring(0, 16))
			   .append("。通知已发往与会人员，请做好相关准备！祝好！");
		msgVO msg_sponser = new msgVO();
		AS send_time = new AS();
		PreparedStatement pst;
		try {
			StringBuffer sql = new StringBuffer();
			
			sql.append("INSERT INTO msg(msg_id, user_id, msg_sender, msg_title, msg_intro, msg_time, msg_count, msg_sender_del, msg_reply_to_id, msg_first_id) VALUES(null, ")
			   .append(-1)
			   .append(", ")
			   .append("'系统消息', ")
			   .append("'会议审批通过', '")
			   .append(confirm.toString())
			   .append("', ")
			   .append(send_time.getsqlDateTime())
			   .append(", ")
			   .append(1)
			   .append(", 0, 0, 0) "); 
			
			System.out.println(sql);
			
			pst = dbc.conn.prepareStatement(sql.toString());
			
			int line = pst.executeUpdate();
			int record_id = -1;
			pst = dbc.conn.prepareStatement("SELECT MSG_SEQ.currval AS record_id FROM dual ");
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					record_id = dbc.rs.getInt("record_id");
				}
			}
			
			if (line != 0 && record_id != -1) {
				
				first = msgdao.setSendRece(record_id, sponser_id);
				
			}
			
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		StringBuffer content = new StringBuffer();
		content.append("这是系统消息：")
			   .append("会议名称——")
			   .append(meeting.getM_title())
			   .append("；")
			   .append("会议室——")
			   .append(room.getRoom_name())
			   .append("；")
			   .append("开始时间——")
			   .append(meeting.getM_start().substring(0, 16))
			   .append("；")
			   .append("结束时间——")
			   .append(meeting.getM_end().substring(0, 16))
			   .append("；")
			   .append("会议内容——")
			   .append(meeting.getM_intro())
			   .append("。敬候您的参与！详情请见‘会议查询’板块。");
		
		try {
			StringBuffer sql = new StringBuffer();
				   sql.append("INSERT INTO msg(msg_id, user_id, msg_sender, msg_title, msg_intro, msg_time, msg_count, msg_sender_del, msg_reply_to_id, msg_first_id) VALUES(null, ")
				   	  .append(-1)
				   	  .append(", ")
				   	  .append("'系统消息', ")
				   	  .append("'会议通知', '")
				   	  .append(content.toString())
				   	  .append("', ")
				   	  .append(send_time.getsqlDateTime())
				   	  .append(", ")
				   	  .append(partner_count)
				   	  .append(", 0, 0, 0) ");
			
			pst = dbc.conn.prepareStatement(sql.toString());
			
			int lines = pst.executeUpdate();
			pst = dbc.conn.prepareStatement("SELECT MSG_SEQ.currval AS msg_id FROM dual ");
			int msg_id = -1;
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					msg_id = dbc.rs.getInt("msg_id");
				}
			}
			
			if (lines != 0 && msg_id != -1) {
				while (it.hasNext()) {
					Integer user_id = (Integer) it.next();
					
					
					if (msgdao.setSendRece(msg_id, user_id.intValue())) {
						++count;
					}
					
				} // End of while().
				
				if (count == partner_count && first == true) {
					flag = true;
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
		
		
		return flag;
		
		
	} // End of setSendNotice();
	
	public boolean sendRefuseToSponser(int sponser_id, MeetingVO meeting) {
		dbc = new DbConnection();
		boolean flag = false;
		int msg_id = -1;
		msgDAO msgdao = new msgDAO();
		
		StringBuffer refuse = new StringBuffer();
		refuse.append("这是系统消息：")
			  .append("非常抱歉！您所申请的会议“")
			  .append(meeting.getM_title())
			  .append("”未能通过审批！")
			  .append("请尽快联系有关部门！");
		
		AS send_time = new AS();
		PreparedStatement pst;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO msg(msg_id, user_id, msg_sender, msg_title, msg_intro, msg_time, msg_count, msg_sender_del, msg_reply_to_id, msg_first_id) VALUES(null, ")
			   .append(-1)
			   .append(", ")
			   .append("'系统消息', ")
			   .append("'会议审批被驳回', '")
			   .append(refuse.toString())
			   .append("', ")
			   .append(send_time.getsqlDateTime())
			   .append(", ")
			   .append(1)
			   .append(", 0, 0, 0) ");
			
			pst = dbc.conn.prepareStatement(sql.toString());
			
			int line = pst.executeUpdate();
			
			pst = dbc.conn.prepareStatement("SELECT MSG_SEQ.currval AS msg_id FROM dual ");
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					msg_id = dbc.rs.getInt("msg_id");
				}
			}
			
			if (line != 0 && msg_id != -1) {
				if (msgdao.setSendRece(msg_id, sponser_id)) {
					flag = true;
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
		
		return flag;
		
	} // End of sendRefuseToSponser().
	
	/**
	 * 将发送信息的收件人列表存入msg_rece
	 * @param
	 * @return
	 */
	
	public boolean setSendRece(int msg_id,int user_id){
		boolean receiveFlag = false;
		String sql = new String();
		
		int msg_rece_state = 0;
		sql = "insert into msg_rece values(null,"+msg_id+","+user_id+","+msg_rece_state+")";
//for test
//System.out.println("3:"+sql);
//		
		int affectedRows = 0;
		dbc = new DbConnection();
		try {
			affectedRows = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(affectedRows>0){
			receiveFlag = true;
		}
		dbc.closeConnection();
		return receiveFlag;
	}
	
	
	
	/**
	 * function:查询msg
	 * 
	 * @return:msg ArrayList
	 */
	public List getMsg(int user_id,String pattern,int startNum,int endNum){
		List msgList = new ArrayList();
		String sql = new String();
		
		/*if("unRead".equals(pattern)){
			//sql = "select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender,msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
					//" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state=0 and msg.msg_id = msg_rece.msg_id order by msg_time desc";
			if(endNum>0){
				sql = "select * from (select a.*,rownum rc from " +
					"(select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender," +
					"msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
					" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state=0 " +
					"and msg.msg_id = msg_rece.msg_id order by msg_time desc) a " +
					"where rownum<="+endNum+") b where rc>"+startNum;
			}else{
				sql = "select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender,msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
					" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state=0 and msg.msg_id = msg_rece.msg_id order by msg_time desc";
			}
System.out.println("unRead sql:"+sql);			
		}
		else if("receive".equals(pattern)){
			//sql = "select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender,msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
					//" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state=1 and msg.msg_id = msg_rece.msg_id order by msg_time desc";
			if(endNum>0){
				sql = "select * from " +
					"(select a.*,rownum rc from " +
					"(select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender," +
					"msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
					" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state=1 " +
					"and msg.msg_id = msg_rece.msg_id order by msg_time desc) a " +
					"where rownum<="+endNum+") b" +
					" where rc >"+startNum;
			}else{
				sql = "select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender," +
					"msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
					" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state=1 " +
					"and msg.msg_id = msg_rece.msg_id order by msg_time desc";
			}
System.out.println("receive sql:"+sql);	
		}
		*/
		int state =0 ;
		if("unRead".equals(pattern))
		{
			state=0;
		}
		else if("receive".equals(pattern)){
			state=1;
		}
		else if("website".equals(pattern)){
			sql = "select ";
		}
		if(endNum>0){
			sql = "select * from " +
				"(select a.*,rownum rc from " +
				"(select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender," +
				"msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
				" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state="+state+
				"and msg.msg_id = msg_rece.msg_id order by msg_time desc) a " +
				"where rownum<="+endNum+") b" +
				" where rc >"+startNum;
		}else{
			sql = "select msg.msg_id as msg_id,msg.user_id as user_id,msg_title,msg_sender," +
				"msg_intro,msg_time,msg_count,msg_sender_del,msg_reply_to_id,msg_first_id" +
				" from msg,msg_rece where msg_rece.user_id="+user_id+" and msg_rece_state="+state+
				"and msg.msg_id = msg_rece.msg_id order by msg_time desc";
		}
		dbc = new DbConnection();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				msgVO msg = new msgVO();
				AS as = new AS();
				msg.setMsg_id(dbc.rs.getInt("msg_id"));
				msg.setUser_id(dbc.rs.getInt("user_id"));
				msg.setMsg_sender(dbc.rs.getString("msg_sender"));
				msg.setMsg_title(dbc.rs.getString("msg_title"));				
				msg.setMsg_intro(as.Clob_To_String(dbc.rs.getClob("msg_intro")));
//System.out.println("msg_title:"+msg.getMsg_title()+"msg_intro:"+msg.getMsg_intro());				
				msg.setMsg_time(dbc.rs.getString("msg_time"));
				msg.setMsg_count(dbc.rs.getInt("msg_count"));
				msg.setMsg_sender_del(dbc.rs.getInt("msg_sender_del"));
				msg.setMsg_reply_to_id(dbc.rs.getInt("msg_reply_to_id"));
				msg.setMsg_first_id(dbc.rs.getInt("msg_first_id"));
						    
				msgList.add(msg);	
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbc.closeConnection();
		return msgList;
	}
	/**
	 * get message by msg_id 
	 * @param msg_id
	 * @return
	 */
	public msgVO getMsgById(int msg_id){
		msgVO msgvo = new msgVO();
		String sql = "select * from msg where msg_id="+msg_id;
		dbc = new DbConnection();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				AS as = new AS();
				msgvo.setMsg_id(dbc.rs.getInt("msg_id"));
				msgvo.setUser_id(dbc.rs.getInt("user_id"));
				msgvo.setMsg_sender(dbc.rs.getString("msg_sender"));
				msgvo.setMsg_title(dbc.rs.getString("msg_title"));				
				msgvo.setMsg_intro(as.Clob_To_String(dbc.rs.getClob("msg_intro")));				
				msgvo.setMsg_time(dbc.rs.getString("msg_time"));
				msgvo.setMsg_count(dbc.rs.getInt("msg_count"));
				msgvo.setMsg_sender_del(dbc.rs.getInt("msg_sender_del"));
				msgvo.setMsg_reply_to_id(dbc.rs.getInt("msg_reply_to_id"));
				msgvo.setMsg_first_id(dbc.rs.getInt("msg_first_id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbc.closeConnection();
		return msgvo;
	}
	/**
	 * get message_receivers' name String by msg_id
	 * @param msg_id
	 * @return
	 */
	public String getMsgReceNamesById(int msg_id){
		String rece_names = new String();
		String sql = "select user_name from userinfo where user_id in" +
				"(select user_id  from msg where msg_id="+msg_id+") union "+
				"select user_name from userinfo where user_id in (select user_id from msg_rece where msg_id="+msg_id+")";
		dbc = new DbConnection();
	
		try {                                                        
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				rece_names += dbc.rs.getString("user_name")+";";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
System.out.println("getMsgReceNamesById sql:"+sql+"	rece_names:"+rece_names);			
		dbc.closeConnection();
		return rece_names;
	}
	/**
	 * 查询sendBox的各种信息
	 * @param user_id
	 * @return
	 */
	public List getSendMsg(int user_id,int startNum,int endNum)
	{
		List sendMsgList =new ArrayList();
		String sql1= new String();
		String sql2= new String();
		java.sql.Statement st1,st2;
		java.sql.ResultSet rs1,rs2;
		
		if(endNum>0)
		{
		sql1= "select * from(select a.*,rownum rc from " +
				"(select* from msg where msg.user_id="+user_id+" and msg.msg_sender_del=0 order by msg.msg_time desc) a " +
				"where rownum<="+endNum+") b where rc>"+startNum;
		}
		else
		{
		 sql1="select * from msg where msg.user_id ="+user_id+" and msg.msg_sender_del=0 order by msg.msg_time desc"	;
		}
		
System.out.println("getSendBox sql:"+sql1);
		dbc = new DbConnection();
		try {			
			st1=dbc.conn.createStatement();
			rs1=st1.executeQuery(sql1);
			
			while(rs1.next()){
				msg_allVO msg_allvo = new msg_allVO();
				AS as =new AS();
				msgVO msg =new msgVO();
				msg.setMsg_id(rs1.getInt("msg_id"));
				msg.setUser_id(rs1.getInt("user_id"));
				msg.setMsg_sender(rs1.getString("msg_sender"));
				msg.setMsg_title(rs1.getString("msg_title"));	
//System.out.println("getSendMsg msg_title:"+rs1.getString("msg_title"));				
				msg.setMsg_intro(as.Clob_To_String(rs1.getClob("msg_intro")));
				msg.setMsg_time(rs1.getString("msg_time"));
//System.out.println("Msg_time:"+rs1.getString("msg_time"));
				msg.setMsg_count(rs1.getInt("msg_count"));
				msg.setMsg_sender_del(rs1.getInt("msg_sender_del"));
				msg.setMsg_reply_to_id(rs1.getInt("msg_reply_to_id"));
				msg.setMsg_first_id(rs1.getInt("msg_first_id"));
				msg_allvo.setMsgvo(msg);
				//int msgid=rs1.getInt("msg_id");
				sql2="select userinfo.user_name from userinfo where userinfo.user_id "+
			    " in (select msg_rece.user_id from msg_rece where msg_rece.msg_id ="+rs1.getInt("msg_id")+")";
				st2=dbc.conn.createStatement();
				rs2=st2.executeQuery(sql2);
				String rece_names=new String ();
				while(rs2.next()){
					
					//msg_allvo.setMsg_receivers(rs2.getString("user_name"));
					rece_names +=rs2.getString("user_name")+";";
				}
				msg_allvo.setMsg_receivers(rece_names);
			 //System.out.println(rece_names);
				sendMsgList.add(msg_allvo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sendMsgList;
	}
	/**
	 * 
	 */
	public boolean setReadMsg(int msg_id){
		boolean setReadFlag = false;
		String sql = new String();
		dbc = new DbConnection();
		sql = "update msg_rece set msg_rece_state=1 where msg_id="+msg_id;
//System.out.println("SetReadMsg sql:"+sql);		
		int affectedRows = 0;
		try {
			affectedRows = dbc.st.executeUpdate(sql);
			if(affectedRows>0){
//System.out.println("affectedRows:"+affectedRows);				
				setReadFlag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbc.closeConnection();
		return setReadFlag;
	}
	
	/**
	 * function:删除
	 * @return
	 */
	public boolean setDelMsg(int msg_id,int user_id,String table){
		boolean setDelFlag = false;
		int affectedRows = 0;
		String sql=new String();
		dbc = new DbConnection();
		sql = "select msg_count,msg_sender_del from msg where msg_id="+msg_id;
//System.out.println("setDelMsg 1 sql:"+sql);		
		int msg_count = 0;
		int msg_sender_del = 1;
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if(dbc.rs.next()){
				msg_count = dbc.rs.getInt("msg_count");
				msg_sender_del = dbc.rs.getInt("msg_sender_del");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if("msg".equals(table)){//delete from table msg
			if(msg_count>0){
				sql = "update msg set msg_sender_del=1 where msg_id="+msg_id;
			}else{
				sql = "delete from msg where msg_id="+msg_id;
			}
//System.out.println("setDelMsg 2 sql:"+sql);			
			try {
				affectedRows =dbc.st.executeUpdate(sql);
				if(affectedRows>0){
					setDelFlag = true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			// delete from table msg_rece
			// in table msg_rece_state=2 means the receiver delete the message
			sql = "update msg_rece set msg_rece_state=2 where msg_id="+msg_id+" and user_id="+user_id;
//System.out.println("setDelMsg 3 sql:"+sql);		
			try {
				affectedRows = dbc.st.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(affectedRows>0){
				msg_count--;
				if(msg_count<=0 && msg_sender_del>0){
					sql = "delete from msg where msg_id="+msg_id;
				}else{
					sql = "update msg set msg_count="+msg_count+" where msg_id ="+msg_id;
				}
//System.out.println("setDelMsg 4 sql:"+sql);				
				try {
					affectedRows = dbc.st.executeUpdate(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(affectedRows>0)
			setDelFlag = true;
		dbc.closeConnection();
		return setDelFlag;
	}
}
