<%@ page contentType="text/html;charset=UTF-8" language="java" %><%
    String host = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>Clickshot</h1>
    <p>
        HOST: <%=host %>
    </p>
    <p>
        <%= request.getRequestURI() %>
    </p>
    <a href="/allUsers">allUsers</a>
</body>
</html>