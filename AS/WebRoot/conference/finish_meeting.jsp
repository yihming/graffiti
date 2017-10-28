<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
 <link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>完成会议申请</title>
 
	
	<script>
		function finishmeeting(){
			if(document.finish_meeting.m_title.value==""){
				alert("没有填写会议名称");
				return;
			}
			if(document.finish_meeting.m_intro.value==""){
				alert("没有填写会议内容");
				return;
			}
			document.finish_meeting.submit();	
		}	
	
		function tolist(){
			document.edit_list.title.value=document.finish_meeting.m_title.value;
			document.edit_list.intro.value=document.finish_meeting.m_intro.value;
			document.edit_list.submit();
		}
	</script>
	
  </head>
  
<body>
<div class="thickbox_content">
<h1>&gt;请输入您要邀请的人员</h1>
<% 
//	int id= Integer.parseInt((String)request.getAttribute("meeting_id"));

	List li=new ArrayList();
	li=(List)request.getAttribute("userlist");
	String m_t=(String)request.getAttribute("title");
	String m_i=(String)request.getAttribute("intro");
	if(m_t==null){
		m_t="";
	}
	if(m_i==null){
		m_i="";
	}
	session.setAttribute("userlist",li);
%>
	<table border="1" width="90%">
	
	<form name="finish_meeting" action="./conference/MeetingServlet" method="post">
		<input type="hidden" name="opflag" value="finish" />
		<input type="hidden" name="meeting_id" value=" " />
		<tr>
			<td width="33%">名称</td>
			<td width="67%"><input  type="text" name="m_title" value="<%=m_t %>"/></td>
		</tr>
		<tr>
			<td>内容提要</td><td><textarea cols="40" rows="5" name="m_intro" style=" overflow:hidden; "><%=m_i %></textarea></td>
		</tr>
	</form>	
	
		<tr>
			<td>与会人员</td>
			<td><br></td>
		</tr>

	
	<form name="edit_list" action="./conference/MeetingServlet" method="post">	
		<input type="hidden" name="opflag" value="edit_applyer" />	
		<input type="hidden" name="title" value="" />
		<input type="hidden" name="intro" value="" />
		<tr>
			<td>
				<textarea name="equip_intro" cols="20" rows="4" readonly style=" overflow:hidden; "><%
					session.setAttribute("ulist",li);
					if(li!=null){
						Iterator it=li.iterator();
						while(it.hasNext())
						{
							UserVO uservo=new UserVO();
							uservo=(UserVO)it.next();
				%><%=uservo.getUser_true_name()%>;<%}}%></textarea>
			</td>
			<td><input type="button" name="editlist" value="从列表中选取" onclick="tolist()" /></td>
		</tr>
	</form>
	 
	</table>
	


 </div>
 <div align="center"><input type="button" value="上一步" onclick="backward()" disabled />&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" name="order_submit" value="完成" onclick="finishmeeting()"/></div>
 
 <script language="Javascript">
 	function backward() {
 		window.history.back();
 	}
 </script>
</body>
</html>
