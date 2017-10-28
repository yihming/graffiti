<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" import="com.as.function.UserSession" %>
<%@ page language="java" import="com.as.dao.FriendDAO" %>
<%@ page language="java" import="com.as.dao.UserDAO" %>
<%@ page language="java" import="com.as.dao.DeptDAO" %>
<%@ page language="java" import="com.as.vo.FriendClassVO" %>
<%@ page language="java" import="com.as.vo.FriendVO" %>
<%@ page language="java" import="com.as.vo.UserVO" %>
<%@ page language="java" import="com.as.vo.DeptVO" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
UserSession usersession = new UserSession();
usersession = (UserSession)session.getAttribute("usersession");
int user_id = usersession.getUser_id();
String user_name = usersession.getUser_name();

UserVO uservo = new UserVO();
UserDAO userdao = new UserDAO();
DeptVO deptvo = new DeptVO();
DeptDAO deptdao = new DeptDAO();
List deptlist = deptdao.getAllDeptList();
Iterator it = deptlist.iterator();

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
    
    <title>My JSP 'colleaguelist.jsp' starting page</title>
    
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
  <div class="thickbox_content">
  <h1>公司同事</h1>
  <div id="demo">
  <div name="dept" id="accordion">
			<%
			while(it.hasNext()){
				deptvo = (DeptVO)it.next();
				%>
				
				<h3><a href="#"><%=deptvo.getDept_name() %></a></h3>
			
				<div name="dept_colleague">
					<ul>
				<%
					List userlist = userdao.getUserListByDeptId(deptvo.getDept_id());
					Iterator userit = userlist.iterator();
					while(userit.hasNext()){
						uservo = (UserVO)userit.next();
				 %>				
					<li >
					<a href="card/detail_info.jsp?user_id=<%=uservo.getUser_id()%>&user_nam=<%=uservo.getUser_name() %>" target="body">
					<font color="#0000FF"><%=uservo.getUser_true_name() %>	</font>
					</a>
					</li>
						
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
	
	</div>
  </body>
</html>
