<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head><title>Thêm tài liệu mới</title></head>
<body>
  <nav><a href="StockInSlipScreen1.jsp">Quay lại phiếu nhập</a><a href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất (<c:out value="${sessionScope.librarian.fullName}"/>)</a></nav>
  <h2>Thêm tài liệu mới</h2>

  <c:if test="${not empty message}"><div class="msg"><c:out value="${message}"/></div></c:if>

  <%-- Bước 42-43: nhập thông tin, click Lưu -> gửi request cho StockInServlet1 --%>
  <form action="${pageContext.request.contextPath}/stockin" method="post">
    <input type="hidden" name="action" value="addDocument"/>
    <div><label>Mã tài liệu (*):</label><input type="text" name="documentCode" value="<c:out value='${param.documentCode}'/>"/></div>
    <div><label>Tên tài liệu (*):</label><input type="text" name="title" value="<c:out value='${param.title}'/>"/></div>
    <div><label>Tác giả:</label><input type="text" name="author" value="<c:out value='${param.author}'/>"/></div>
    <div><label>Nhà xuất bản:</label><input type="text" name="publisher" value="<c:out value='${param.publisher}'/>"/></div>
    <div><label>Năm xuất bản:</label><input type="number" name="publishYear" value="<c:out value='${param.publishYear}'/>"/></div>
    <div><label>Loại tài liệu:</label>
      <select name="documentTypeId">
        <option value="1" ${param.documentTypeId == '1' ? 'selected' : ''}>Sách</option>
        <option value="2" ${param.documentTypeId == '2' ? 'selected' : ''}>Giáo trình</option>
        <option value="3" ${param.documentTypeId == '3' ? 'selected' : ''}>Tạp chí</option>
      </select></div>
    <div><label>Vị trí lưu trữ:</label><input type="text" name="location" value="<c:out value='${param.location}'/>"/></div>
    <div><label>Giá:</label><input type="number" name="price" value="<c:out value='${param.price}'/>"/></div>
    <div><label>Mô tả:</label><input type="text" name="description" value="<c:out value='${param.description}'/>"/></div>
    <button type="submit">Lưu tài liệu mới</button>
  </form>
</body>
</html>
