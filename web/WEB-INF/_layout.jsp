<%@ page contentType="text/html;charset=UTF-8" language="java" %><%
  String pageBody = "/WEB-INF/" + request.getAttribute("pageBody");
%>
<%--  <jsp:include page="/WEB-INF/header.jsp"/>--%>
  <jsp:include page="<%=pageBody%>"/>
<%--  <jsp:include page="footer.jsp"/>--%>

