<%@ page language="java" import="java.util.*,javax.servlet.http.HttpSession" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.vo.UserVO,com.as.vo.msgVO,com.as.dao.msgDAO,com.as.function.UserSession" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  	<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'unReadBox.jsp' starting page</title>
    <script language="javascript">
     	function replyMsg(msg_id){
     		document.unRead_form.action="./message/sendMessage.jsp?msg_reply_to_id="+msg_id;
     		document.unRead_form.submit();
     	}
     	function delMsg_check(msg_id){
     		if(confirm("确实要删除吗？")){
     			document.unRead_form.action="./servlet/msgServlet?submitFrom=unReadBox&msg_id="+msg_id;
     			document.unRead_form.submit();
     		}
     	}
    </script>
 
  </head>
  
  <body>
  <div class="thickbox_content">
  	 <% 
	 
	 int user_id = 49;//attention 

	 try{
			UserSession usersession = (UserSession) session.getAttribute("usersession");
			user_id = usersession.getUser_id();		
		}catch(Exception e){
			response.sendRedirect("../login.jsp");
		}
	
	final int pageSize = 2;
	int currentPage = 1,recordCount = 0,pageCount = 1,startNum = 0;
	msgDAO msgdao = new msgDAO();
	List msgList = new ArrayList();
	msgVO msgvo = new msgVO();
	if((request.getParameter("currentPage"))==null){
		msgList = msgdao.getMsg(user_id,"unRead",0,0);
		recordCount = msgList.size();
		if(recordCount%pageSize==0){
			pageCount = recordCount/pageSize;
		}else{
			pageCount = recordCount/pageSize+1;
		}
	}else{
		currentPage = Integer.parseInt(request.getParameter("currentPage"));
		if(request.getParameter("pageCount")!=null)
	    	pageCount = Integer.parseInt(request.getParameter("pageCount"));
	    if(request.getParameter("recordCount")!=null)
	    	recordCount = Integer.parseInt(request.getParameter("recordCount"));
		startNum = (currentPage-1)*pageSize;
		msgList = msgdao.getMsg(user_id,"unRead",startNum,startNum+pageSize);
	}
	Iterator it = msgList.iterator();
	%>
   <h1>未读短信</h1> 
   <form name="unRead_form" method="post">
	<table border="1" width="98%">
	 <tr  class="smallhead"> 
	  <th scope="col">发件人</th> 
	  <th scope="col">发送时间</th> 
	  <th scope="col">发送标题</th> 
	  <th scope="col">内容简要</th> 
	  <th scope="col">回复/删除</th> 
	 </tr> 
	<%
	int i =0;
	while(it.hasNext()&&i<pageSize){
		i++;
		msgvo =(msgVO)it.next();	
		String subMsg_intro = new String();
	    if(msgvo.getMsg_intro().length()>10){
	      	subMsg_intro = msgvo.getMsg_intro().substring(0,10)+"...";
	    }else{
	      	subMsg_intro = msgvo.getMsg_intro();
	    }	
	%>
		  <tr> 
			<th scope="row"><%=msgvo.getMsg_sender()%></th> 
			<td><%=msgvo.getMsg_time().substring(0,19)%></td> 
			<td><a href="./message/readMessage.jsp?msg_id=<%=msgvo.getMsg_id()%>&submitFrom=unReadBox"><%=msgvo.getMsg_title()%></a></td> 
			<td><%=subMsg_intro %></td> 
			<td align="center">
			    <!-- <input type="hidden" name="recordCount" value="<%=recordCount%>" />  -->
				<!-- <input type="hidden" name="msg_id" value="<%=msgvo.getMsg_id() %>"> -->
			    <input type="button" value="回复" onclick="replyMsg(<%=msgvo.getMsg_id()%>)"/>
			    <input type="button" value="删除" onclick="delMsg_check(<%=msgvo.getMsg_id() %>)"/>
			</td>
		  </tr> 
		  <%
		  }
		  %>
		  <tr>
		<td colspan="5" align="center">
		
		<%	
		if(i<1){
			if(currentPage==pageCount){
				currentPage--;
			}
			pageCount--;
		}
		if(currentPage==1){
		%>
		 首页 上一页
		<%}else{%>
		<a href="./message/unReadBox.jsp?currentPage=1&pageCount=<%=pageCount %>"><font color="red">首页</font></a>
		<a href="./message/unReadBox.jsp?currentPage=<%=currentPage-1 %>&pageCount=<%=pageCount %>"><font color="red">上一页</font></a>
		<%}%>
		<%if(currentPage==pageCount || pageCount<=1){%>
		 下一页 尾页
		<%}else{ %>
		<a href="./message/unReadBox.jsp?currentPage=<%=currentPage+1 %>&pageCount=<%=pageCount%>"><font color="red">下一页</font></a>
		<a href="./message/unReadBox.jsp?currentPage=<%=pageCount %>&pageCount=<%=pageCount %>"><font color="red">尾页</font></a>
		<%} %>
		</td>
		</tr>
		<tr><td colspan="5" align="center">第<%=currentPage %>页，每页至多<%=pageSize %>条记录，共<%=pageCount %>页</td></tr>
	</table>
   </form>	
   </div>
  </body>
</html>
