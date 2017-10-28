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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script language="javascript">
    function class_del(class_id){
       if(confirm("这将删除该分类中的所有好友，确认删除？")){
       	if(confirm("真的吗？"))
         window.location.href="../friendoperation?operation=deleteclass&f_class_id="+class_id;
      }
     }
</script>
<title>无标题文档</title>
</head>
<%
String result = request.getParameter("result");
if(result!=null){
	String outmessage = "欢迎使用AS办公系统！";
	if(result.equals("update_ok")){
		outmessage = "好友分类更新成功！";
	}else if(result.equals("update_failed")){
		outmessage = "好友分类失败！";
	}else if(result.equals("delete_ok")){
		outmessage = "删除成功！";	
	}else if(result.equals("delete_failed")){
		outmessage = "删除失败！";
	}else if(result.equals("add_ok")){
		outmessage = "好友分类添加成功!";
	}else if(result.equals("add_failed")){
		outmessage = "好友分类添加失败!";
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
<h1>分类管理</h1>
<table width="98%"  border="1" cellpadding="1" cellspacing="0" align="center">
  <tr align="center">
    <td width="50%">姓名</td>
	<td width="50%">操作</td>
  </tr>
<%
Iterator it = f_class_list.iterator();
int id=0;
while(it.hasNext()){
	friendclassvo =(FriendClassVO)it.next();
 %>
<form name="classform" action="../friendoperation?operation=updateclass"  method="post">
  <tr align="center">
    <td><input name="f_class_name" value="<%=friendclassvo.getF_class_name()%>"></input></td>
	<td>
	<input type="hidden" name="f_class_name" id="f_class_name" />
	<input type="hidden" name="f_class_id" value="<%=friendclassvo.getF_class_id() %>"/>

	<input type="submit" name="action" value="修改" />
	<input type="button" name="action" value="删除" onClick="class_del(<%=friendclassvo.getF_class_id() %>)"/>
	</td>
	
  </tr>
</form>
  <%}

   %>

</table><br/>
   <form style="margin-left:10px" name="classform" action="../friendoperation?operation=addclass" method="post">
   <input type="hidden" name="user_id" value="<%=user_id %>"/>
   <input type="text" name="f_class_name" />
   <input type="submit" value="增加一个分类" />
   </form>
   </div>
</body>
</html>
