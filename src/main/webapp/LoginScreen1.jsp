<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head><title>Đăng nhập hệ thống - LibMan</title></head>
<body>
  <nav><a href="index.jsp">Trang chủ</a></nav>
  <h2>Đăng nhập hệ thống LibMan</h2>

  <c:if test="${param.required == '1'}"><div class="msg" style="color:red;">Vui lòng đăng nhập để sử dụng chức năng này.</div></c:if>
  <c:if test="${param.loggedOut == '1'}"><div class="msg ok" style="color:green;">Bạn đã đăng xuất thành công.</div></c:if>
  <c:if test="${not empty message}"><div class="msg" style="color:red;"><c:out value="${message}"/></div></c:if>

  <%-- Gửi request (action=login, username, password) cho LoginServlet1 --%>
  <form action="${pageContext.request.contextPath}/login" method="post">
    <input type="hidden" name="action" value="login"/>
    <div><label>Tên đăng nhập:</label><input type="text" name="username" value="<c:out value='${param.username}'/>"/></div>
    <div><label>Mật khẩu:</label><input type="password" name="password"/></div>
    <button type="submit">Đăng nhập</button>
  </form>
</body>
</html>