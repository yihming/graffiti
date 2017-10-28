<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'readTask.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  <%
  	int task_id=0;
  	if(request.getParameter("task_id")!=null){
  		task_id = Integer.parseInt(request.getParameter("task_id"));
  		
  	}
  	TaskDAO taskdao = new TaskDAO();
  	List readtask=new ArrayList();
  	readtask=taskdao.getTaskByTaskId(task_id);
  		TaskVO taskvo = new TaskVO();
  		UserDAO userdao=new UserDAO();
  	Iterator it =readtask.iterator();
			while(it.hasNext()){
				 taskvo=(TaskVO)it.next();
  %>
  <body>
     <table border="1px"> 
	 <tr><td>发起人</td><td><%=userdao.getUserTrueNameById(taskvo.getUser_id()) %></td></tr> 
	 <tr><td>开始时间</td><td><%=taskvo.getTask_start() %></td></tr>
	 <tr><td>结束时间</td><td><%=taskvo.getTask_end() %></td></tr>
	 <tr><td>任务标题</td><td><%=taskvo.getTask_title() %></td></tr> 
	 <tr><td>任务内容</td><td><%=taskvo.getTask_intro() %></td></tr>
	<%} %>
	</table> 

	
  </body>
</html>
