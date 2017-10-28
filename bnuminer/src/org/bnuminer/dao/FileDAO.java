package org.bnuminer.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bnuminer.DbConnection;
import org.bnuminer.vo.FileInfoVO;

public class FileDAO {
	private DbConnection dbc;
	
	/**
	 * 将上传文件之信息写入到数据库中
	 * @param sourceFile - 上传的文件
	 * @return boolean - true: 写入成功
	 *                   false: 写入失败
	 */
	public boolean setAddFile(FileInfoVO fileinfovo) {
		dbc = new DbConnection();
		PreparedStatement pst;
		boolean result = false;
		
		try {
			pst = dbc.conn.prepareStatement("INSERT INTO file_info (file_id, file_name, file_create_time, file_modify_time, file_size, file_type) VALUES(null, ?, ?, ?, ?, ?)");
			pst.setString(1, fileinfovo.getFile_name());
			pst.setString(2, fileinfovo.getFile_create_time());
			pst.setString(3, fileinfovo.getFile_modify_time());
			pst.setInt(4, fileinfovo.getFile_size());
			pst.setString(5, fileinfovo.getFile_type());
			
			if (pst.executeUpdate() != 0) { // 插入file_info表成功
				
				result = true;
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		
		
		return result;
		
	} // End of setAddFile().
	
	
	public FileInfoVO getSearchFileById(int file_id) {
		FileInfoVO result = new FileInfoVO();
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM file_info WHERE file_id = ?");
			pst.setInt(1, file_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				
				while (dbc.rs.next()) {
					result.setFile_id(file_id);
					result.setFile_name(dbc.rs.getString("file_name"));
					result.setFile_size(dbc.rs.getInt("file_size"));
					result.setFile_type(dbc.rs.getString("file_type"));
					result.setFile_create_time(dbc.rs.getString("file_create_time"));
					result.setFile_modify_time(dbc.rs.getString("file_modify_time"));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
		
	} // End of getSearchFileById().
	
	public int getSearchFileId(FileInfoVO fileinfovo) {
		dbc = new DbConnection();
		PreparedStatement pst;
		int file_id = -1;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM file_info WHERE file_name = ? AND file_create_time = ? AND file_modify_time = ? AND file_size = ? AND file_type = ?");
			pst.setString(1, fileinfovo.getFile_name());
			pst.setString(2, fileinfovo.getFile_create_time());
			pst.setString(3, fileinfovo.getFile_modify_time());
			pst.setInt(4, fileinfovo.getFile_size());
			pst.setString(5, fileinfovo.getFile_type());
			
			if (pst.execute()) { // 存在记录
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					file_id = dbc.rs.getInt("file_id");
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return file_id;
		
		
	}
	
	public List<FileInfoVO> getSearchEditableByUserId(int user_id) {
		List<FileInfoVO> result = new ArrayList<FileInfoVO>();
		
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT file_info.* FROM user_file_info, file_info WHERE user_file_info.file_id = file_info.file_id AND is_owner = 1 AND user_id = ? ORDER BY file_modify_time DESC");
			pst.setInt(1, user_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					FileInfoVO fileinfovo = new FileInfoVO();
					
					fileinfovo.setFile_id(dbc.rs.getInt("file_id"));
					fileinfovo.setFile_name(dbc.rs.getString("file_name"));
					fileinfovo.setFile_size(dbc.rs.getInt("file_size"));
					fileinfovo.setFile_type(dbc.rs.getString("file_type"));
					fileinfovo.setFile_create_time(dbc.rs.getString("file_create_time"));
					fileinfovo.setFile_modify_time(dbc.rs.getString("file_modify_time"));
					
					result.add(fileinfovo);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		
		return result;
	} // End of getSearchEditableByUserId().
	
	public boolean setDeleteFileById(int file_id) {
		boolean flag = false;
		
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("DELETE FROM file_info WHERE file_id = ? ");
			pst.setInt(1, file_id);
			
			if (pst.executeUpdate() != 0)
				flag = true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return flag;
		
	} // End of setDeleteFileById().
	

}
