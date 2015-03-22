<%@ page contentType="text/html;charset=UTF-8"%>

<jsp:useBean id="jplagbean" class="jplagHomepage.JPlagBean" scope="page"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
  String code=request.getParameter("code");
  if(code==null || code.length()==0)
    response.sendRedirect("https://jplag.ipd.kit.edu");
%>
<head>
 <title>Extend account</title>
 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<body>
<%
  String str=jplagbean.extendAccount(code);
  if(str.length()==0) {
 %>
 <h3>Your account has been extended!</h3>
 <p>
 We hope you enjoyed using JPlag so far and continue to successfully use it
 for the next year, too!
 </p>
 <%
  }
  else {
 %>
 <h3>Following error occurred:</h3>
 <p>
 
<b> <%= str %> </b>
 <br/><br/>
 If this error message isn't very helpful, please contact the administrator!
 </p>
 <%
  }
 %>
 
 <%@include file="footer.jsp" %>
</body>