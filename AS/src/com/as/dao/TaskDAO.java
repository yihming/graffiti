package com.as.dao;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.as.db.DbConnection;
import com.as.function.*;
import com.as.vo.TaskVO;

public class TaskDAO {
	private DbConnection dbc;
	//public TaskDAO() {
	//	dbc = new DbConnection();
	//}

	private String tasker = "tasker";

	private String task = "task";

	/*
	 * 功能：找到该ID的任务题目，发布人，完成时间，发布时间；
	 *  参数：user_id:当前用户的ID； 
	 *  返回：返回list
	 */
	public List getMyAllTask(int user_id) throws SQLException {
		dbc = new DbConnection();
		List ta = new ArrayList();
		AS as = new AS();
		
		String sql ="SELECT task_id,task_intro,user_id,task_start,task_end,task_title FROM "
			+ task + "" + " WHERE user_id=" + user_id + "";
		/*
		 * String sql = "SELECT task_intro,user_id,task_start,task_end,task_title FROM "
				+ task + "" + " WHERE task_id in (select task_id from " + tasker
				+ " where user_id=" + user_id + ")";
		 * 
		 */
//System.out.println("getAllMyTask: "+sql);
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while (dbc.rs.next()) {
				TaskVO taskvo = new TaskVO();
				taskvo.setTask_id(dbc.rs.getShort("task_id"));
				taskvo.setTask_title(dbc.rs.getString("task_title"));
				taskvo.setTask_intro(as.Clob_To_String(dbc.rs
						.getClob("task_intro")));
				taskvo.setUser_id(dbc.rs.getInt("user_id"));
				taskvo.setTask_start(dbc.rs.getDate("task_start"));
				taskvo.setTask_end(dbc.rs.getDate("task_end"));
				
				ta.add(taskvo);
//System.out.println("Task_title:"+dbc.rs.getString("task_title"));
//System.out.println("Task_intro:"+as.Clob_To_String(dbc.rs.getClob("task_intro")));
//System.out.println("User_id:"+dbc.rs.getInt("user_id"));
//System.out.println("Task_start:"+dbc.rs.getDate("task_start"));
//System.out.println("Task_end:"+dbc.rs.getDate("task_end"));
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
//System.out.println("bulbul");
		dbc.closeConnection();
		return ta;

	}

	/*
	 * 功能：找到用户叫别人做的任务列表； 
	 * 参数：user_id:当前用户的ID； 
	 * 返回：返回list
	 */
	public List getNeedMedo(int user_id) {
		dbc = new DbConnection();
//System.out.println(user_id);		
		List tasklist = new ArrayList();
		

		String sql = "SELECT task_id,task_title, task_intro,user_id,task_start,task_end FROM task  where task_id in (select task_id from tasker where user_id="
				+ user_id + ")";
//System.out.println("findNeedMedo" + sql);
		AS as = new AS();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while (dbc.rs.next()) {
				TaskVO taskvo = new TaskVO();
				taskvo.setTask_id(dbc.rs.getShort("task_id"));
				taskvo.setTask_title(dbc.rs.getString("task_title"));
				taskvo.setTask_intro(as.Clob_To_String(dbc.rs
						.getClob("task_intro")));
				taskvo.setUser_id(dbc.rs.getInt("user_id"));
				taskvo.setTask_start(dbc.rs.getDate("task_start"));
				taskvo.setTask_end(dbc.rs.getDate("task_end"));
				tasklist.add(taskvo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbc.closeConnection();
		return tasklist;
	}

	/*
	 * 功能：添加一个新的任务
	 *  参数：String user_name,Date task_end,String task_title,Clob task_intro
	 *  返回：1
	 */
	public int setNewTask(int user_id,String task_start, String task_end,
		String task_title, String task_intro) {
		String sql = new String();
		int current_id=0;
		dbc = new DbConnection();
		
		//AS as = new AS();
		//String startDate = as.getsqlDateTime();
		
		 sql = "insert into task values( null ,"+user_id+",'" + task_title + "','" + task_intro+ "'," +
		 		"to_date('"+task_start+"','yyyy-mm-dd hh24-mi')," + "to_date('"+task_end+"','yyyy-mm-dd hh24-mi'))";
			 
//System.out.println("my sql : "+sql); 
		 int affectedRows=0;
		 try {
			affectedRows = dbc.st.executeUpdate(sql);
//System.out.println("affectedRows : "+affectedRows);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
		
	if(affectedRows>0){
		try {
			dbc.rs = dbc.st.executeQuery("select task_seq.currval as current_task_id from dual");
			while(dbc.rs.next()){
				current_id = dbc.rs.getInt("current_task_id");
			}
		}
			catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		     }
			
		}
		
	
		return current_id;
	}

	public int setTasker(int current_task_id,int temp_user_id){
		
		dbc = new DbConnection();
		String sql=new String();
	sql="insert into tasker values(null,"+current_task_id+","+temp_user_id+")";
System.out.println("tasker"+sql);		
		int temp_value=0;
		try {
			temp_value = dbc.st.executeUpdate(sql);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return 0;				
	}
	
	public boolean setDelTaskById(int task_id){
		boolean setDelFlag = false;
		String sql=new String();
		sql = "delete from tasker where task_id="+task_id;
		int affectedRows = 0;
		dbc = new DbConnection();
System.out.println("setDelTaskById sql 1:"+sql);		
		try {
			affectedRows = dbc.st.executeUpdate(sql);
			if(affectedRows>0){
				sql = "delete from task where task_id="+task_id;
System.out.println("setDelTaskById sql 1:"+sql);				
				affectedRows = dbc.st.executeUpdate(sql);
				if(affectedRows>0)
					setDelFlag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return setDelFlag;
	}
	
	public List getTaskByTaskId(int task_id){
		dbc = new DbConnection();
		List readtask = new ArrayList();
		String sql=new String();
		sql="select * from task where task_id="+task_id+ "";
		AS as = new AS();
		try {
			dbc.rs=dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				TaskVO taskvo = new TaskVO();
				taskvo.setTask_id(dbc.rs.getShort("task_id"));
				taskvo.setTask_title(dbc.rs.getString("task_title"));
				taskvo.setTask_intro(as.Clob_To_String(dbc.rs
						.getClob("task_intro")));
				taskvo.setUser_id(dbc.rs.getInt("user_id"));
				taskvo.setTask_start(dbc.rs.getDate("task_start"));
				taskvo.setTask_end(dbc.rs.getDate("task_end"));
				
				readtask.add(taskvo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return readtask;
	}
	
	/*public static void main(String[] args) {
		
		
		TaskDAO taskdao = new TaskDAO();
		TaskVO taskvo = new TaskVO();
		List list = new ArrayList();
		Iterator it = list.iterator();
		try {
			//list=taskdao.addNewTask(45, null, "testtitle", "swimmingandeting")
			list = taskdao.getMyAllTask(42);
			while(it.hasNext()){
				System.out.println("sdfasdfaf");
				taskvo = (TaskVO) it.next();
				System.out.println(taskvo.getTask_title());
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

}
