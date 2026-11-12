<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head>
  <title>Chi tiết tài liệu</title>
  <style>
    .plain th, .plain td { border: none; padding: 3px 14px 3px 0; background: none; font-weight: normal; text-align: left; }
    .plain th { color: #333; }
    .btn { display: inline-block; padding: 6px 18px; border: 1px solid #333; border-radius: 6px;
           background: #eee; color: #000; text-decoration: none; }
  </style>
</head>
<body>
  <nav><a href="index.jsp">Trang chủ</a><a href="DocumentSearchScreen1.jsp">Tìm tài liệu khác</a></nav>
  <h2>CHI TIẾT TÀI LIỆU</h2>

  <%-- Hiển thị chi tiết tài liệu + danh sách các bản sao (DocumentServlet1 đã lấy sẵn: document, copyList) --%>
  <c:choose>
    <c:when test="${empty document}">
      <div class="msg">Không có tài liệu để hiển thị.</div>
    </c:when>
    <c:otherwise>
      <table class="plain">
        <tr><th>Mã tài liệu:</th><td><c:out value="${document.documentCode}"/></td></tr>
        <tr><th>Tên tài liệu:</th><td><c:out value="${document.title}"/></td></tr>
        <tr><th>Loại:</th><td><c:out value="${document.documentType.typeName}"/></td></tr>
        <tr><th>Tác giả:</th><td><c:out value="${document.author}"/></td></tr>
        <tr><th>Nhà xuất bản:</th><td><c:out value="${document.publisher}"/></td></tr>
        <tr><th>Năm xuất bản:</th><td><c:out value="${document.publishYear}"/></td></tr>
        <tr><th>Giá bìa:</th><td><fmt:formatNumber value="${document.price}" pattern="#,##0"/> VNĐ</td></tr>
        <tr><th>Mô tả:</th><td><c:out value="${document.description}"/></td></tr>
      </table>

      <h3>Các bản tài liệu (sẵn có <c:out value="${availableCount}"/>/<c:out value="${totalCount}"/>)</h3>
      <c:choose>
        <c:when test="${empty copyList}">
          <p>(Tài liệu này chưa có bản sao nào)</p>
        </c:when>
        <c:otherwise>
          <table>
            <tr><th>Mã vạch</th><th>Vị trí kệ</th><th>Tình trạng</th><th>Trạng thái</th></tr>
            <c:forEach var="copy" items="${copyList}">
              <tr>
                <td><c:out value="${copy.copyCode}"/></td>
                <td><c:out value="${document.location}"/></td>
                <td><c:out value="${copy.condition}"/></td>
                <td><c:out value="${copy.availabilityStatus}"/></td>
              </tr>
            </c:forEach>
          </table>
        </c:otherwise>
      </c:choose>

      <%-- Quay lại danh sách kết quả tìm kiếm với đúng từ khóa --%>
      <c:url var="backUrl" value="/document">
        <c:param name="action" value="search"/>
        <c:param name="keyword" value="${keyword}"/>
      </c:url>
      <p><a class="btn" href="${backUrl}">Quay lại kết quả</a></p>
    </c:otherwise>
  </c:choose>
</body>
</html>
