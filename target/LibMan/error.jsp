<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head><title>Lỗi</title></head>
<body>
  <h2>Đã xảy ra lỗi</h2>
  <div class="msg"><c:out value="${pageContext.exception.message}"/></div>
  <p><a href="index.jsp">Về trang chủ</a></p>
</body>
</html>
