<%@ page contentType="text/html;charset=UTF-8"%>
<% request.setCharacterEncoding("UTF-8"); %>
<jsp:useBean id="devuser" class="jplagHomepage.JSPDeveloperData" scope="session"/>
<jsp:setProperty name="devuser" property="reset" value=""/>
<jsp:setProperty name="devuser" property="*"/>

 <%@include file="header.jsp" %>

<h1>Developer Registration</h1>

<p>Below, you can sign up yourself for developer mails concerning
changes of the compatibility level and related needed changes to your client
as described in the last chapter of the tutorial. As mentioned there it is
not our intention to make such changes in the near future, but we want to make
sure you get the ability to know about changes before your client becomes
unusable.</p>

<form method="post" action="develop.jsp">
 <input type="hidden" name="initial" value="false" />
 <table>
  <tr>
   <td>Username:</td>
   <td><input type="text" name="username" style="width: 20em"
		value="<%= devuser.getUsername() %>" /></td>
  </tr>
  <tr>
   <td>Password:</td>
   <td><input type="password" name="password" style="width: 20em"
		value="<%= devuser.getPassword() %>" /></td>
  </tr>
  <tr>
   <td align="right"><input type="radio" name="signwhat" value="signup" 
   	<% if(devuser.getSignwhat().equals("signup")) out.print("checked"); %>
   	/></td>
   <td>Sign up as developer</td>
  </tr>
  <tr>
   <td align="right"><input type="radio" name="signwhat" value="signoff"
   	<% if(devuser.getSignwhat().equals("signoff")) out.print("checked"); %>
   	/></td>
   <td>Remove developer status</td>
  </tr>
  <tr>
   <td colspan="2">
    <br />
    <%= devuser.setDeveloperState() %>
   </td>
  </tr>
  <tr>
  <td colspan="2"><input type="submit" value=" Update Developer Status "/></td>
  </tr>
 </table>
</form>
 
 <%@include file="footer.jsp" %>

</body>
</html>