<%@ page language="java" import="java.util.*,com.as.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.function.UserSession"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

UserSession usersession = (UserSession)session.getAttribute("usersession");
int user_id =usersession.getUser_id();


%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<title>代办事件</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>

	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
    <script type="text/javascript" src="../js/jqueryui/ui/ui.datepicker.js"></script>

	<script type="text/javascript">
	$(function() {
		$("#tabs").tabs();
		$("#datepicker1").datepicker();
	   $("#datepicker2").datepicker();
	});
	function agency_submit(){
	  alert("提交成功");
	  return false;
	  }
	  
	 function agency_cancel(){
	  if(confirm("确定取消代办？")){
           alert("您的代办已经取消");
	   }
	   return false;
	 }
	</script>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'agency.jsp' starting page</title>
 

  </head>
  
  <body>
   <div class="thickbox_content">
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">申请代理</a></li>
		<li><a href="#tabs-2">取消代理</a></li>
		<li><a href="#tabs-3">查看历史代理</a></li>
	</ul>
  <div id="tabs-1">
     <form action="#" method="post">
		<table width="98%" border="0" style="border:0px;">
		  <tr>
			<td width="14%">选择代办人</td>
			<td width="86%"><label>
			  <select name="select">
				<option value="1" selected>谭朝华</option>
				<option value="2">何跃</option>
				<option value="3">郭凯</option>
				<option value="3">王鹏亮</option>
				<option value="3">布力布力</option>
				<option value="3">杨逸明</option>
				<option value="3">王俊杰</option>
				<option value="3">刘丽娟</option>
			  </select>
			</label></td>
		  </tr>
		  <tr>
			<td colspan="2"><table width="98%" border="0">
			  <tr class="smallhead">
				<td>事情</td>
				<td>权限</td>
			  </tr>
			  <tr>
				<td>站内短信</td>
				<td>
				  <input type="checkbox" name="checkbox1" value="checkbox[]" />
				查看
				<input type="checkbox" name="checkbox2" value="checkbox[]" />
				回复</td>
			  </tr>
			  <tr>
				<td>会议申请</td>
				<td>
				  <input type="checkbox" name="checkbox3" value="checkbox[]" />
				提交
				<input type="checkbox" name="checkbox4" value="checkbox[]"/>
				删除
				<input type="checkbox" name="checkbox5" value="checkbox[]" />
				查看
				
				<input type="checkbox" name="checkbox6" value="checkbox[]" />
				更新</td>
			  </tr>
			  <tr>
				<td>网络硬盘管理</td>
				<td>
				  <input type="checkbox" name="checkbox7" value="checkbox[]" />
				查看
				<input type="checkbox" name="checkbox8" value="checkbox[]" />
				上传
				<input type="checkbox" name="checkbox9" value="checkbox[]" />
				下载				</td>
			  </tr>
			  <tr>
				<td>授权时间</td>
				<td> 
				  <input name="textfield"  id="datepicker1" type="text">
				到
				<input name="textfield2"  id="datepicker2" type="text">
				</td>
			  </tr>
			  <tr>
			    <td>&nbsp;</td>
			    <td><input type="button" name="Submit" value=" " class="sure_submit" onclick="return agency_submit()"/></td>
			    </tr>
			</table></td>
			</tr>
		</table>
		</form>
   </div>
	<div id="tabs-2">
	    <h2>目前谭朝华在给你代办，你可以取消</h2>
		<input  type="button" value="取消代办" onclick="return agency_cancel()"/>
	</div>
	<div id="tabs-3">
	  <table border="1" width="98%">
	<tr class="smallhead">
	  <td>代理人</td>
	  <td>代理时间</td>
	  <td>给以权限</td>
	</tr>
	<tr><td>&nbsp;</td>
	  <td>&nbsp;</td>
	  <td>&nbsp;</td>
	</tr>
    <tr><td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
	</table>
	</div>
</div>
</div>
 
  </body>
</html>
