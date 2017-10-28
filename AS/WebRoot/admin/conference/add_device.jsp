<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<jsp:directive.page import="com.as.dao.*,com.as.vo.*"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>新增移动设备</title>
 
	<script>
	    	function backtolist(){
			window.location.href="/AS/admin/conference/device_list.jsp";
		}
		
			function submitadd_device(){
				if(document.add_device.equip_name.value==""){
					alert("名称不能为空");
					return ;
				}else if(document.add_device.equip_count.value==""){
					alert("数目不能为空");
					return ;
				}
				document.add_device.submit();
			}
	</script>
  </head>
  
  <body>
  
  		<%
			String str=(String)request.getAttribute("fail");
			if("fail".equals(str))
			{
		%>
		<script>
		 	alert("添加失败，可能存在该名称，请使用另一个名称，或者更新相应名称的设备!");		
		</script>
		<%
		 	}
		%>
<div class="thickbox_content">
<h1>新增移动设备</h1>

	 
<form name="add_device" action="./conference/DeviceServlet" method="post">&nbsp; 
	<input type="hidden" name="opflag" value="add" /><br />
	<table border="1" width="98%">
		<tr>
			<td>设备名称</td><td><input type="text" name="equip_name" /></td>
			
		</tr>
		<tr>
		<td>数目</td><td><input type="text" name="equip_count" /></td>
		</tr>
		
		<tr>
		<td valign="top">设备信息</td><td colspan=3><textarea name="equip_intro" cols="45" rows="10" ></textarea></td>
		</tr>
	</table>
	&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="添加" onclick="submitadd_device()" /><input type="button" value="返回" onclick="backtolist()"/>
</form>
</div>
</body>
</html>
