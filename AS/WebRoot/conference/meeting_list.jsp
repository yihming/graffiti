<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*,com.as.function.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>会议信息更改</title>
 

 
    <script>
    	function viewmeeting(id){
			window.location.href="/AS/conference/view_meeting.jsp?m_id="+id;
		}
		
		function del(id){		
			var b =confirm("确认要删除吗？");
			if(b){
				window.location.href="/AS/conference/MeetingServlet?opflag=del&m_id="+id;
			}else{
				alert("删除操作取消！");
			}
		}
		
		<!-- 翻页操作 -->
		  function changepage(currentpage){
				document.formpage.currentPage.value=currentpage;
				document.formpage.submit();
				return false;
		}
		
    </script> 
  </head>
  
  <body>
<form>
<table width="80%" cellpadding="0" align="center">
	<tr ><td colspan="2"><span>您所申请的会议信息如下：</span></td>
	</tr>
	<tr class="smallhead" align="center">
		<td width="10%"><span>会议名称</span></td>
		<td width="15%"><span>开始时间</span></td>
		<td width="15%"><span>结束时间</span></td>			
		<td width="20%"><span>会议室</span></td>
		<td width="10%"><span>人数</span></td>
		<td width="10%"><span>状态</span></td>		
		<td ><span>操作</span></td>
	</tr>
	<%
		PageHelp pagehelp=null;
		List list1 =null;
		int meeting_id=0;
		String m_state;
	
		UserSession usersession = (UserSession) request.getSession().getAttribute("usersession");
		int u_id = usersession.getUser_id();
			pagehelp=(PageHelp)request.getAttribute("pagehelp");
			
			if(pagehelp==null){
				int currentPage=1;
				MeetingDAO meetingdao = new MeetingDAO();
				pagehelp=meetingdao.queryMeeting("and ( m_id in (select t.m_id from m_user_rs t where t.user_id="+u_id+"))",currentPage);
			}
		list1=pagehelp.getObjectlist();
		
		
		if (list1 == null) {
			%>
				<tr>
					<td colspan=7 align="center">您目前没有会议</td>
				</tr>
			<%
		} else {
			Iterator it =list1.iterator();
		
			M_user_rsDAO m_user_rsdao=new M_user_rsDAO();
			
			while (it.hasNext()) {
				MeetingVO meetingvo = (MeetingVO) it.next();
				meeting_id = meetingvo.getM_id();
				if (meetingvo.getM_state() == 0) {
					m_state="等待审批";
				} else if (meetingvo.getM_state() == 1) {
						m_state = "审批通过";
					} else if (meetingvo.getM_state() == 2) {
							m_state = "被驳回";
						} else {
								m_state = "发生异常";
							}
					%>
						<tr  align="center">
		<td width="10%"><span ><%=meetingvo.getM_title() %></span></td>
		<td width="15%"><span ><%=meetingvo.getM_start().substring(0, 16) %></span></td>
		<td width="15%"><span ><%=meetingvo.getM_end().substring(0, 16) %></span></td>
		<td width="20%"><span ><%=meetingvo.getRoom_name() %></span></td>
		<td width="10%"><span ><%=meetingvo.getM_num() %></span></td>
		<td width="10%"><span ><%=m_state %></span></td>		
		<%
			if(m_user_rsdao.getIfsponsor(u_id,meeting_id)){				
		 %>
		<td><input type="button" value="查看" onclick="viewmeeting(<%=meetingvo.getM_id() %>)" /><input type="button" value="删除" onclick="del(<%=meetingvo.getM_id() %>)" /></td>
		<%
			}else{
		 %>
		<td><input type="button" value="查看" onclick="viewmeeting(<%=meetingvo.getM_id() %>)" /><input type="button" value="删除" disabled  /></td>		 
		 <%
		 	}
		  %>
	</tr>
					<%
			} // End of while.
		} // End of if.

			
	 %>
	
</table>
</form>

<form name="formpage" action="/AS/conference/MeetingServlet" method="post">
	<table width="80%" cellpadding="0" align="center" >			  
			<input type="hidden" name="opflag" value="changePage"/>
			<input type="hidden" name="currentPage" value="<%=pagehelp.getCurrentpage() %>"/> 
			<input type="hidden" name="condi" value="<%=pagehelp.getCondition()%>"/>
		<tr>
			<td height=25 cols="3"><%=pagehelp.getPagebar()%></td>
		</tr>
</table>
</form>
</body>
</html>
