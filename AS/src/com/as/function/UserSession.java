package com.as.function;
/**
 * 
 *    session的用法 
 *  根据网上的判断，java的session最好用一个类实现
 * 本系统中，UserSession是用来保存session的，有user_id 和user_name两个
 *存取session的办法，在sevlet里面
 *       UserSession usersession = new UserSession();
  *      usersession.setUser_id(user_id);//user_id是变量
  *     usersession.setUser_name(user_name);//user_name 是变量
 *    HttpSession session = request.getSession(); //新建session
*	session.setAttribute("usersession", usersession); //把usersession变量存入session
*	
*获得session内容
* *    HttpSession session = request.getSession(); //新建session
*UserSession usersession = (UserSession)session.getAttribute("usersession");
*int user_id =usersession.getUser_id()

 */
public class UserSession {
	private int user_id;
	private int user_group;
	private String user_name;
	private String user_true_name;
	
 
	public String getUser_true_name() {
		if(user_true_name==null){
			user_true_name = "";
		}
		return user_true_name;
	}
	public void setUser_true_name(String user_true_name) {
		this.user_true_name = user_true_name;
	}
	public int getUser_id() {
 
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		if(this.user_name==null){
			user_name ="";
		}
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getUser_group() {
		return user_group;
	}
	public void setUser_group(int user_group) {
		this.user_group = user_group;
	}

}
