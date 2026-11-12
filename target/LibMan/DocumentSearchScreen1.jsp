<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head><title>Tìm tài liệu</title></head>
<body>
  <nav><a href="index.jsp">Trang chủ</a></nav>
  <h2>Tìm tài liệu</h2>

  <%-- Bước 3-4: nhập keyword, click Tìm -> gửi request cho DocumentServlet1 --%>
  <form action="${pageContext.request.contextPath}/document" method="post">
    <input type="hidden" name="action" value="search"/>
    <label>Từ khóa (tên tài liệu):</label>
    <input type="text" name="keyword" value="<c:out value='${keyword}'/>"/>
    <button type="submit">Tìm</button>
  </form>

  <c:if test="${not empty message}"><div class="msg"><c:out value="${message}"/></div></c:if>

  <%-- Bước 18: hiển thị danh sách tài liệu kèm số bản sao còn sẵn --%>
  <c:if test="${not empty listDocument}">
    <table>
      <tr><th>Mã tài liệu</th><th>Tên tài liệu</th><th>Tác giả</th><th>Nhà xuất bản</th>
          <th>Số bản sao còn sẵn</th><th></th></tr>
      <c:forEach var="d" items="${listDocument}">
        <tr>
          <td><c:out value="${d.documentCode}"/></td>
          <td><c:out value="${d.title}"/></td>
          <td><c:out value="${d.author}"/></td>
          <td><c:out value="${d.publisher}"/></td>
          <td><c:out value="${availableCount[d.documentCode]}"/><c:if test="${availableCount[d.documentCode] == 0}"> (Hết sách)</c:if></td>
          <td>
            <%-- Chọn 1 tài liệu -> gửi documentCode (và keyword để quay lại đúng kết quả) cho DocumentServlet1 --%>
            <form action="${pageContext.request.contextPath}/document" method="post" style="margin:0">
              <input type="hidden" name="action" value="detail"/>
              <input type="hidden" name="documentCode" value="<c:out value='${d.documentCode}'/>"/>
              <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>"/>
              <button type="submit">Xem chi tiết</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
</body>
</html>
