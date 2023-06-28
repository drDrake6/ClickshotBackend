<%@ page import="org.json.JSONObject" %><%@
page contentType="text/html;charset=UTF-8" language="java"%><%
JSONObject obj = (JSONObject)request.getAttribute("smth");
%>
<% if(obj != null){ %>
<b><%= obj %></b>
<% } %>