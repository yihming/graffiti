<%@ page language="java" import="java.util.*,com.as.dao.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'note.jsp' starting page</title>
    <script language="javascript">
       
       function note_check(){
       		//alert(document.forms[0].msg_rece.value);	
       		if(!document.note_form.note_intro.value){
       			alert("内容不能为空！");
       		}else{
       			document.note_form.action="./note/noteAdd.jsp";
       			document.note_form.submit();
       		}	
       }
    </script>
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
  	int user_id = 42;
  	//HttpSession session = request.getSession();
	//UserSession usersession = (UserSession)session.getAttribute("usersession");
	//if(usersession.getUser_id()!=0){
		//	user_id = usersession.getUser_id();
	//}
	String note_intro = new String();
	boolean setAddFlag = false;
	if(request.getParameter("note_intro")!=null){
		note_intro = request.getParameter("note_intro");
		NoteDAO notedao = new NoteDAO();
///System.out.println("user_id:"+user_id+"	note_intro:"+note_intro);		
		if(notedao.setAddNote(user_id,note_intro)){
			setAddFlag = true;	
		}
	}
  %>
  	<form name="note_form" method="post"> 
    <table>
    <%if(setAddFlag){%>
    <tr><td>添加成功</td></tr>
    <%}%>
    <tr><td>便签内容</td></tr>
    <tr><td><textarea name="note_intro" cols="50" rows="6"></textarea></td> </tr>
     <tr><td align="left"><input type="button" onclick="note_check()" value="提交"/></td></tr> 
    
    </table>
</form>
  </body>
</html>
