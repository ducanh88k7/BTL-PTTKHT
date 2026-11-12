<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<%--
  Bước 27: SupplierSearchScreen1.jsp gọi trang này và truyền nhà cung cấp đã chọn.
  Trang đóng gói thành bean Supplier1, lưu vào session, đồng thời bắt đầu phiếu nhập mới.
  (Chỉ chạy khi có tham số supplierId; các lần forward từ StockInServlet1 không có tham số này.)
--%>
<c:if test="${not empty param.supplierId}">
  <jsp:useBean id="supplier" class="ptit.libman.model.Supplier1" scope="session"/>
  <jsp:setProperty name="supplier" property="id" param="supplierId"/>
  <jsp:setProperty name="supplier" property="supplierCode" param="supplierCode"/>
  <jsp:setProperty name="supplier" property="name" param="name"/>
  <jsp:setProperty name="supplier" property="address" param="address"/>
  <jsp:setProperty name="supplier" property="phone" param="phone"/>
  <jsp:setProperty name="supplier" property="email" param="email"/>
  <jsp:setProperty name="supplier" property="taxCode" param="taxCode"/>
  <jsp:setProperty name="supplier" property="description" param="description"/>
  <c:remove var="detailList" scope="session"/>
  <c:remove var="currentDocument" scope="session"/>
</c:if>
<html>
<head><title>Lập phiếu nhập</title></head>
<body>
  <nav><a href="index.jsp">Trang chủ</a><a href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất (<c:out value="${sessionScope.librarian.fullName}"/>)</a></nav>
  <h2>Lập phiếu nhập</h2>

  <c:choose>
    <c:when test="${empty sessionScope.supplier}">
      <div class="msg">Chưa chọn nhà cung cấp. <a href="SupplierSearchScreen1.jsp">Chọn nhà cung cấp</a></div>
    </c:when>
    <c:otherwise>
      <%-- Bước 28: hiển thị phiếu nhập và thông tin nhà cung cấp --%>
      <div class="box">
        <b>Nhà cung cấp:</b> <c:out value="${sessionScope.supplier.name}"/>
        (<c:out value="${sessionScope.supplier.supplierCode}"/>)
        - ĐT: <c:out value="${sessionScope.supplier.phone}"/>
        - MST: <c:out value="${sessionScope.supplier.taxCode}"/>
      </div>

      <c:if test="${not empty message}"><div class="msg"><c:out value="${message}"/></div></c:if>

      <%-- Bước 29-30: quét/nhập documentCode -> gửi request cho StockInServlet1 --%>
      <h3>Quét/nhập mã tài liệu</h3>
      <form action="${pageContext.request.contextPath}/stockin" method="post">
        <input type="hidden" name="action" value="findDocument"/>
        <label>Mã tài liệu:</label>
        <input type="text" name="documentCode" value="<c:out value='${scannedCode}'/>"/>
        <button type="submit">Tìm</button>
      </form>

      <%-- Bước 39-40: Thêm tài liệu mới (JSP -> JSP, chỉ mở form) --%>
      <p>
        <c:url var="addDocUrl" value="DocumentAddScreen1.jsp">
          <c:param name="documentCode" value="${scannedCode}"/>
        </c:url>
        <a href="${addDocUrl}">+ Thêm tài liệu mới</a>
      </p>

      <%-- Bước 38: hiển thị thông tin tài liệu vừa tìm được, nhập số lượng + đơn giá --%>
      <c:if test="${not empty sessionScope.currentDocument}">
        <div class="box">
          <b>Tài liệu:</b> <c:out value="${sessionScope.currentDocument.documentCode}"/> -
          <c:out value="${sessionScope.currentDocument.title}"/>
          (<c:out value="${sessionScope.currentDocument.author}"/>,
           <c:out value="${sessionScope.currentDocument.publisher}"/>)
          <c:choose>
            <%-- Luồng ngoại lệ: tài liệu đã có trong phiếu -> chọn cộng dồn hay thêm dòng riêng --%>
            <c:when test="${not empty duplicateLine}">
              <div class="msg">
                Tài liệu này đã có trong phiếu (dòng <c:out value="${duplicateLineNo}"/>:
                số lượng <c:out value="${duplicateLine.quantity}"/>,
                đơn giá <fmt:formatNumber value="${duplicateLine.unitPrice}" pattern="#,##0"/>).
                Bạn muốn <b>cộng dồn</b> số lượng vào dòng đó (giữ nguyên đơn giá của dòng đó),
                hay <b>thêm dòng riêng</b> theo đơn giá nhập bên dưới?
              </div>
              <form action="${pageContext.request.contextPath}/stockin" method="post">
                <input type="hidden" name="action" value="addLine"/>
                <label>Số lượng:</label><input type="number" name="quantity" min="1" value="1"/>
                <label>Đơn giá (VNĐ):</label><input type="number" name="unitPrice" min="1"
                       value="<c:out value='${duplicateLine.unitPrice}'/>"/>
                <button type="submit" name="mode" value="merge">Cộng dồn số lượng</button>
                <button type="submit" name="mode" value="separate">Thêm dòng riêng</button>
              </form>
            </c:when>
            <c:otherwise>
              <%-- Bước 52-53: nhập quantity, unitPrice, click Thêm vào phiếu -> gửi request cho StockInServlet1 --%>
              <form action="${pageContext.request.contextPath}/stockin" method="post">
                <input type="hidden" name="action" value="addLine"/>
                <label>Số lượng:</label><input type="number" name="quantity" min="1" value="1"/>
                <label>Đơn giá (VNĐ):</label><input type="number" name="unitPrice" min="1"/>
                <button type="submit">Thêm vào phiếu</button>
              </form>
            </c:otherwise>
          </c:choose>
        </div>
      </c:if>

      <%-- Bước 58: bảng chi tiết phiếu nhập đã thêm (detailList trong session, chưa lưu CSDL) --%>
      <h3>Chi tiết phiếu nhập</h3>
      <c:choose>
        <c:when test="${empty sessionScope.detailList}">
          <p>(Chưa có tài liệu nào trong phiếu)</p>
        </c:when>
        <c:otherwise>
          <c:set var="total" value="0"/>
          <table>
            <tr><th>#</th><th>Mã tài liệu</th><th>Tên tài liệu</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th><th></th></tr>
            <c:forEach var="d" items="${sessionScope.detailList}" varStatus="st">
              <c:set var="total" value="${total + d.amount}"/>
              <tr>
                <td>${st.count}</td>
                <td><c:out value="${d.document.documentCode}"/></td>
                <td><c:out value="${d.document.title}"/></td>
                <td>${d.quantity}</td>
                <td><fmt:formatNumber value="${d.unitPrice}" pattern="#,##0"/></td>
                <td><fmt:formatNumber value="${d.amount}" pattern="#,##0"/></td>
                <td>
                  <%-- Nhập nhầm: xóa dòng này khỏi phiếu (gửi request removeLine cho StockInServlet1) --%>
                  <form action="${pageContext.request.contextPath}/stockin" method="post" style="margin:0"
                        onsubmit="return confirm('Xóa dòng này khỏi phiếu nhập?');">
                    <input type="hidden" name="action" value="removeLine"/>
                    <input type="hidden" name="index" value="${st.index}"/>
                    <input type="hidden" name="documentId" value="<c:out value='${d.document.id}'/>"/>
                    <button type="submit">Xóa</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <tr><th colspan="5">Tổng cộng</th><th><fmt:formatNumber value="${total}" pattern="#,##0"/> VNĐ</th><th></th></tr>
          </table>

          <%-- Bước 59-60: click Submit -> gửi request cho StockInServlet1 --%>
          <form action="${pageContext.request.contextPath}/stockin" method="post">
            <input type="hidden" name="action" value="submit"/>
            <button type="submit">Submit (hoàn tất phiếu nhập)</button>
          </form>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</body>
</html>
