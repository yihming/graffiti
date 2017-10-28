<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.dao.msgDAO,com.as.function.*,com.as.vo.msg_allVO"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
 
  	<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'sendBox.jsp' starting page</title>
    <script language = "javascript" type="text/javascript">
     function delMsg_check(msg_id){
     if(confirm("确实要删除吗?")){
       document.send_form.action="./servlet/msgServlet?submitFrom=sendBox&msg_id="+msg_id;
       document.send_form.submit();
     	}
     }
    </script>
 
  </head>
  
  <body>
  <div class="thickbox_content">
    <h1>发件箱</h1> 
    <form name="send_form" method="post">
	    <table border="1" width="90%"> 
		  <tr class="head"> 
            <th scope="col">收件人</th> 
		    <th scope="col">发送时间</th> 
		    <th scope="col">发送标题</th> 
		    <th scope="col">内容简要</th> 
		    <th scope="col">删除</th>
	      </tr> 
	<% 
		int user_id=49;
	    try{
			UserSession usersession = (UserSession) session.getAttribute("usersession");
			user_id = usersession.getUser_id();		
		}catch(Exception e){
			response.sendRedirect("../login.jsp");
		}
	    final int pageSize = 2;
	    int currentPage=1,recordCount=0,startNum=0,pageCount=1;
	    msgDAO msgdao = new msgDAO();
	    msg_allVO msgallvo = new msg_allVO();
	    List msgList =new ArrayList();
	          
	 if((request.getParameter("currentPage"))==null){
	    msgList = msgdao.getSendMsg(user_id,0,0);
		recordCount = msgList.size();
		if(recordCount%pageSize==0){
			pageCount = recordCount/pageSize;
		}else{
			pageCount = recordCount/pageSize+1;
		}
	}else{
		currentPage = Integer.parseInt(request.getParameter("currentPage"));
	    pageCount = Integer.parseInt(request.getParameter("pageCount"));
		startNum = (currentPage-1)*pageSize;
		msgList = msgdao.getSendMsg(user_id,startNum,startNum+pageSize);
	}
	 Iterator it =  msgList.iterator();
	  int i=0;
	  while(it.hasNext()&&i<pageSize)
	     {
	     	i++;
	        msgallvo= (msg_allVO) it.next();
            String subMsg_intro = new String();
            if(msgallvo.getMsgvo().getMsg_intro().length()>10){
            	subMsg_intro = msgallvo.getMsgvo().getMsg_intro().substring(0,10)+"...";
            }else{
               	subMsg_intro = msgallvo.getMsgvo().getMsg_intro();
            }
	%>
		  <tr> 
			<td><%=msgallvo.getMsg_receivers() %></td> 
            <td><%=msgallvo.getMsgvo().getMsg_time().substring(0,19)%></td> 
			<td><a href="./message/readMessage.jsp?msg_id=<%=msgallvo.getMsgvo().getMsg_id() %>&submitFrom=sendBox"><%=msgallvo.getMsgvo().getMsg_title() %></a></td> 
			<td><%=subMsg_intro%></td> 
			<td align="center">
				<input type="hidden" name="msg_id" value="<%=msgallvo.getMsgvo().getMsg_id() %>">
			    <input type="button" value="删除" onclick="delMsg_check(<%=msgallvo.getMsgvo().getMsg_id() %>)"/>
			</td>
		  </tr> 
	<%	  
		}
	%>
		   <tr>
		<td align="center" colspan="5">
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
		<a href="./message/sendBox.jsp?currentPage=1&pageCount=<%=pageCount %>"><font color="red">首页</font></a>
		<a href="./message/sendBox.jsp?currentPage=<%=currentPage-1 %>&pageCount=<%=pageCount %>"><font color="red">上一页</font></a>
		<%}%>
		<%if(currentPage==pageCount || pageCount<=1){%>
		 下一页 尾页
		<%}else{ %>
		<a href="./message/sendBox.jsp?currentPage=<%=currentPage+1 %>&pageCount=<%=pageCount%>"><font color="red">下一页</font></a>
		<a href="./message/sendBox.jsp?currentPage=<%=pageCount %>&pageCount=<%=pageCount %>"><font color="red">尾页</font></a>
		<%} %>
		</td>
		</tr>
		<tr><td colspan="5" align="center">第<%=currentPage %>页，每页至多<%=pageSize %>条记录，共<%=pageCount %>页</td></tr>
	</table> 
	</form>
	</div>
  </body>
</html>
