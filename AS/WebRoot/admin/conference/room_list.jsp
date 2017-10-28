<%@ page language="java" import="java.util.*, com.as.dao.RoomDAO, com.as.vo.RoomVO, com.as.function.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>会议室信息修改</title>
 
	<script>
		function doEdit(id) {
			window.location.href = "./admin/conference/update_room.jsp?id=" + id;
		}
		
		
		function doDelete(id) {
			var extra = confirm("确认是否删除？");
			if (extra) {
				window.location.href = "./conference/RoomServlet?opflag=del&id=" + id;
			} 
		}
		
		function changepage(currentpage) {
			document.page_form.currentPage.value = currentpage;
			document.page_form.submit();
			return false;
		}
	</script>
  </head>
  
  <body>
  <%
		String add_flag = (String) request.getAttribute("succ");
		
		if (add_flag != null) {
			%>
				<script>
					alert("添加会议室成功！");
				</script>
			<%
		} 
		
		String action = (String) request.getAttribute("res");
		if (action == "upd_succ") {
			%>
				<script>
					alert("更新成功！");
				</script>
			<%
		} 
	 %>
  
<div class="thickbox_content">
<h1>会议室信息修改</h1>
<form>
<div align="center">
	<table border="1" width="98%">
	
		<tr><td colspan="10">会议室信息如下：</td>
		</tr>
		<tr align="center">
			<td rowspan="3">会议室名称</td>
			<td rowspan="3">容纳人数</td>
			<td colspan="6">主要设备</td>
			<td rowspan="3">备注</td>
			<td rowspan="3">操作</td>
		</tr>
		<tr align="center">
			<td colspan="2">投影仪</td>
	  		<td colspan="2">电视</td>
	  		<td colspan="2">电脑</td>
	  	</tr>
		<tr align="center">
		  <td>总数</td>
	  	  <td>损坏</td>
	      <td>总数</td>
	  	  <td>损坏</td>
	  	  <td>总数</td>
	  	  <td>损坏</td>
	  </tr>
	  <%
	  		
	  		PageHelp pagehelp = null;
	  		List list = null;
	  		pagehelp = (PageHelp) request.getAttribute("pagehelp");
	  		
	  		if (pagehelp == null) {
	  			int currentPage = 1;
	  			
	  			RoomDAO roomdao = new RoomDAO();
	  			pagehelp = roomdao.getRoom_PageHelp("", currentPage);
	  			
	  		}
	  		list = pagehelp.getObjectlist();
	  		Iterator it = list.iterator();
	  		
	  		while (it.hasNext()) {
	  			RoomVO roomvo = (RoomVO) it.next();
	  		%>
		<tr align="center">
			<td><%= roomvo.getRoom_name() %></td>
			<td><%= roomvo.getRoom_size() %></td>
			<td><%= roomvo.getRoom_projector() %></td>
			<td><%= roomvo.getPro_broken() %></td>
			<td><%= roomvo.getRoom_tv() %></td>
			<td><%= roomvo.getTv_broken() %></td>
			<td><%= roomvo.getRoom_pc() %></td>
			<td><%= roomvo.getPc_broken() %></td>
			<td><%= roomvo.getRoom_intro() %></td>
			<td><input type="button" onclick="doEdit(<%= roomvo.getRoom_id() %>)" value="编辑" />
				<input type="button" onclick="doDelete(<%= roomvo.getRoom_id() %>)" value="删除" /></td>
		</tr>
	  		<%
	  		
	  		}
	   %>
		
</div>
</form>

<form name="page_form" action="./conference/RoomServlet" method="post">
	<table>
		<input type="hidden" name="opflag" value="changePage" />
		<input type="hidden" name="currentPage" value="<%= pagehelp.getCurrentpage() %>" />
		<input type="hidden" name="cond" value="<%= pagehelp.getCondition() %>" />
		<tr>
			<td height=25 colspan="3"><%=pagehelp.getPagebar() %><br><br></td>
		</tr>
	</table>
</form>
</body>
</html>
