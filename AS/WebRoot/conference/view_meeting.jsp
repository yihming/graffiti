<%@ page language="java" import="java.util.*, com.as.dao.MeetingDAO, com.as.vo.MeetingVO, com.as.dao.M_device_rsDAO, com.as.vo.DeviceVO, com.as.vo.RoomVO, com.as.dao.RoomDAO, com.as.dao.M_user_rsDAO" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  <link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'update_meeting.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  <%
  		int m_id = Integer.parseInt((String)request.getParameter("m_id"));
  		MeetingDAO meetingdao = new MeetingDAO();
  		MeetingVO meeting = meetingdao.getMeetingById(m_id);
  		String state = new String();
  		
  		switch (meeting.getM_state()) {
  			case 0:	state = "等待审批";
  					break;
  			case 1:	state = "审批通过";
  					break;
  			case 2:	state = "未通过审批";
  					break;
			default:	state = "异常状态";
						break;
  		}
  		
  		RoomVO meetingroom = new RoomVO();
  		RoomDAO roomdao = new RoomDAO();
  		meetingroom = roomdao.getRoomByM_id(m_id);
  		
  		M_user_rsDAO m_user_rsdao = new M_user_rsDAO();
  		List partners = m_user_rsdao.getPartner_name(m_id);
  		
   %>
    <form>
    	<table border=1 width="40%">
    		<tr>
    			<td width="30%">会议名称</td><td><%= meeting.getM_title() %></td>
    		</tr>
    		<tr>
    			<td>开始时间</td><td><%= meeting.getM_start() %></td>
    		</tr>
    		<tr>
    			<td>结束时间</td><td><%= meeting.getM_end() %></td>
    		</tr>
    		<tr>
    			<td>与会人数</td><td>会议室</td>
    		</tr>
    		<tr>
    			<td><%= meeting.getM_num() %></td><td>中山纪念堂</td>
    		</tr>
    		<tr>
    			<td>会议室地址</td><td>广州越秀区</td>
    		</tr>
    		<tr>
    			<td>会议状态</td><td><%= state %></td>
    		</tr>
    		<tr>
    			<td colspan=2>会议内容</td>
    		</tr>
    		<tr>
    			<td colspan=2><%= meeting.getM_intro() %></td>
    		</tr>
    		<tr>
    			<td colspan=2>与会人员</td>
    		</tr>
    		<tr><td colspan=2>
    		<%
    			Iterator here = partners.iterator();
    			
    			while (here.hasNext()) {
    				String partner_name = (String) here.next();
    				
    				%>
    					<%= partner_name %>
    					
    				<%
    				
    			}
    		 %>
    		 </td>
    		</tr>
    		<tr>
    			<td colspan=2>设备信息</td>
    		</tr>
    		<tr>
    			<td>投影仪</td><td>总数&nbsp;<%= meetingroom.getRoom_projector() %>&nbsp;台，损坏&nbsp; <%= meetingroom.getPro_broken() %>&nbsp;台</td>
    		</tr>
    		<tr>
    			<td>电视</td><td>总数&nbsp;<%= meetingroom.getRoom_tv() %>&nbsp;台，损坏&nbsp;<%= meetingroom.getTv_broken() %>&nbsp;台</td>
    		</tr>
    		<tr>
    			<td>电脑</td><td>总数&nbsp;<%= meetingroom.getRoom_pc() %>&nbsp;台，损坏&nbsp;<%= meetingroom.getPc_broken() %>&nbsp;台</td>
    		</tr>
    	<%
    		M_device_rsDAO m_device_rsdao = new M_device_rsDAO();
  			List used_devices = m_device_rsdao.getUsed_deviceNameByM_id(m_id);	
  			Iterator it = used_devices.iterator();
  			
  			while (it.hasNext()) {
  				DeviceVO device = (DeviceVO) it.next();
  				
  				%>
  					<tr>
  						<td><%= device.getDevice_name() %></td>
  						<td>申请使用&nbsp;<%= device.getDevice_valid() %>&nbsp;台</td>
  					</tr>
  				<%
  			}
   		 %>
    		
    	</table>
    	<div><input type="button" value="返回" onclick="backward()" / ></div>
    </form>
    <script>
    	function backward() {
    		window.history.back();
    	}
    </script>
  </body>
</html>
