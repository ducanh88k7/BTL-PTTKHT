<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head><title>Thêm nhà cung cấp</title></head>
<body>
  <nav><a href="SupplierSearchScreen1.jsp">Quay lại tìm nhà cung cấp</a><a href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất (<c:out value="${sessionScope.librarian.fullName}"/>)</a></nav>
  <h2>Thêm nhà cung cấp mới</h2>

  <c:if test="${not empty message}"><div class="msg"><c:out value="${message}"/></div></c:if>

  <%-- Bước 16-17: nhập thông tin, click Lưu -> gửi request cho StockInServlet1 --%>
  <form action="${pageContext.request.contextPath}/stockin" method="post">
    <input type="hidden" name="action" value="addSupplier"/>
    <div><label>Tên (*):</label><input type="text" name="name" value="<c:out value='${param.name}'/>"/></div>
    <div><label>Địa chỉ:</label><input type="text" name="address" value="<c:out value='${param.address}'/>"/></div>
    <div><label>Điện thoại (*):</label><input type="text" name="phone" value="<c:out value='${param.phone}'/>"/></div>
    <div><label>Email:</label><input type="text" name="email" value="<c:out value='${param.email}'/>"/></div>
    <div><label>Mã số thuế:</label><input type="text" name="taxCode" value="<c:out value='${param.taxCode}'/>"/></div>
    <button type="submit">Lưu</button>
  </form>
</body>
</html>
