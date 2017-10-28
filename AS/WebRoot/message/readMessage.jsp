<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.dao.msgDAO,com.as.vo.msgVO,com.as.function.AS"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  <link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'readMessage.jsp' starting page</title>
     <script language="javascript">
     	function replyMsg(msg_id){
     		document.readMsg_form.action="./message/sendMessage.jsp?msg_reply_to_id="+msg_id;
     		document.readMsg_form.submit();
     	}
    </script>
 
  </head>
  <% 
	int msg_id = 49;
  	msg_id = Integer.parseInt(request.getParameter("msg_id"));
  	String submitFrom = new String();
	msgDAO msgdao = new msgDAO();
	msgVO msgvo = new msgVO();
	msgvo = msgdao.getMsgById(msg_id);	
	if(request.getParameter("submitFrom")!=null){
		submitFrom = (String)request.getParameter("submitFrom");
  		if("unReadBox".equals((String)request.getParameter("submitFrom"))){
  			msgdao.setReadMsg(msgvo.getMsg_id());
  		}
  	}
  %>
  <body>
  <div class="thickbox_content">
  <form name="readMsg_form" method="post">
    <table border="1px" width="98%">
	 <tr><td width="20%">发件人</td><td><%=msgvo.getMsg_sender() %></td>
	 </tr> 
	 <tr><td>主题</td><td><%=msgvo.getMsg_title() %></td> </tr> 
	 <tr><td>内容</td><td><%=msgvo.getMsg_intro() %></td> </tr>
	 <%if(!("sendBox".equals(submitFrom))){
//System.out.println("submitFrom:"+submitFrom);	 
	 %>
	 <tr><td align="center" colspan="2"><input type="button" value="回复" onclick="replyMsg(<%=msgvo.getMsg_id()%>)"/></td> </tr>
	 <% 
	 }
	 %>
	</table> 
	</form>
     <button onclick="window.history.back()">返回发件箱</button>
	</div>
  </body>
</html>
