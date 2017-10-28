<%@ page language="java" import="java.util.*, com.as.dao.MeetingDAO, com.as.vo.MeetingVO, com.as.dao.RoomDAO" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>会议室申请审批</title>
  
  </head>
  
  <body>
<div class="thickbox_content">
<form>
	<table border="1" width="98%" >
		<tr class="smallhead"><td colspan=7>&gt;待审会议室申请信息如下：</td>
		</tr>
		<tr align="center">
			<td>会议名称</td>
			<td>开始时间</td>
			<td>结束时间</td>
			<td>会议室</td>
			<td>人数</td>
			<td>详细信息</td>
			<td>审批</td>
		</tr>
		<%
			List waitingmeeting = new ArrayList();
			MeetingDAO meetingdao = new MeetingDAO();
			
			waitingmeeting = meetingdao.getAllWaitingMeeting();
			if (waitingmeeting != null) {
				Iterator it = waitingmeeting.iterator();
				while (it.hasNext()) {
					MeetingVO meeting = new MeetingVO();
					meeting = (MeetingVO) it.next();
					%>
						<tr align="center">
							<td><%= meeting.getM_title() %></td>
							<td><%= meeting.getM_start().substring(0, 16) %></td>
							<td><%= meeting.getM_end().substring(0, 16) %></td>
							<td><%= meeting.getRoom_name() %></td>
							<td><%= meeting.getM_num() %></td>
							<td><a href="/AS/conference/view_meeting.jsp?m_id=<%= meeting.getM_id() %>">查看详情</a></td>
							<td>
								<input type="button" value="批准" onclick="ratified(<%= meeting.getM_id() %>)" />
								<input type="button" value="驳回" onclick="refuse(<%= meeting.getM_id() %>)" />
							</td>
						</tr>
					<%
				}
				
			} else {
				%>
					<tr>
						<td colspan=7 align="center">目前暂无待审会议记录</td>
					</tr>
				<%
			}
		 %>
		
		
	</table>
</form>
</div>
<script>
	function ratified(id) {
		var judge = confirm("确定批准吗？");
		if (judge) {
			window.location.href = "/AS/conference/MeetingServlet?opflag=pass&m_id=" + id;
		}
	}
	
	function refuse(id) {
		var extra = confirm("确定驳回吗？");
		if (extra) {
			window.location.href = "/AS/conference/MeetingServlet?opflag=refused&m_id=" + id;
		}
	}
</script>
</body>
</html>
