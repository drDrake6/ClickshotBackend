<%@ page import="java.lang.System" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %><%
    String contextPath = request.getContextPath();
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>Ой</h1>
<p><%=System.getProperty("user.dir")%></p>
<img src="<%=contextPath%>/media/gopher.png" alt="none">
</body>
</html>
