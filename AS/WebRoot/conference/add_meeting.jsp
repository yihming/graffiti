<%@ page language="java" import="java.util.*, com.as.dao.DeviceDAO" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.vo.DeviceVO;"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  <link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>创建会议</title>
    
	<link type="text/css" href="./js/jqueryui/css/ui.all.css" rel="stylesheet" />
	
	<script type="text/javascript" src="./js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="./js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="./js/jqueryui/ui/ui.tabs.js"></script>
    <script type="text/javascript" src="./js/jqueryui/ui/ui.datepicker.js"></script>
	
<script type="text/javascript">
	$(function() {
		$("#date1").datepicker({dateFormat: 'yy-mm-dd'});
		$("#date2").datepicker({dateFormat: 'yy-mm-dd'});
	});
</script>
<script>
function check_info(mstart_date, mstart_hour, mstart_minute, mend_date, mend_hour, mend_minute, m_num) {
		if (mstart_hour == "" || mstart_minute == "") {
			alert("开始时间不完整！");
		} else if (mend_date == "" || mend_hour == "" || mend_minute == "") {
			alert("结束时间不完整！");
		} else if (m_num == "" || m_num == "填入数字") {
			alert("请填写与会人数！");
		} else {
			document.add_m_form.submit();
		}
	}
</script>
  </head>
  
  <body>
<div class="thickbox_content">
<h1>申请会议室</h1>
<form name="add_m_form" action="./conference/MeetingServlet" method="post">
<input type="hidden" name="opflag" value="add_first" />
	<table width="512" border="1" id="m_table">  
		<tr class="smallhead"><td colspan="3">请输入所要预约的会议条件</td></tr>
		<tr align="center">
			<td width="30%">开始时间</td>
			<td><input type="text" size=10 id="date1" name="mstart_date" value=""/></td>
			<td>
				<select name="mstart_hour">
					<option selected value="">请选择</option>
				<%
					int hour = 0;
					for (hour = 0; hour <= 23; ++hour) {
						%>
							<option value="<%= hour %>"><%= hour %></option>
						<%
					}
				 %>
				</select>
				时
				<select name="mstart_minute">
					<option selected value="">请选择</option>
				<%
					int minute = 0;
					for (minute = 0; minute <= 59; ++minute) {
						%>
							<option value="<%= minute %>"><%= minute %></option>
						<%
					}
					
				 %>
				</select>
				分
			</td>
		</tr>
		<tr align="center">
			<td width="30%">结束时间</td>
			<td><input type="text" size=10 id="date2" name="mend_date" /></td>
			<td>
				<select name="mend_hour">
					<option selected value="">请选择</option>
					<%
						for (hour = 0; hour <= 23; ++hour) {
							%>
								<option value="<%= hour %>"><%= hour %></option>
							<%
						}
					 %>
				</select>
				时
				<select name="mend_minute">
					<option selected value="">请选择</option>
					<%
						for (minute = 0; minute <= 59; ++minute) {
							%>
								<option value="<%= minute %>"><%= minute %></option>
						<%
						}
					 %>
				</select>
				分
			</td>
		</tr>
		<tr align="center"><td width="30%">与会人数</td><td colspan=2><input type="text" size=10 name="m_num" onfocus="this.value=''" value="填入数字" /></td></tr>
		<tr><td class="smallhead" colspan="3">设备需求</td></tr>
		<tr align="center"><td width="30%">投影仪</td><td colspan=2><input type="text" size=10 name="projector" />&nbsp;台</td></tr>
		<tr align="center"><td width="30%">电视</td><td colspan=2><input type="text" size=10 name="tv" />&nbsp;台</td></tr>
		<tr align="center"><td width="30%">电脑</td><td colspan=2><input type="text" size=10 name="pc"/>&nbsp;台</td></tr>
		<tr class="smallhead"><td colspan=3>移动设备</td></tr>
		<%
			DeviceDAO devicedao = new DeviceDAO();
			List list = new ArrayList();
			
			list = devicedao.getAllDevice();
			Iterator it = list.iterator();
			int count = 1;
			
			while (it.hasNext()) {
				DeviceVO device = new DeviceVO();
				
				device = (DeviceVO) it.next();
				%>
					<tr align="center">
						<td width="30%"><%= device.getDevice_name() %></td>
						<td colspan=2 align="center">
							<select name="device_<%= count %>_num">
								<option value="0">0</option>
								<%
									int device_count = device.getDevice_valid();
									int i;
									for (i = 1; i <= device_count; ++i) {
										%>
											<option value="<%= i %>"><%= i %></option>
										<%
									}
								 %>
							</select>
						</td>
					</tr>
				<%
				++count;
			}
		 %>
		
	</table>
	<input type="submit" name="submit_meeting" value="" class="sure_submit" onclick="check_info(mstart_date, mstart_hour, mstart_minute, mend_date, mend_hour, mend_minute, m_num)" /><br/>
   <img src="/AS/images/next_step.png" width="64" height="64" alt="进入下一步"/>
</form>
</div>

</body>
</html>
