<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head><title>Nhập tài liệu - Tìm nhà cung cấp</title></head>
<body>
  <nav><a href="index.jsp">Trang chủ</a><a href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất (<c:out value="${sessionScope.librarian.fullName}"/>)</a></nav>
  <h2>Nhập tài liệu - Tìm nhà cung cấp</h2>

  <%-- Bước 3-4: nhập keyword, click Tìm -> gửi request cho StockInServlet1 --%>
  <form action="${pageContext.request.contextPath}/stockin" method="post">
    <input type="hidden" name="action" value="searchSupplier"/>
    <label>Tên nhà cung cấp:</label>
    <input type="text" name="keyword" value="<c:out value='${keyword}'/>"/>
    <button type="submit">Tìm</button>
  </form>

  <%-- Bước 13-14: Thêm nhà cung cấp mới (JSP -> JSP, chỉ mở form) --%>
  <p><a href="SupplierAddScreen1.jsp">+ Thêm nhà cung cấp mới</a></p>

  <c:if test="${not empty message}"><div class="msg"><c:out value="${message}"/></div></c:if>

  <%-- Bước 12 / 25: hiển thị danh sách nhà cung cấp --%>
  <c:if test="${not empty listSupplier}">
    <table>
      <tr><th>Mã</th><th>Tên</th><th>Địa chỉ</th><th>Điện thoại</th><th>Email</th><th>Mã số thuế</th><th></th></tr>
      <c:forEach var="s" items="${listSupplier}">
        <tr>
          <td><c:out value="${s.supplierCode}"/></td>
          <td><c:out value="${s.name}"/></td>
          <td><c:out value="${s.address}"/></td>
          <td><c:out value="${s.phone}"/></td>
          <td><c:out value="${s.email}"/></td>
          <td><c:out value="${s.taxCode}"/></td>
          <td>
            <%-- Bước 26-27: chọn nhà cung cấp -> gọi StockInSlipScreen1.jsp, truyền nhà cung cấp (lưu session) --%>
            <form action="StockInSlipScreen1.jsp" method="post" style="margin:0">
              <input type="hidden" name="supplierId" value="<c:out value='${s.id}'/>"/>
              <input type="hidden" name="supplierCode" value="<c:out value='${s.supplierCode}'/>"/>
              <input type="hidden" name="name" value="<c:out value='${s.name}'/>"/>
              <input type="hidden" name="address" value="<c:out value='${s.address}'/>"/>
              <input type="hidden" name="phone" value="<c:out value='${s.phone}'/>"/>
              <input type="hidden" name="email" value="<c:out value='${s.email}'/>"/>
              <input type="hidden" name="taxCode" value="<c:out value='${s.taxCode}'/>"/>
              <input type="hidden" name="description" value="<c:out value='${s.description}'/>"/>
              <button type="submit">Chọn</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
</body>
</html>
