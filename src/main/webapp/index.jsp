<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head><title>LibMan - Hệ thống quản lý thư viện</title></head>
<body>
  <h2>LibMan - Hệ thống quản lý thư viện</h2>

  <c:choose>
    <%-- Trường hợp Nhân viên thư viện đã đăng nhập --%>
    <c:when test="${not empty sessionScope.librarian}">
      <p>Xin chào Nhân viên thư viện, <b><c:out value="${sessionScope.librarian.fullName}"/></b> (<c:out value="${sessionScope.librarian.staffCode}"/>)
         | <a href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất</a></p>
    </c:when>

    <%-- Trường hợp Bạn đọc đã đăng nhập --%>
    <c:when test="${not empty sessionScope.reader}">
      <p>Xin chào Bạn đọc, <b><c:out value="${sessionScope.reader.fullName}"/></b> (<c:out value="${sessionScope.reader.readerCode}"/>)
         | <a href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất</a></p>
    </c:when>

    <%-- Trường hợp chưa đăng nhập --%>
    <c:otherwise>
      <p>Bạn chưa đăng nhập. <a href="LoginScreen1.jsp">Đăng nhập hệ thống</a></p>
    </c:otherwise>
  </c:choose>

  <c:if test="${not empty message}"><div class="msg"><c:out value="${message}"/></div></c:if>

  <ul>
    <c:choose>
      <c:when test="${not empty sessionScope.reader}">
        <li><a href="DocumentSearchScreen1.jsp">Bạn đọc: Tìm tài liệu</a></li>
      </c:when>
      <c:when test="${not empty sessionScope.librarian}">
        <li><a href="SupplierSearchScreen1.jsp">Nhân viên thư viện: Nhập tài liệu từ nhà cung cấp</a></li>
      </c:when>
      <c:otherwise>
        <li><a href="DocumentSearchScreen1.jsp">Bạn đọc: Tìm tài liệu</a> (cần đăng nhập)</li>
        <li><a href="SupplierSearchScreen1.jsp">Nhân viên thư viện: Nhập tài liệu từ nhà cung cấp</a> (cần đăng nhập)</li>
      </c:otherwise>
    </c:choose>
  </ul>
</body>
</html>