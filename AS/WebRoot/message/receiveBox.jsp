<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.dao.msgDAO,com.as.vo.msgVO,com.as.function.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    	<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'receiveBox.jsp' starting page</title>
    <script language="javascript">
    	function delMsg_check(msg_id){
    		if(confirm("确认要删除？")){
    			document.receive_form.action="./servlet/msgServlet?submitFrom=receiveBox&msg_id="+msg_id;
    			document.receive_form.submit();
    		}
    	}
    </script>
 

  </head>
  
  <body>
  <div class="thickbox_content">
  <form name="receive_form" method="post">
    <h1>收件箱</h1>
   <table border="1" width="98%" align="center"> 
		  <tr class="smallhead"> 
            <th scope="col">发件人</th> 
		    <th scope="col">发送时间</th> 
		    <th scope="col">发送标题</th> 
		    <th scope="col">内容简要</th> 
		    <th scope="col">删除</th> 
	      </tr>
	      
	<% 
		int user_id = 49;//attention session
		try{
			UserSession usersession = (UserSession) session.getAttribute("usersession");
			user_id = usersession.getUser_id();		
		}catch(Exception e){
			response.sendRedirect("../login.jsp");
		}
	      	
	    final int pageSize = 2;
	    int currentPage=1,recordCount=0,startNum=0,pageCount=1;
	    msgDAO msgdao = new msgDAO();
	    msgVO msgvo = new msgVO();
	    List msgList = new ArrayList();
	      	
	    if((request.getParameter("currentPage"))==null){
	      	msgList =msgdao.getMsg(user_id,"receive",0,0); 
	      	recordCount = msgList.size();
	      	if(recordCount%pageSize==0){
	      		pageCount = recordCount / pageSize;
	      	}else{
	      		pageCount = recordCount / pageSize+1;
	      	}
	    }else{
	      	currentPage = Integer.parseInt(request.getParameter("currentPage"));
	      	pageCount = Integer.parseInt(request.getParameter("pageCount"));
	      	startNum = (currentPage-1)*pageSize;
	      	msgList = msgdao.getMsg(user_id,"receive",startNum,startNum+pageSize);
	    }	      	 
	    Iterator it = msgList.iterator();
	    int i = 0;
	    while(it.hasNext()&&i<pageSize){
	      	i++;
	      	msgvo = (msgVO)it.next();
	      	String subMsg_intro = new String();
	      	if(msgvo.getMsg_intro().length()>10){
	      		subMsg_intro = msgvo.getMsg_intro().substring(0,10)+"...";
	      	}else{
	      		subMsg_intro = msgvo.getMsg_intro();
	      	}	      	  	
	   %>
		  <tr> 
			<th scope="row"><%=msgvo.getMsg_sender() %></th> 
			<td><%=msgvo.getMsg_time().substring(0,19)%></td> 
			<td><a href="./message/readMessage.jsp?msg_id=<%=msgvo.getMsg_id() %>"><%=msgvo.getMsg_title() %></a></td> 
			<td><%=subMsg_intro%></td> 
			<td align="center" valign="center">
			 <input type="hidden" name="msg_id" value="<%=msgvo.getMsg_id() %>"/>
			 <input type="button"  onclick="delMsg_check(<%=msgvo.getMsg_id() %>)" value="删除" />
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
		<a href="./message/receiveBox.jsp?currentPage=1&pageCount=<%=pageCount %>"><font color="red">首页</font></a>
		<a href="./message/receiveBox.jsp?currentPage=<%=currentPage-1 %>&pageCount=<%=pageCount %>"><font color="red">上一页</font></a>
	<%}%>
	<%if(currentPage==pageCount || pageCount<=1){%>
		 下一页 尾页
	<%}else{ %>
		<a href="./message/receiveBox.jsp?currentPage=<%=currentPage+1 %>&pageCount=<%=pageCount%>"><font color="red">下一页</font></a>
		<a href="./message/receiveBox.jsp?currentPage=<%=pageCount %>&pageCount=<%=pageCount %>"><font color="red">尾页</font></a>
	<%} %>
		</td>
		</tr>
		<tr><td colspan="5" align="center">第<%=currentPage %>页，每页至多<%=pageSize %>条记录，共<%=pageCount %>页</td></tr>
	</table> 
   </form>	
   </div>
  </body>
</html>
