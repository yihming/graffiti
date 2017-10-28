package org.bnuminer.test;

/**
 *  测试数据库访问
 */
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bnuminer.DbConnection;
import org.bnuminer.dao.UserDAO;

public class DbTest {
	
	
	public static void main(String[] args) {
		DbTest db = new DbTest();
		
		
		db.selectItem();
		
		
	}
	

	public void checkUsername(String username) {
		UserDAO userdao = new UserDAO();
		if (userdao.checkUserByName(username))
			System.out.println("用户名存在");
		else 
			System.out.println("用户名不存在");
	}
	
	public void selectItem() {
			DbConnection dbc = new DbConnection();
			PreparedStatement pst;
			try {
				pst = dbc.conn.prepareStatement("SELECT * FROM configs");
				
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					System.out.println(dbc.rs.getString("item") + "  " + dbc.rs.getString("content"));
				} 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
	}
	
	public void insertItem(){
		DbConnection dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("INSERT INTO user_info (user_id, user_name, user_pwd, user_true_name, user_email, user_pri) VALUES(null, ?, MD5(?), ?, ?, ?);");
			pst.setString(1, "lorryboy");
			pst.setString(2, "123456");
			pst.setString(3, "杨逸明");
			pst.setString(4, "bauerdelscu@gmail.com");
			pst.setString(5, "admin");
			
			pst.executeUpdate();
			System.out.println("OK！");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
	}
	
	public void updateItem() {
		DbConnection dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("UPDATE user_info SET user_true_name = ? WHERE user_name = ?");
			pst.setString(1, "测试用户");
			pst.setString(2, "test");
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
	}
}
