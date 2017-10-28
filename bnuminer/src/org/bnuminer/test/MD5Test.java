package org.bnuminer.test;

/**
 * 测试MD5()函数
 */
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bnuminer.DbConnection;
import org.bnuminer.Functions;

public class MD5Test {
	public static void main(String[] args) {
		DbConnection dbc = new DbConnection();
		String pwd = Functions.md5("123456");
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM user_info WHERE user_id = 1");
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();
				if (dbc.rs.next()) {
					if (dbc.rs.getString("user_pwd").equals(pwd)) {
						System.out.println("Password Correct!");
					} else
						System.out.println("Passwords Not Match!");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
	}
}
