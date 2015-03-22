<%@ page contentType="text/html;charset=UTF-8"%>

<HTML>
  <HEAD>

  <TITLE>JPlagTest</TITLE>
  </HEAD>
  <BODY>
  <CENTER>
  <BR>
  <jsp:useBean id="jplagTest" class="jplagHomepage.JPlagTestBean"
  			   scope="page" />
  <%
    String l_result  = jplagTest.getServerInfo();
  %>
  <%= l_result %>
  </CENTER>
 
 <%@include file="footer.jsp" %>
  </BODY>
</HTML>
