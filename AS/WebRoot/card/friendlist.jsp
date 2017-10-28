<!--
version 1.2
基本框架搞定
Guo Kai 2009-8-31
-->
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" import="com.as.function.UserSession" %>
<%@ page language="java" import="com.as.dao.FriendDAO" %>
<%@ page language="java" import="com.as.vo.FriendClassVO" %>
<%@ page language="java" import="com.as.vo.FriendVO" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
UserSession usersession = new UserSession();
usersession = (UserSession)session.getAttribute("usersession");
int user_id = usersession.getUser_id();
String user_name = usersession.getUser_name();

FriendDAO frienddao = new FriendDAO();
FriendClassVO friendclassvo = new FriendClassVO();
List f_class_list = new ArrayList();
f_class_list = frienddao.getFriendClassByUserId(user_id);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<link type="text/css" href="../css/base.css" rel="stylesheet"/>
	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <script type="text/javascript" src="../js/jqueryui/ui/ui.accordion.js"></script>
  
  	<script type="text/javascript">
	$(function() {
		$("#accordion").accordion({
			icons: {
    			header: "ui-icon-circle-arrow-e",
   				headerSelected: "ui-icon-circle-arrow-s"
			}
		});
	});
	</script>
	
    <base href="<%=basePath%>">
    
    <title>My JSP 'friendlist.jsp' starting page</title>
    
	 

  </head>
  <%
	String result = request.getParameter("result");
	if(result!=null){
		String outmessage = "欢迎您使用AS办公系统！";
		if(result.equals("delete_ok")){
			outmessage = "好友删除成功!";
		}else if(result.equals("delete_failed")){
			outmessage = "好友删除失败!";
		}else if(result.equals("update_ok")){
			outmessage = "好友更新成功！";
		}else if(result.equals("update_failed")){
			outmessage = "好友更新失败!";
		}else if(result.equals("add_ok")){
			outmessage = "好友添加成功!";
		}else if(result.equals("add_failed")){
			outmessage = "好友添加失败!";
		}
     %>
     <script language="javascript">
     alert("<%=outmessage%>");
     </script>
     <%
	}	
 %>
  <body>
  <div class="thickbox_content">
  <h1>我的好友</h1>
  <div id="demo">
  <div name="friend_class" id="accordion">
	<% 
		List friendlist = new ArrayList(); 
		Iterator it = f_class_list.iterator(); 
		Iterator it2; 
		FriendVO friendvo; 
		while(it.hasNext()){ 
			friendclassvo = (FriendClassVO) it.next(); 
			friendlist = frienddao.getFriendByClassId(friendclassvo.getF_class_id()); 
			it2 = friendlist.iterator(); 
		%>
		
			
			<h3><a href="#"><%=friendclassvo.getF_class_name()%></a></h3>
			<div name="friend_list">	
				<ul>
					<%
					while(it2.hasNext()){
						friendvo = (FriendVO)it2.next();
						%>
						<li>
						<a href='card/friend.jsp?friend_id=<%=friendvo.getFriend_id() %>'><%=friendvo.getFriend_name() %></a>
						<br></li>
						<%
					}
					 %>
				</ul>
			</div>
		
			<%
		}
%>
</div>
</div>
  </body>
</html>
