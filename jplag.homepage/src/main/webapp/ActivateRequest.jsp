<%@ page contentType="text/html;charset=UTF-8"%>

<jsp:useBean id="jplagbean" class="jplagHomepage.JPlagBean" scope="page"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
  String code=request.getParameter("code");
  if(code==null || code.length()==0)
    response.sendRedirect("http://jplag.ipd.kit.edu");
%>
<head>
 <title>Activate request</title>
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<body background="pics/background.png" style="background-attachment:fixed">
 <br/><br/>
 <%
  String str=jplagbean.validateEmail(code);
  if(str.length()==0) {
 %>
 <center><h3>Thank you for your email verification!</h3></center><br/><br/>
 
 The request was given to the administrator, who will decide on your request
 soon!
 <%
  }
  else {
 %>
 <center><h3>Following error occurred:</h3></center><br/><br/>
 
<b> <%= str %> </b>
 <br/><br/>
 If this error message isn't very helpful, please contact the administrator!
 <%
  }
 %>
 
 <%@include file="footer.jsp" %>
</body>