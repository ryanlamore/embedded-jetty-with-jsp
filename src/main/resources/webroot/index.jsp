<%@ page import="com.salesforce.git.management.HttpFormServlet" %>
<%@ page import="java.util.List" %>
<html>
<head><title>Request new Github repository</title></head>
<body>
<h1>Request a new Github repository under the force.com organization</h1>

  <%
    String email = (String)request.getParameter(com.salesforce.git.management.HttpFormServlet.Parameter.email.name());
    String repositoryName = (String)request.getParameter(com.salesforce.git.management.HttpFormServlet.Parameter.repository_name.name());
    String description = (String)request.getParameter(com.salesforce.git.management.HttpFormServlet.Parameter.description.name());
    String commiters = (String)request.getParameter(com.salesforce.git.management.HttpFormServlet.Parameter.commiters.name());
    String users = (String)request.getParameter(com.salesforce.git.management.HttpFormServlet.Parameter.users.name());

    if (request.getAttribute("errors") != null) {
  %>
    <font color="red">
    <ul>
      <%
        List<String> errors = (List<String>)request.getAttribute("errors");
        for (String error : errors) {
      %>
      <li><%=error%></li>
      <%
        }
      %>
      </ul>
      </font>
  <% } %>

<form action="/form/" method="post">
  <table>
    <tr>
      <td>Your @salesforce.com email address: </td>
      <td><input type="email" name="<%=com.salesforce.git.management.HttpFormServlet.Parameter.email%>" required="true" size="80" value="<%=email == null ? "" : email%>"></td>
    </tr>
    <tr>
      <td>Name of your github repository: </td>
      <td><input type="text" name="<%=com.salesforce.git.management.HttpFormServlet.Parameter.repository_name%>" required="true" size="80" value="<%=repositoryName == null ? "" : repositoryName%>"/></td>
    </tr>
    <tr>
      <td>Description of your project: </td>
      <td><textarea name="<%=com.salesforce.git.management.HttpFormServlet.Parameter.description%>" required="true" rows="3" cols="80"><%=description == null ? "" : description%></textarea></td>
    </tr>
    <tr>
      <td>Github commit usernames, comma separated<b><sup>**</sup></b>: </td>
      <td><input type="text" name="<%=com.salesforce.git.management.HttpFormServlet.Parameter.commiters%>" required="true" size="80" value="<%=commiters == null ? "" : commiters%>"/></td>
    </tr>
    <tr>
      <td>Github readonly usernames, comma separated<b><sup>**</sup></b>: </td>
      <td><input type="text" name="<%=com.salesforce.git.management.HttpFormServlet.Parameter.users%>" size="80" value="<%=users == null ? "" : users%>"/></td>
    </tr>
    <tr>
      <td colspan="2"><sup>**</sup> = <b>NOT your LDAP user or git.soma user, but GITHUB user.  Create a new one if you don't have one</b></td>
    </tr>
  </table>
  <input type="submit" value="Submit">
</form>
</body>
</html>
