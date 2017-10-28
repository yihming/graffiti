package org.bnuminer.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bnuminer.DbConnection;
import org.bnuminer.vo.UserInfoVO;

public class UserDAO {
	private DbConnection dbc;
	
	/**
	 * 插入新用户信息
	 * @param user_info
	 * @return boolean - true: 插入信息成功
	 * 					 false: 插入信息失败
	 */
	public boolean setAddUser(UserInfoVO user_info) {
		boolean result = false;
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("INSERT INTO user_info (user_id, user_name, user_pwd, user_true_name, user_email, user_pri, user_signup) VALUES(null, ?, MD5(?), ?, ?, ?, ?)");
			pst.setString(1, user_info.getUser_name());
			pst.setString(2, user_info.getUser_pwd());
			pst.setString(3, user_info.getUser_true_name());
			pst.setString(4, user_info.getUser_email());
			pst.setString(5, user_info.getUser_pri());
			pst.setString(6, user_info.getUser_signup());
			
			if (pst.executeUpdate() != 0) {
				result = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		
		
		return result;
		
	} // End of setAddUser().
	
	/**
	 * 根据用户名查找用户是否存在
	 * @param username 用户名
	 * @return 若存在，则为true；否则，为false
	 */
	public boolean checkUserByName(String username) {
		boolean result = false;
		
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_info WHERE user_name = ?");
			pst.setString(1, username);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				if (dbc.rs.next()) {
					result = true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	} // End of checkUserByName().
	
	/**
	 * 
	 * @return
	 */
	public List<UserInfoVO> getSearchAllUsers() {
		List<UserInfoVO> result = new ArrayList<UserInfoVO>();
		
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_info WHERE user_pri != 'admin' ORDER BY user_signup DESC");
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					UserInfoVO userinfovo = new UserInfoVO();
					userinfovo.setUser_id(dbc.rs.getInt("user_id"));
					userinfovo.setUser_name(dbc.rs.getString("user_name"));
					userinfovo.setUser_pwd(dbc.rs.getString("user_pwd"));
					userinfovo.setUser_true_name(dbc.rs.getString("user_true_name"));
					userinfovo.setUser_email(dbc.rs.getString("user_email"));
					userinfovo.setUser_pri(dbc.rs.getString("user_pri"));
					userinfovo.setUser_signup(dbc.rs.getString("user_signup"));
					
					result.add(userinfovo);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	} // End of getSearchAllUsers().
	
	public List<UserInfoVO> getUsersExceptSelf(int user_id) {
		List<UserInfoVO> result = new ArrayList<UserInfoVO>();
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_info WHERE user_id != ? AND user_pri != 'pending' ");
			pst.setInt(1, user_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while(dbc.rs.next()) {
					UserInfoVO userinfovo = new UserInfoVO();
					userinfovo.setUser_id(dbc.rs.getInt("user_id"));
					userinfovo.setUser_name(dbc.rs.getString("user_name"));
					userinfovo.setUser_true_name(dbc.rs.getString("user_true_name"));
					userinfovo.setUser_email(dbc.rs.getString("user_email"));
					userinfovo.setUser_pri(dbc.rs.getString("user_pri"));
					
					result.add(userinfovo);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	} // End of getUsersExceptSelf().
	
	public UserInfoVO getUserById(int user_id) {
		UserInfoVO result = new UserInfoVO();
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_info WHERE user_id = ? ");
			pst.setInt(1, user_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				if (dbc.rs.next()) {
					result.setUser_id(dbc.rs.getInt("user_id"));
					result.setUser_name(dbc.rs.getString("user_name"));
					result.setUser_true_name(dbc.rs.getString("user_true_name"));
					result.setUser_email(dbc.rs.getString("user_email"));
					result.setUser_pri(dbc.rs.getString("user_pri"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
		
	} // End of getUserById().
	
	public boolean approvalUserById(int user_id) {
		boolean flag = false;
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("UPDATE user_info SET user_pri = 'user' WHERE user_id = ? ");
			pst.setInt(1, user_id);
			
			if (pst.executeUpdate() != 0) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return flag;
		
	} // End of approvalUserById().
	
	public boolean setDeleteUserById(int user_id) {
		boolean flag = false;
		
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("DELETE FROM user_info WHERE user_id = ? ");
			pst.setInt(1, user_id);
			
			if (pst.executeUpdate() != 0) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return flag;
	} // End of setDeleteUserById().
}
