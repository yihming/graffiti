package org.bnuminer.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bnuminer.DbConnection;
import org.bnuminer.vo.FileInfoVO;
import org.bnuminer.vo.User_file_infoVO;

public class User_file_infoDAO {
	private DbConnection dbc;
	
	public boolean setAddUser_file_info(User_file_infoVO user_file_infovo) {
		boolean result = false;
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("INSERT INTO user_file_info (user_file_info_id, user_id, file_id, is_owner) VALUES(null, ?, ?, ?)");
			pst.setInt(1, user_file_infovo.getUser_id());
			pst.setInt(2, user_file_infovo.getFile_id());
			pst.setInt(3, user_file_infovo.getIs_owner());
			
			if (pst.executeUpdate() != 0) { // 插入记录成功
				result = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
		
	} // End of setAddUser_file_info().
	
	/**
	 * 获取指定用户所能选择的文件列表
	 * @param user_id 用户ID
	 * @return 文件用户关联信息列表List<User_file_infoVO>
	 */
	public List<User_file_infoVO> getSearchItemsByUserId(int user_id) {
		dbc = new DbConnection();
		PreparedStatement pst;
		List<User_file_infoVO> result = new ArrayList<User_file_infoVO>();
		
		try {
			pst = dbc.conn.prepareStatement("SELECT file_info.*, user_file_info_id, is_owner FROM user_file_info, file_info WHERE user_file_info.file_id = file_info.file_id AND user_id = ? ORDER BY file_modify_time DESC");
			pst.setInt(1, user_id);
			
			if (pst.execute()) { // 存在记录
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					User_file_infoVO user_file_infovo = new User_file_infoVO();
					user_file_infovo.setFile_id(dbc.rs.getInt("file_id"));
					user_file_infovo.setUser_file_info_id(dbc.rs.getInt("user_file_info_id"));
					user_file_infovo.setUser_id(user_id);
					user_file_infovo.setIs_owner(dbc.rs.getInt("is_owner"));
					
					FileInfoVO fileinfovo = new FileInfoVO();
					fileinfovo.setFile_id(dbc.rs.getInt("file_id"));
					fileinfovo.setFile_name(dbc.rs.getString("file_name"));
					fileinfovo.setFile_create_time(dbc.rs.getString("file_create_time"));
					fileinfovo.setFile_modify_time(dbc.rs.getString("file_modify_time"));
					fileinfovo.setFile_size(dbc.rs.getInt("file_size"));
					fileinfovo.setFile_type(dbc.rs.getString("file_type"));
					
					//user_file_infovo.setFileinfovo(fileinfovo);
					
					result.add(user_file_infovo);
				} // End of while.
				
				
				
			} // End of if.
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		
		return result;
	} // End of getSearchItemsByUserId().
	
	public String getOwnerName(int file_id) {
		dbc = new DbConnection();
		PreparedStatement pst;
		String owner_name = null;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT user_info.* FROM user_info, user_file_info WHERE user_info.user_id = user_file_info.user_id AND is_owner = 1 AND file_id = ?");
			pst.setInt(1, file_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				if (dbc.rs.next()) {
					owner_name = dbc.rs.getString("user_name");
					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return owner_name;
		
	} // End of getOwnerName().
	
	public List<String> getSearchSharedUserNameByFileId(int file_id) {
		List<String> result = new ArrayList<String>();
		
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT user_info.* FROM user_info, user_file_info WHERE user_info.user_id = user_file_info.user_id AND file_id = ? AND is_owner = 0 ");
			pst.setInt(1, file_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					String user_name = dbc.rs.getString("user_name");
					
					result.add(user_name);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
		
	} // End of getSearchSharedUserNameByFileId().
	
	public List<Integer> getSearchSharedUserIdByFileId(int file_id) {
		List<Integer> result = new ArrayList<Integer>();
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_file_info WHERE file_id = ? AND is_owner = 0");
			pst.setInt(1, file_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					result.add(Integer.valueOf(dbc.rs.getInt("user_id")));
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	} // End of getSearchSharedUserByFileId().
	
	public boolean getSearchItemByIds(int user_id, int file_id) {
		boolean flag = false;
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_file_info WHERE user_id = ? AND file_id = ? ");
			pst.setInt(1, user_id);
			pst.setInt(2, file_id);
			
			dbc.rs = pst.executeQuery();
			if (dbc.rs.next()) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return flag;
	} // End of getSearchItemByIds().
	
	public List<User_file_infoVO> getSearchItemsByFileId(int file_id) {
		List<User_file_infoVO> result = new ArrayList<User_file_infoVO>();
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_file_info WHERE file_id = ? AND is_owner = 0 ");
			pst.setInt(1, file_id);
			
			dbc.rs = pst.executeQuery();
			while (dbc.rs.next()) {
				User_file_infoVO user_file_infovo = new User_file_infoVO();
				user_file_infovo.setUser_id(dbc.rs.getInt("user_id"));
				user_file_infovo.setUser_file_info_id(dbc.rs.getInt("user_file_info_id"));
				user_file_infovo.setFile_id(dbc.rs.getInt("file_id"));
				user_file_infovo.setIs_owner(dbc.rs.getInt("is_owner"));
				
				result.add(user_file_infovo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	} // End of getSearchItemsByFileId().
	
	public void setDeleteItemByIds(int user_id, int file_id) {
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("DELETE FROM user_file_info WHERE user_id = ? AND file_id = ? ");
			pst.setInt(1, user_id);
			pst.setInt(2, file_id);
			
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
	} // End of setDeleteItemByIds().
	
	public boolean checkOwner(int user_id, int file_id) {
		boolean flag = false;
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_file_info WHERE user_id = ? AND file_id = ? ");
			pst.setInt(1, user_id);
			pst.setInt(2, file_id);
			
			dbc.rs = pst.executeQuery();
			if (dbc.rs.next()) {
				int is_owner = dbc.rs.getInt("is_owner");
				if (is_owner == 1)
					flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return flag;
	} // End of checkOwner().
	
	public int getOwnerIdByFileId(int file_id) {
		int owner_id = -1;
		
		dbc = new DbConnection();
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_file_info WHERE file_id = ? AND is_owner = 1 ");
			pst.setInt(1, file_id);
			
			dbc.rs = pst.executeQuery();
			if (dbc.rs.next()) {
				owner_id = dbc.rs.getInt("user_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return owner_id;
	} // End of getOwnerIdByFileId().
	

}
