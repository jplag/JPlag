<%@ page contentType="text/html;charset=UTF-8"%>
<% request.setCharacterEncoding("UTF-8"); %>
<jsp:useBean id="user" class="jplagHomepage.JSPRequestData" scope="session"/>
 <%@include file="header.jsp" %>

  <div class="text">
<h1>Request a JPlag account</h1>

<p>
 In order to use JPlag, you need to have an account. With this account you
 will be able access our service through an easy and installation-free
 Java Web Start client.</p>
 <p>The information you provide in the following form will be saved on the JPlag
 server and an email verification will be sent to your email address.
 After you have visited the link provided in this email, we will review your
 application and activate your account.</p>
 <p> Please note that we do not give accounts to users of anonymous email addresses
 like Hotmail, Yahoo, Gmail, etc. Use an official email account provided by your university/school.</p>

     <form method="post" action="CheckRequest.jsp">
      <table>
       <tr>
         <td style="text-align: right;">
            <input type="checkbox" name="understand" <%
            if(user.getUnderstand()) out.print(" checked");
            %>></td>
            <td>I understand, that JPlag compares only among the files I send
            in one submission.<br />
            In no way does it compare to any external resources like books or
            internet pages or to any previously sent files.<br /><br />
          </td>
        <td><%= user.getUnderstandError() %></td>
       </tr>
       <tr>
        <td>Username:</td>
        <td><input type="text" name="username" class="text"
                           value="<%= user.getUsername() %>"></td>
        <td><%= user.getUsernameError() %></td>
       </tr>
       <tr>
        <td>Password:</td>
        <td><input type="password" name="password" class="text"
                           value="<%= user.getPassword() %>"></td>
        <td><%= user.getPasswordError() %></td>
       </tr>
       <tr>
        <td>Confirm password:</td>
        <td><input type="password" name="passwordSame" class="text"
                           value="<%= user.getPasswordSame() %>"></td>
       </tr>
       <tr>
        <td>Full name:</td>
        <td><input type="text" name="realname" class="text"
                           value="<%= user.getRealname() %>"></td>
        <td><%= user.getRealnameError() %></td>
       </tr>
       <tr>
        <td>Email address:</td>
        <td><input type="text" name="email" class="text"
                           value="<%= user.getEmail() %>"></td>
        <td><%= user.getEmailError() %></td>
       </tr>
       <tr>
        <td>Alternative email address (optional):</td>
        <td><input type="text" name="altEmail" class="text"
                           value="<%= user.getAltEmail() %>"></td>
       </tr>
       <tr>
        <td>University/school you are working for:</td>
        <td><input type="text" name="uniSchool" class="text"
                           value="<%= user.getUniSchool() %>"></td>
        <td><%= user.getUniSchoolError() %></td>
       </tr>
       <tr>
        <td>Official web page of the university/school&nbsp;&nbsp;&nbsp;<br />
                        showing your email address<br />
                        (allowing us to verify your identity):</td>
        <td valign="bottom"><input type="text" name="homepage" class="text"
                           value="<%= user.getHomepage() %>"></td>
        <td valign="bottom"><%= user.getHomepageError() %></td>
       </tr>
       <tr>
        <td>Short description of the purpose for<br />
                which you want to use JPlag<br />
                (which course etc.):</td>
        <td><textarea name="reason"><%= user.getReason() %></textarea></td>
        <td><%= user.getReasonError() %></td>
       </tr>
       <tr>
        <td>Additional notes (optional):</td>
        <td><textarea name="notes"><%= user.getNotes() %></textarea></td>
       </tr>
<tr><td colspan="3" style="text-align: center;">
      <input type="submit" value=" Send Request "/>
</td></tr>
      </table>
     </form>
<%@include file="footer.jsp" %>
