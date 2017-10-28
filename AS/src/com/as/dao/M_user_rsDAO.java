package com.as.dao;
/***********************************************************************
 * Version 1.3: Add setAddRecords(). (2009-8-31 15:05 Bauer Yung)
 * Version 1.4: Add setDelRecords(). (2009-8-31 16:35 Bauer Yung)
 * Version 1.8: Add getPartner_id(). (2009-8-31 23:47 Bauer Yung)
 **********************************************************************/
import com.as.db.DbConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.as.vo.*;

public class M_user_rsDAO {
	private DbConnection dbc;
	public boolean setAddM_user_rs(M_user_rsVO m_user_rsvo){
		dbc=new DbConnection();
		PreparedStatement pst;
		String sql="insert into m_user_rs (m_user_rs_id,m_id,user_id,applier_state) values(null,?,?,?)";
		try{
			pst=dbc.conn.prepareStatement(sql);
			pst.setInt(1,m_user_rsvo.getM_id());
			pst.setInt(2, m_user_rsvo.getUser_id());
			pst.setInt(3, m_user_rsvo.getApplier_state());
			pst.executeQuery();
		}catch(SQLException e1){
			e1.printStackTrace();
		}
		return false;
	}
	
	public List getPartner_id(int meeting_id){
		dbc = new DbConnection();
		List partner_ids = new ArrayList();
		
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("SELECT user_id FROM m_user_rs WHERE m_id = ? AND applier_state = 0 ");
			pst.setInt(1, meeting_id);
			
			dbc.rs = pst.executeQuery();
			
			while (dbc.rs.next()) {
				Integer uid = new Integer(dbc.rs.getInt("user_id"));
				partner_ids.add(uid);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return partner_ids;
	} // End of getPartner_id().
	
	public List getPartner_name(int meeting_id){
		dbc=new DbConnection();
		PreparedStatement pst;
		List result = new ArrayList();
		try {
			pst=dbc.conn.prepareStatement("select t.user_name as uname from userinfo t where t.user_id in (select user_id from m_user_rs where m_id=?)");
			pst.setInt(1, meeting_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				
				while (dbc.rs.next()) {
					String user_name = dbc.rs.getString("uname");
					result.add(user_name);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}	
	
	public boolean getIfsponsor(int user_id,int meeting_id)
	{
		dbc=new DbConnection();
		PreparedStatement pst;
		try{
			pst=dbc.conn.prepareStatement("select * from m_user_rs where user_id=? and m_id= ? and applier_state=1");
			pst.setInt(1, user_id);
			pst.setInt(2, meeting_id);
			if(pst.execute()){
				dbc.rs = pst.executeQuery();
				if(dbc.rs.next()){
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;		
	}
	
	public boolean setAddRecords(int u_id, List users, int m_id) {
		dbc = new DbConnection();
		boolean flag = false;
		
		// Add the sponser's information.
		M_user_rsVO m_user_rs = new M_user_rsVO();
		m_user_rs.setApplier_state(1);
		m_user_rs.setM_id(m_id);
		m_user_rs.setUser_id(u_id);
		
		// Add it into m_user_rs table.
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO m_user_rs(m_user_rs_id, m_id, user_id, applier_state) VALUES(null, ")
		   .append(m_user_rs.getM_id())
		   .append(", ")
		   .append(m_user_rs.getUser_id())
		   .append(", ")
		   .append(m_user_rs.getApplier_state())
		   .append(")");
		
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement(sql.toString());
			int i = pst.executeUpdate();
			
			if (i != 0) {
				int count = 0;
				if (users != null) { // If m_user_rsVO List is not null, insert each element into m_user_rs table.
					Iterator it = users.iterator();
					while (it.hasNext()) {
						UserVO uservo = (UserVO) it.next();
						m_user_rs = new M_user_rsVO();
					
						m_user_rs.setApplier_state(0);
						m_user_rs.setM_id(m_id);
						m_user_rs.setUser_id(uservo.getUser_id());
					
						sql = new StringBuffer();
						sql.append("INSERT INTO m_user_rs(m_user_rs_id, m_id, user_id, applier_state) VALUES(null, ")
						   .append(m_user_rs.getM_id())
						   .append(", ")
						   .append(m_user_rs.getUser_id())
						   .append(", ")
						   .append(m_user_rs.getApplier_state())
						   .append(")");
						pst = dbc.conn.prepareStatement(sql.toString());
						int j = pst.executeUpdate();
					
						if (j != 0) {
							++count;
						}
					}
					
					if (count == users.size()) {
						flag = true;
					}
				} else {
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
		
	} // End of setAddRecords().
	
	public boolean setDelRecords(int m_id) {
		dbc = new DbConnection();
		boolean flag = false;
		
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("DELETE FROM m_user_rs WHERE m_id = ? ");
			pst.setInt(1, m_id);
			
			int lines = pst.executeUpdate();
			
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
		
	} // End of setDelRecords().
	
	public int getSponser_ID(int m_id) {
		dbc = new DbConnection();
		int sponser_id = -1;
		
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT user_id FROM m_user_rs WHERE m_id = ? AND applier_state = 1 ");
			pst.setInt(1, m_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				
				while (dbc.rs.next()) {
					sponser_id = dbc.rs.getInt("user_id");
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
		
		return sponser_id;
		
	} // End of getSponser_ID().
}
