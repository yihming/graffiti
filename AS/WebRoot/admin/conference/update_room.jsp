<%@ page language="java" import="java.util.*, com.as.dao.RoomDAO, com.as.vo.RoomVO" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>修改会议室信息</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../../css/base.css" rel="stylesheet" type="text/css" />
<script lang="javascript">

	function check_res() {
	    var rpro = document.update_room_form.roomprojector.value;
	 	var pro_b = document.update_room_form.probroken.value;
	 	var rtv = document.update_room_form.roomtv.value;
	 	var tv_b = document.update_room_form.tvbroken.value;
	 	var rpc = document.update_room_form.roompc.value;
	 	var pc_b = document.update_room_form.pcbroken.value;
		
		if (parseInt(rpro) - parseInt(pro_b) < 0) alert("投影仪的损坏数不能超过其总数！");
		else if (parseInt(rtv) - parseInt(tv_b) < 0) alert("电视的损坏数不能超过其总数！");
			 else if (parseInt(rpc) - parseInt(pc_b) < 0) alert("电脑的损坏数不能超过其总数！");
			 	  else {
			 	  	document.update_room_form.submit();
			 	  }
	}
	
	function back_forward() {
		window.history.back();
	}
</script>

  </head>
  
  <body>
  <%
  		int rid = Integer.parseInt(request.getParameter("id"));
  		
  		RoomDAO roomdao = new RoomDAO();
  		RoomVO room = roomdao.getRoomById(rid);
  		
  		
   %>
   <div class="thickbox_content">
   <h1>修改会议室信息</h1>
<form name="update_room_form" action="./conference/RoomServlet" method="post">
<input type="hidden" name="opflag" value="upd" />
<input type="hidden" name="id" value="<%= rid %>" />
	<table border="1" widtd="90%">
		<tr>
			<td width="33%">会议室名称</td>
			<td width="67%" colspan=2><input type="text" name="roomname" value="<%= room.getRoom_name() %>" /></td>
		</tr>
		<tr>
			<td width="33%">地址</td>
			<td width="67%" colspan=2><input type="text" name="roomaddr" value="<%= room.getRoom_addr() %>" /></td>
		</tr>
		<tr>
			<td>最多容纳人数</td>
			<td colspan=2><input type="text" name="roomsize" value="<%= room.getRoom_size() %>" /></td>
		</tr>
		<tr>
			<td colspan=3><b>设备信息</b></td> 
		</tr>
		<tr>
			<td>投影仪：</td>
			<td align="center">总数<input type="text" size=4 name="roomprojector" value="<%= room.getRoom_projector() %>" />&nbsp;台</td>
			<td align="center">损坏<input type="text" size=4 name="probroken" value="<%= room.getPro_broken() %>" />&nbsp;台</td>
		</tr>
		<tr>
			<td>电视：</td>
			<td align="center">总数<input type="text" size=4 name="roomtv" value="<%= room.getRoom_tv() %>" />&nbsp;台</td>
			<td align="center">损坏<input type="text" size=4 name="tvbroken" value="<%= room.getTv_broken() %>" />&nbsp;台</td>
		</tr>
		<tr>
			<td>电脑：</td>
			<td align="center">总数<input type="text" size=4 name="roompc" value="<%= room.getRoom_pc() %>" />&nbsp;台</td>
			<td align="center">损坏<input type="text" size=4 name="pcbroken" value="<%= room.getPc_broken() %>" />&nbsp;台</td>
		</tr>
		<tr><td colspan=2><b>备注信息</b></td></tr>
		<tr><td><input type="text" name="roomintro" value="<%= room.getRoom_intro() %>" /></td></tr>
	</table>
	<input type="button" value="更改" onclick="check_res()" /><input type="button" value="取消" onclick="back_forward()" />
</form>
</div> 
  </body>
</html>
