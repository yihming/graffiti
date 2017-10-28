<%@ page language="java" import="java.util.*,org.apache.*,com.as.dao.*,com.as.vo.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>更新设备</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../../css/base.css" rel="stylesheet" type="text/css">
		<script>
	    	function backtolist(){
			window.location.href="/AS/admin/conference/device_list.jsp";
		}
	</script>
  </head>
  
  <body>
  	<%   
  			String str=(String)request.getAttribute("success");
  			if("update_success".equals(str))
  			{ 
  	%>
  	<script>
  			alert("更新成功！");
  			window.location.href="/AS/admin/conference/device_list.jsp";
  	</script>
  	  	
  	<%
  			return;
  			}
  		int i=Integer.parseInt(request.getParameter("id"));
	       DeviceDAO devicedao=new DeviceDAO();
	       DeviceVO devicevo=devicedao.getDeviceById(i);
	%>

<div class="thickbox_content">
<h1>更新设备</h1>
<form name="update_device" action="./conference/DeviceServlet" method="post">&nbsp; 
	<input type="hidden" name="opflag" value="update" /><br />
	<input type="hidden" name="equip_id" value="<%=devicevo.getDevice_id()%>"/>
	<table>
		<tr>
			<td>设备名称</td><td><input type="text" name="equip_name" value="<%=devicevo.getDevice_name() %>"/></td>
		</tr>
		
		<tr>
			<td>数目</td><td><input type="text" name="equip_count" value="<%=devicevo.getDevice_count() %>"/></td>
			<td>损坏数目</td><td><input type="text" name="equip_damage" value="<%=devicevo.getDevice_damage() %>"></td>
		</tr>
		
		<tr>
		<td valign="top">设备信息</td><td colspan=3><textarea name="equip_intro" cols="45" rows="10" ><%=devicevo.getDevice_intro()%></textarea></td>
		</tr>
	</table>
	&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="更新" /><input type="button" value="返回" onclick="backtolist()"/>
</form>
</div>
</body>
</html>
