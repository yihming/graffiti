<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
  
    <title>My JSP 'noteHistory.jsp' starting page</title>
    
    <script language="javascript">
    function delNote_submit(note_id){
    	if(confirm("确实要删除吗？")){
    		document.noteDel_form.action = "./note/noteHistory.jsp?note_id="+note_id;
    		document.noteDel_form.submit();
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
   <%
   	int user_id = 42;
   	NoteDAO notedao = new NoteDAO();
  	//HttpSession session = request.getSession();
	//UserSession usersession = (UserSession)session.getAttribute("usersession");
	//if(usersession.getUser_id()!=0){
		//	user_id = usersession.getUser_id();
	//}
	int note_id = 0;
	if(request.getParameter("note_id")!=null){
		note_id = Integer.parseInt(request.getParameter("note_id"));
		notedao.setDelNote(note_id);
	}
System.out.println("noteHistory page note_id:"+note_id);	
	List noteList = new ArrayList();
	noteList = notedao.getAllNote(user_id);
	Iterator it = noteList.iterator();
	NoteVO notevo = new NoteVO();
	int i = 0;
	while(it.hasNext()){
		i++;
		notevo =(NoteVO) it.next();
   %>
  <body>
   <form name="noteDel_form" method="post">
    <table border="1" colpadding="1">
     <tr>
      <td><%=i %></td>
      <td><%=notevo.getNote_intro() %></td>
      <td><input type="button" value="删除" onclick="delNote_submit(<%=notevo.getNote_id() %>)"></td>
     </tr>
     <%} %>
    </table>
    </form>
  </body>
  
</html>
