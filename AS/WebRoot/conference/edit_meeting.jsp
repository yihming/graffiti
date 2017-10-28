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
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css">
<!--
.style2 {font-size: 14px}
-->
</style>
<link href="css/base.css" rel="stylesheet" type="text/css" />
<link href="../css/base.css" rel="stylesheet" type="text/css" />
    <script>
    	function update(id){
			window.location.href="/AS/admin/conference/update_meeting.jsp?id="+id;
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
		<td width="10%"><span>开始时间</span></td>
		<td width="10%"><span>开始时间</span></td>			
		<td width="10%"><span>会议室</span></td>
		<td width="10%"><span>人数</span></td>
		<td width="20%"><span>参加人员</span></td>
		<td width="20%"><span>内容概述</span></td>
		<td width="10%"><span>操作</span></td>
	</tr>
	<%
		PageHelp pagehelp=null;
		List list1 =null;
		List list2 =null;
		int meeting_id=0;
			pagehelp=(PageHelp)request.getAttribute("pagehelp");
			
			if(pagehelp==null){
				int currentPage=1;
				MeetingDAO meetingdao = new MeetingDAO();
				pagehelp =meetingdao.queryMeeting("",currentPage);
			}
		list1=pagehelp.getObjectlist();
		Iterator it =list1.iterator();
		
		M_user_rsDAO m_user_rsdao=new M_user_rsDAO();

		while(it.hasNext()){
			MeetingVO meetingvo=(MeetingVO)it.next();
			meeting_id=meetingvo.getM_id();
			list2=m_user_rsdao.getPartner(meeting_id);
			Iterator it2=list2.iterator();				
	 %>
	<tr  align="center">
		<td width="10%"><span ><%=meetingvo.getM_title() %></span></td>
		<td width="10%"><span ><%=meetingvo.getM_start() %></span></td>
		<td width="10%"><span ><%=meetingvo.getM_start() %></span></td>
		<td width="10%"><span ><%=meetingvo.getRoom_name() %></span></td>
		<td width="10%"><span ><%=meetingvo.getM_num() %></span></td>
		<td width="20%"><span >
		<%
			while(it2.hasNext())
			{
				String name=(String)it2.next();
		%>
		<%=name %>		
		<%
			}			
		 %>
		</span></td>
		<td width="20%"><span ><%=meetingvo.getM_intro() %></span></td>
		<td width="10%"><input type="button" value="查看" onclick="viewmeeting(<%=meetingvo.getM_id() %>)" /><input type="button" value="删除" onclick="del(<%=meetingvo.getM_id() %>)" /></td>
	</tr>
	<%} %>
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
