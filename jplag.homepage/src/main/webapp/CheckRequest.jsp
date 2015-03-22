<%@ page contentType="text/html;charset=UTF-8"%>
<% request.setCharacterEncoding("UTF-8"); %>
<jsp:useBean id="user" class="jplagHomepage.JSPRequestData" scope="session"/>
<jsp:setProperty name="user" property="reset" value=""/>
<jsp:setProperty name="user" property="*"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
 <title>Check request</title>
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<body background="pics/background.png" style="background-attachment:fixed">
 <br/><br/>
 <%
  if(user.checkAll()) {
    out.println(user.sendRequest());
  }
  else pageContext.forward("RequestForm.jsp");
 %>
 
 <%@include file="footer.jsp" %>
</body>
</html>