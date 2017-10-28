<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	// Version 1.2: Add the operation to transmit parameters in the form to RoomServlet.(2009-8-27 By Bauer Yung)
	// Version 1.3: Alter the action path of the form. (2009-8-27 13:51 Bauer Yung)
	// Version 1.4: Implement the insert room function. (2009-8-27 15:55 Bauer Yung)
	// Version 1.5: Add add_room_check() function. (2009-8-27 19:24 Bauer Yung)
 %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>添加会议室</title>
 
<script lang="javascript">
	function add_room_check(roomname, roomaddr, roomsize, roomprojector, roomtv, roompc) {
		if (roomname.value == '') alert("请输入会议室名称！");
		else if (roomaddr.value == '') alert("请输入会议室地址！");
		else if (roomsize.value == '') alert("请输入最多容纳人数！");
		else if (roomprojector.value == '') alert("请输入投影仪数目！");
		else if (roomtv.value == '') alert("请输入电视数目！");
		else if (roompc.value == '') alert("请输入电脑数目！");
				else {
						document.add_room_form.submit();
				}
	}
</script>

  </head>
  
  <body>
  <%
  		String flag = (String) request.getAttribute("fail");
  		
  		if (flag != null) {
  			%>
  				<script>
  					alert("会议室名称已被使用！请更换！");
  				</script>
  			
  			<%
  		}
  		  
%>

<div class="thickbox_content">
<h1>添加会议室</h1>
<form name="add_room_form" action="./conference/RoomServlet" method="post">
<input type="hidden" name="opflag" value="add" />
	<table border="1" widtd="90%">
	  <tr class="smallhead">
			<td colspan=2><b>会议室信息</b></td> 
		</tr>
		<tr>
			<td width="33%">会议室名称</td>
			<td width="67%"><input type="text" name="roomname" value="" /></td>
		</tr>
		<tr>
			<td width="33%">地址</td>
			<td width="67%"><input type="text" name="roomaddr" value="" /></td>
		</tr>
		<tr>
			<td>最多容纳人数</td>
			<td><input type="text" name="roomsize" value="输入数字即可" onfocus="this.value=''" /></td>
		</tr>
		<tr class="smallhead">
			<td colspan=2><b>设备信息</b></td> 
		</tr>
		<tr>
			<td>投影仪：</td><td><input type="text" size=4 name="roomprojector" value="" />&nbsp;台</td>
		</tr>
		<tr>
			<td>电视：</td><td><input type="text" size=4 name="roomtv" value="" />&nbsp;台</td>
		</tr>
		<tr>
			<td>电脑：</td><td><input type="text" size=4 name="roompc" value="" />&nbsp;台</td>
		</tr>
		<tr class="smallhead"><td colspan=2><b>备注信息</b></td></tr>
		<tr><td><input type="text" name="roomintro" value="在此输入备注信息" onfocus="this.value=''" /></td></tr>
	</table>
	<input type="button" value="添加" onclick="add_room_check(roomname, roomaddr, roomsize, roomprojector, roomtv, roompc)" /><input type="reset" value="重填" />
</form>
</div>

</body>
</html>
