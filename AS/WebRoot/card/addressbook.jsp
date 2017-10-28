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

String result = request.getParameter("result");

if(result!=null)
{
	String outmessage = "";
	if(result.equals("new_friend_ok")){
		outmessage = "好友添加成功！";
	}else if(result.equals("new_friend_failed")){
		outmessage = "好友添加失败！";
	}
	
	
	%>
	<script language="javascript">
	alert("<%=outmessage%>");
	</script>
	<% 
}
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
<head>
	<title> </title>
	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>

	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
 
	<script type="text/javascript">
	$(function() {
		$("#tabs").tabs();
	});
	</script>
</head>
<script language="javascript">
function add_friend(){
if(document.getElementById("friend_name").value=="")
	alert("用户名不能为空");
	else if(document.getElementById("friend_mail").value==""){alert("电子邮件不能为空！");}
	else if(confirm("确认提交？")) {
	document.newfirendform.submit();
	}

}
</script>
<body>
<div class="thickbox_content">
<h1>名片夹管理</h1>

<div id="tabs">
	<ul>
		<li><a href="#tabs-1">公司所有联系人</a></li>
		<li><a href="#tabs-2">我的好友</a></li>	
		<li><a href="#tabs-3">添加新名片</a></li>
	</ul>
  <div id="tabs-1">
  <h1>公司联系人</h1>
     <img src="images/addressbook.jpg" />
  </div>
	<div id="tabs-2">
	<h1>我的好友</h1>
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
		<div>
			<div name="friend_class">
			<font color="#FF0000" font-size:16px;>	<%=friendclassvo.getF_class_name()%></font>
			</div>

			<div name="friend_list">
				<ul>
					<%
					while(it2.hasNext()){
						friendvo = (FriendVO)it2.next();
						%>
						<li>
						<%=friendvo.getFriend_name() %>
						</li>
						<%
					}
					 %>
				</ul>
			</div>
		</div>
			<%
		}
%>
	  
	</div>
	 <div id="tabs-3">
  <h1>新增联系人</h1><br>
<form action="../friendoperation?operation=addfriend" method="post" name="newfirendform">
  <table border="1" width="98%">
		  <tr>
			<td scope="col">姓名</td>
			<td scope="col"><label>
			  <input type="text" name="friend_name" id="friend_name">
			</label></td>
			<td>好友分组</td>
			<td><label>
			  <select name="friend_class" id="friend_class">
			  <%
			  it = f_class_list.iterator();
			  while(it.hasNext()){
			  friendclassvo = (FriendClassVO)it.next();
			   %>
            <option value = '<%=friendclassvo.getF_class_id() %>'><%=friendclassvo.getF_class_name() %></option>
            <%} %>
          </select>
			</label><a href = "#" onclick="javascript:alert('在这儿弹一个框框出来，新建分组！！');">新建分组</a></td>

		  </tr>
		  <tr>
			<td scope="row">生日</td>
			<td><label>
			  <input type="text" name="friend_birth">
			</label></td>
						<td scope="col">性别</td>
			<td scope="col"><label>
				<input type="radio" name="friend_sex" value="1" checked/>男
				<input type="radio" name="friend_sex" value="0"/>女
			</label></td>			
			
		  </tr>
		  <tr>
			<td scope="row">手机</td>
			<td><label>
			  <input type="text" name="friend_mobile">
			</label></td>
			<td>座机</td>
			<td><label>
			  <input type="text" name="friend_phone">
			</label></td>
		  </tr>
		  <tr>
			<td scope="row">传真</td>
			<td><label>
			  <input type="text" name="friend_fax">
			</label></td>
			<td>电子邮件</td>
			<td><label>
			  <input type="text" name="friend_mail" id="friend_mail">
			</label></td>
		  </tr>
		  <tr>
		    <td scope="row">通讯地址</td>
		    <td colspan="3"><label>
		      <input name="friend_addr" type="text" size="60">
		    </label></td>
	    </tr>
	      <tr>
		    <td scope="row">备注</td>
		    <td colspan="3"><label>
		      <textarea name="friend_intro" style="width:250px;height:150px; margin-left:10px;"></textarea>
		    </label></td>
	    </tr>
		  <tr>
		    <td colspan="4" scope="row"><label>
		      <input type="button" name="Submit" value="" class="sure_submit" onclick="add_friend()">
		    </label></td>
	    </tr>
		</table>
	   </form>
     </div>
</div>
</div>
 
</body>
</html>
