<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.dao.msgDAO,com.as.vo.msgVO,com.as.function.AS"%>
<%@ page language="java" import="com.as.dao.UserDAO,com.as.db.*" %>
<%@ page language="java" import="com.as.vo.UserVO" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  	<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'sendMessage.jsp' starting page</title>
    <script language="javascript">
       function sm_convert(){
         var list = document.forms[0].receiver;
         var user_name = list.options[list.selectedIndex].text+";";
         document.forms[0].msg_rece.value+=user_name;
         list.options[list.selectedIndex].disabled =true;
         document.forms[0].msg_user_id_list.value += list.options[list.selectedIndex].value+"+";
       }
       
       function sm_check(){
       		//alert(document.forms[0].msg_rece.value);	
       		if(!document.sm_form.msg_rece.value){
       			alert("收件人不能为空！");
       		}else if(!document.sm_form.msg_title.value){
       			alert("消息主题不能为空！");
       		}else if(!document.sm_form.msg_intro.value){
       			alert("消息内容不能为空，请认真填写！");
       		}else{
       			if(confirm("请确认信息")){
       				document.sm_form.action="./servlet/msgServlet?submitFrom=sendMessage";
       				document.sm_form.submit();
       			}
       		}
       		
       }
    </script>
	 
  </head>
   
  <body>
  <div class="thickbox_content">
   <form name="sm_form" action="./servlet/msgServlet?submitFrom=sendMessage" method="post"> 
    <%	
    	String success = "false";
    	if(request.getParameter("success")!=null){
    		success = request.getParameter("success");
		}
		if("true".equals(success)){
	%>
	<h1>恭喜发送成功</h1>
	<%}else{ %>
  	<h1>发送新消息</h1> 
  	<%} %>
    <table border="1px"> 
	  <%
	  int msg_reply_to_id = 0;//attention
	  if(request.getParameter("msg_reply_to_id")!=null){
	  		msg_reply_to_id = Integer.parseInt(request.getParameter("msg_reply_to_id"));
		}
	  if(msg_reply_to_id==0){
	  	UserDAO userdao = new UserDAO();
	  	List list = new ArrayList();
	  	UserVO uservo = new UserVO();
	  	list = userdao.getAllUserList();
	  	Iterator it = list.iterator();
	  %>
	  <tr><td>收件人</td><td><input type="text" name="msg_rece" size="50"></td>
	  <td>
	  <select name="receiver" onchange="sm_convert(); ">
	  <option value="" disabled="true">请选择收件人
	  
	  <%
	  	while(it.hasNext())
	  	{
	  	uservo = (UserVO)it.next();
	   %>
	   <option value="<%=uservo.getUser_id() %>"><%=uservo.getUser_name() %>
	   
	   <%
	   	}
	   	%>
	   	</select>
	  </td>
	 </tr>
	 <tr><td>主题</td><td><input name="msg_title" type="text" size="50"></td> </tr> 
	  <%
	   }
	   else{	
	     msgDAO msgdao = new msgDAO();   	 
	   	 msgVO msgvo = new msgVO();
	   	 msgvo = msgdao.getMsgById(msg_reply_to_id);	   	 
	  %>
	   <tr><td>收件人</td>
	   <td><input type="text" name="msg_rece" value="<%=msgdao.getMsgReceNamesById(msg_reply_to_id)%>" size="50"></td>
	  </tr>
	 <tr><td>主题</td>
	 <td colspan="2"><input type="text" value="回复：<%=msgvo.getMsg_title() %>" name="msg_title" type="text" size="50"></td>
	 <td>&nbsp;</td>
	 </tr> 
	  <%
	  }
	  %>
	 <tr><td>内容</td><td colspan="2"><textarea name="msg_intro" cols="50" rows="6"></textarea></td> </tr>
	 <input type="hidden" name="msg_reply_to_id" value="<%=msg_reply_to_id %>"/>
	 <input type="hidden" vlaue="" name="msg_user_id_list"/>
	 <tr><td>&nbsp;</td><td align="center" colspan="2">
	 <input type="button" onclick="sm_check()" value="发送"/></td></tr> 
	</table> 
  </form> 
  </div>
  </body>
</html>
