<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common.jspf"%>
<html>
<head>
  <title>Hóa đơn nhập</title>
  <style>
    /* Khi in: chỉ in nội dung hóa đơn, ẩn menu, form thanh toán và các nút */
    @media print { .no-print, nav, form, button { display: none !important; } }
  </style>
</head>
<body>
  <nav><a href="index.jsp">Trang chủ</a><a href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất (<c:out value="${sessionScope.librarian.fullName}"/>)</a></nav>
  <h2>Hóa đơn nhập tài liệu</h2>

  <c:if test="${not empty message}">
    <div class="msg ${saved ? 'ok' : ''}"><c:out value="${message}"/></div>
  </c:if>

  <c:choose>
    <c:when test="${empty stockInSlip}">
      <div class="msg">Chưa có hóa đơn để hiển thị.</div>
    </c:when>
    <c:otherwise>
      <%-- Bước 81: hiển thị hóa đơn và tổng tiền --%>
      <div class="box">
        <b>Mã phiếu nhập:</b> <c:out value="${stockInSlip.stockInCode}"/><br/>
        <b>Ngày nhập:</b> <c:out value="${stockInSlip.importDate}"/><br/>
        <b>Nhà cung cấp:</b> <c:out value="${stockInSlip.supplier.name}"/>
        (MST: <c:out value="${stockInSlip.supplier.taxCode}"/>)<br/>
        <b>Nhân viên lập phiếu:</b> <c:out value="${stockInSlip.librarian.fullName}"/> (<c:out value="${stockInSlip.librarian.staffCode}"/>)<br/>
        <b>Trạng thái thanh toán:</b> <c:out value="${stockInSlip.paymentStatus}"/>
        <c:if test="${not empty stockInSlip.paymentMethod}">
          (<c:out value="${stockInSlip.paymentMethod}"/>)
        </c:if>
      </div>

      <table>
        <tr><th>#</th><th>Mã tài liệu</th><th>Tên tài liệu</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th></tr>
        <c:forEach var="d" items="${stockInSlip.details}" varStatus="st">
          <tr>
            <td>${st.count}</td>
            <td><c:out value="${d.document.documentCode}"/></td>
            <td><c:out value="${d.document.title}"/></td>
            <td>${d.quantity}</td>
            <td><fmt:formatNumber value="${d.unitPrice}" pattern="#,##0"/></td>
            <td><fmt:formatNumber value="${d.amount}" pattern="#,##0"/></td>
          </tr>
        </c:forEach>
        <tr><th colspan="5">Tổng tiền</th>
            <th><fmt:formatNumber value="${stockInSlip.totalAmount}" pattern="#,##0"/> VNĐ</th></tr>
      </table>

      <%-- In hóa đơn: dùng chức năng in của trình duyệt (use case PrintInvoice1) --%>
      <p class="no-print"><button type="button" onclick="window.print()">In hóa đơn</button></p>

      <%-- Bước 82-83: chọn paymentMethod, click Xác nhận thanh toán -> gửi request cho StockInServlet1 --%>
      <c:if test="${empty stockInSlip.paymentMethod}">
        <form action="${pageContext.request.contextPath}/stockin" method="post">
          <input type="hidden" name="action" value="confirmPayment"/>
          <input type="hidden" name="stockInSlipCode" value="<c:out value='${stockInSlip.stockInCode}'/>"/>
          <label>Hình thức thanh toán:</label>
          <select name="paymentMethod">
            <option value="Tiền mặt">Tiền mặt</option>
            <option value="Chuyển khoản">Chuyển khoản</option>
            <option value="Công nợ">Công nợ</option>
          </select>
          <button type="submit">Xác nhận thanh toán</button>
        </form>
      </c:if>
    </c:otherwise>
  </c:choose>
</body>
</html>
