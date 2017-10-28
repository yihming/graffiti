<%@ page language="java" import="java.util.*, com.as.vo.MeetingVO, com.as.dao.RoomDAO, com.as.vo.RoomVO, com.as.function.PageHelp" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>会议室查询结果</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../css/base.css" rel="stylesheet" type="text/css" />
<script language="Javascript">
	function order(id) {
		var extra = confirm("确定预约吗？");
			if (extra) {
				window.location.href = "./conference/MeetingServlet?opflag=order&room_id=" + id;
			}
	}
	
	function backward() {
		window.history.back();
	}
</script>
  </head>
  
  <body>
<div class="thickbox_content">
<form action="./finish_conference.html" method="post">
	<table border="1" width="90%" cellpadding=" 0">
		<tr><td colspan="11"><span>符合您要求的会议室如下：</span></td>
		</tr>
		<tr bgcolor="#FFFDF0" align="center"><td rowspan="3"><span>会议室</span></td>
		<td rowspan="3"><span>大小</span></td>
		<td rowspan=3><span>地址</span></td>
		<td colspan="6"><span>固定设备</span></td>
		<td rowspan="3"><span>备注</span></td>
		<td rowspan="3"><span>操作</span></td>
		</tr>
		<tr bgcolor="#FFFDF0" align="center">
			<td colspan="2">投影仪</td>
	  		<td colspan="2">电视</td>
	  		<td colspan="2">电脑</td>
	  	</tr>
		<tr bgcolor="#FFFDF0" align="center">
		  <td>总数</td>
	  	  <td>损坏</td>
	      <td>总数</td>
	      <td>损坏</td>
	      <td>总数</td>
	      <td>损坏</td>
	  </tr>
	  <%
	  		
	  		List room_id_available = new ArrayList();
	  		room_id_available = (List) request.getSession().getAttribute("room_show");		// Get List of Integer Objects.
	  		
	  		PageHelp pagehelp_of_rooms = null;
	  		List list = null;
	  		pagehelp_of_rooms = (PageHelp) request.getSession().getAttribute("pagehelp_of_rooms");
	  		
	  		if (pagehelp_of_rooms == null) {
	  			int currentPage = 1;
	  			
	  			RoomDAO roomdao = new RoomDAO();
	  			pagehelp_of_rooms = roomdao.getRooms_Available(room_id_available, currentPage);
	  			
	  		}
	  		list = pagehelp_of_rooms.getObjectlist();
	  		Iterator it = list.iterator();
	  		
	  		while (it.hasNext()) {
	  			RoomVO roomvo = (RoomVO) it.next();
	  			
	  			%>
	  		<tr align="center">
	  			<td><%= roomvo.getRoom_name() %></td>
	  			<td><%= roomvo.getRoom_size() %></td>
	  			<td><%= roomvo.getRoom_addr() %></td>
	  			<td><%= roomvo.getRoom_projector() %></td>
	  			<td><%= roomvo.getPro_broken() %></td>
	  			<td><%= roomvo.getRoom_tv() %></td>
	  			<td><%= roomvo.getTv_broken() %></td>
	  			<td><%= roomvo.getRoom_pc() %></td>
	  			<td><%= roomvo.getPc_broken() %></td>
	  			<td><%= roomvo.getRoom_intro() %></td>
	  			<td><input type="button" value="预约" onclick="order(<%= roomvo.getRoom_id() %>)" /></td>
	  		</tr>
	  			<%
	  		}
	   %>
		
  </table><br />
  <div align="center"><input type="button" value="上一步" onclick="backward()" /></div>
</form>

<form name="page_form" action="./conference/MeetingServlet" method="post">
<table>
		<input type="hidden" name="opflag" value="changePage_of_rooms" />
		<input type="hidden" name="currentPage" value="<%= pagehelp_of_rooms.getCurrentpage() %>" />
		<tr>
			<td height=25 colspan="3"><%=pagehelp_of_rooms.getPagebar() %><br><br></td>
		</tr>
	</table>
</form>

</body>
</html>
