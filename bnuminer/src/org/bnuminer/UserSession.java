package org.bnuminer;

/**
 * <p>封装在HttpSession中的当前用户信息
 * @author John Yung
 *
 */
public class UserSession {
	private int user_id;
	private String user_name;
	private String user_true_name;
	private String user_pri;
	private String user_signup;
	
	// 获取用户真实姓名
	public String getUser_true_name() {
		if (user_true_name == null) {
			user_true_name = "";
		}
		return user_true_name;
	}
	
	// 设置用户真实姓名
	public void setUser_true_name(String user_true_name) {
		this.user_true_name = user_true_name;
	}
	
	// 获取用户ID号
	public int getUser_id() {
		return user_id;
	}
	
	// 设置用户ID号
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	// 获取用户名称
	public String getUser_name() {
		if (this.user_name == null) {
			user_name = "";
		}
		return user_name;
	}
	
	// 设置用户名称
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	// 获取用户权限
	public String getUser_pri() {
		return user_pri;
	}
	
	// 设置用户权限
	public void setUser_pri(String user_pri) {
		this.user_pri = user_pri;
	}
	
	// 获取用户注册时间
	public String getUser_signup() {
		return user_signup;
	}
	
	// 设置用户注册时间
	public void setUser_signup(String user_signup) {
		this.user_signup = user_signup;
	}
}
