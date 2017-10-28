<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'mytask.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  
  <%      int user_id = 42;
	String user_true_name = "";
	//	HttpSession session = request.getSession();
	//UserSession usersession = (UserSession)session.getAttribute("usersession");
	//if(usersession.getUser_id()!=0){
		//	user_id = usersession.getUser_id();
		// user_true_name = usersession.getUser_true_name();
	//}
  %>
 欢迎光临AlphaSphere的用户：<%=user_true_name %>
  <br>
  
<form action="./task/Taskservlet " method="post" name="listform">
<table align="center" border="2" width="80%">
<tr>
<td>标题</td>
<td>发布人</td>
<td>任务内容</td>
<td>开始时间</td>
<td>结束时间</td>
</tr>
		<%
			TaskDAO taskdao = new TaskDAO() ;	
			TaskVO taskvo= new TaskVO();		
			List tasklist =new ArrayList();
			tasklist=taskdao.getNeedMedo(user_id);

			UserDAO userdao=new UserDAO();

			Iterator it=tasklist.iterator();
			while(it.hasNext()){
			taskvo=(TaskVO)it.next();
		 %>
<tr>
<td><a href="./task/readTask.jsp?task_id=<%=taskvo.getTask_id()%>"><%=taskvo.getTask_title() %></td>
<td><%=userdao.getUserTrueNameById(taskvo.getUser_id()) %></td>
<td><%=taskvo.getTask_intro() %></td>
<td><%=taskvo.getTask_start() %></td>
<td><%=taskvo.getTask_end() %></td>
</tr>
<%
  }
 %>
</table>

</form>

  </body>
</html>
