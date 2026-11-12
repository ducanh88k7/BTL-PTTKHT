package ptit.libman.web;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ptit.libman.dao.DocumentCopyDAO1;
import ptit.libman.dao.DocumentDAO1;
import ptit.libman.dao.StockInSlipDAO1;
import ptit.libman.dao.SupplierDAO1;
import ptit.libman.model.Document1;
import ptit.libman.model.DocumentCopy1;
import ptit.libman.model.Librarian1;
import ptit.libman.model.StockInSlip1;
import ptit.libman.model.StockInSlipDetail1;
import ptit.libman.model.Supplier1;

@WebServlet("/stockin")
public class StockInServlet1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SupplierDAO1 supplierDAO;
    private DocumentDAO1 documentDAO;
    private StockInSlipDAO1 stockInSlipDAO;
    private DocumentCopyDAO1 documentCopyDAO;

    @Override
    public void init() {
        supplierDAO = new SupplierDAO1();
        documentDAO = new DocumentDAO1();
        stockInSlipDAO = new StockInSlipDAO1();
        documentCopyDAO = new DocumentCopyDAO1();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession().getAttribute("librarian") == null) {   // AuthFilter đã chặn; kiểm tra thêm cho chắc
            response.sendRedirect(request.getContextPath() + "/LoginScreen1.jsp?required=1");
            return;
        }
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "searchSupplier":
                    searchSupplier(request, response);
                    break;
                case "addSupplier":
                    addSupplier(request, response);
                    break;
                case "findDocument":
                    findDocument(request, response);
                    break;
                case "addDocument":
                    addDocument(request, response);
                    break;
                case "addLine":
                    addLine(request, response);
                    break;
                case "removeLine":
                    removeLine(request, response);
                    break;
                case "submit":
                    submit(request, response);
                    break;
                case "confirmPayment":
                    confirmPayment(request, response);
                    break;
                default:
                    forward(request, response, "SupplierSearchScreen1.jsp");
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    // ------------------------------------------------------------------
    // Vòng lặp 1: tìm / thêm nhà cung cấp
    // ------------------------------------------------------------------

    /** Bước 4-12: LẤY dữ liệu - tìm nhà cung cấp theo tên. */
    private void searchSupplier(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String keyword = request.getParameter("keyword");

        List<Supplier1> listSupplier = supplierDAO.searchByName(keyword);

        request.setAttribute("keyword", keyword);
        request.setAttribute("listSupplier", listSupplier);
        if (listSupplier.isEmpty()) {
            request.setAttribute("message",
                    "Không tìm thấy nhà cung cấp, bạn có muốn thêm mới?");
        }
        forward(request, response, "SupplierSearchScreen1.jsp");
    }

    /** Bước 17-25: LƯU dữ liệu - thêm nhà cung cấp mới. */
    private void addSupplier(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String name = trim(request.getParameter("name"));
        String address = trim(request.getParameter("address"));
        String phone = trim(request.getParameter("phone"));
        String email = trim(request.getParameter("email"));
        String taxCode = trim(request.getParameter("taxCode"));

        if (name.isEmpty() || phone.isEmpty()) {
            request.setAttribute("message", "Tên và số điện thoại nhà cung cấp là bắt buộc.");
            forward(request, response, "SupplierAddScreen1.jsp");
            return;
        }

        Supplier1 supplier = new Supplier1(name, address, phone, email, taxCode); // Servlet -> thực thể
        boolean saved = supplierDAO.addSupplier(supplier);                      // Servlet -> DAO

        if (saved) {
            // hiển thị nhà cung cấp vừa thêm để thủ thư chọn luôn
            request.setAttribute("listSupplier", Collections.singletonList(supplier));
            request.setAttribute("message", "Đã thêm nhà cung cấp mới. Hãy chọn nhà cung cấp để tiếp tục.");
            forward(request, response, "SupplierSearchScreen1.jsp");
        } else {
            request.setAttribute("message", "Không thể lưu nhà cung cấp.");
            forward(request, response, "SupplierAddScreen1.jsp");
        }
    }

    // ------------------------------------------------------------------
    // Vòng lặp 2: nhập từng tài liệu
    // ------------------------------------------------------------------

    /** Bước 30-41: LẤY dữ liệu - tìm tài liệu theo mã quét/nhập. */
    private void findDocument(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession();
        if (!hasSupplier(request, response, session)) {
            return;
        }
        String documentCode = trim(request.getParameter("documentCode"));

        Document1 document = documentDAO.findByCode(documentCode);

        request.setAttribute("scannedCode", documentCode);
        if (document == null) {
            session.removeAttribute("currentDocument");
            request.setAttribute("message",
                    "Không tìm thấy tài liệu có mã '" + documentCode + "', bạn có muốn thêm mới?");
        } else {
            session.setAttribute("currentDocument", document);
        }
        // Nếu tài liệu đã có trong phiếu, forwardSlip đánh dấu để trang hỏi: cộng dồn hay thêm dòng riêng
        forwardSlip(request, response, session);
    }

    /** Bước 43-51: LƯU dữ liệu - thêm tài liệu mới (khi quét không thấy). */
    private void addDocument(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession();
        if (!hasSupplier(request, response, session)) {
            return;
        }
        String documentCode = trim(request.getParameter("documentCode"));
        String title = trim(request.getParameter("title"));
        String author = trim(request.getParameter("author"));
        String publisher = trim(request.getParameter("publisher"));
        String location = trim(request.getParameter("location"));
        String description = trim(request.getParameter("description"));

        if (documentCode.isEmpty() || title.isEmpty()) {
            request.setAttribute("message", "Mã tài liệu và Tên tài liệu là bắt buộc.");
            forward(request, response, "DocumentAddScreen1.jsp");
            return;
        }
        int publishYear;
        double price;
        int documentTypeId;
        try {
            publishYear = parseIntOrZero(request.getParameter("publishYear"));
            price = parseDoubleOrZero(request.getParameter("price"));
            documentTypeId = Integer.parseInt(request.getParameter("documentTypeId"));
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Năm xuất bản, giá hoặc loại tài liệu không hợp lệ.");
            forward(request, response, "DocumentAddScreen1.jsp");
            return;
        }

        Document1 document = new Document1(documentCode, title, author, publisher, publishYear,
                location, price, description, documentTypeId);   // Servlet -> thực thể
        boolean saved = documentDAO.addDocument(document);       // Servlet -> DAO

        if (saved) {
            session.setAttribute("currentDocument", document);   // dùng ngay cho dòng chi tiết đang lập
            request.setAttribute("scannedCode", documentCode);
            request.setAttribute("message", "Đã thêm tài liệu mới. Hãy nhập số lượng và đơn giá.");
            forward(request, response, "StockInSlipScreen1.jsp");
        } else {
            request.setAttribute("message", "Mã tài liệu đã tồn tại hoặc loại tài liệu không hợp lệ.");
            forward(request, response, "DocumentAddScreen1.jsp");
        }
    }

    /**
     * Bước 53-61: thêm một dòng chi tiết vào detailList (trong session, CHƯA lưu CSDL).
     *
     * Nếu tài liệu chưa có trong phiếu: đóng gói StockInSlipDetail1 mới (luồng chính).
     * Nếu tài liệu đã có trong phiếu, thủ thư phải chọn (tham số mode):
     *   mode=merge    : cộng dồn số lượng vào dòng gần nhất của tài liệu, giữ nguyên đơn giá dòng đó
     *                   (không tạo StockInSlipDetail1 mới)
     *   mode=separate : thêm dòng riêng với đơn giá vừa nhập (chính là luồng chính)
     *   (không có mode): chưa chọn -> hỏi lại, không thêm gì
     */
    private void addLine(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession();
        if (!hasSupplier(request, response, session)) {
            return;
        }
        Document1 document = (Document1) session.getAttribute("currentDocument");
        if (document == null) {
            request.setAttribute("message", "Hãy quét/nhập mã tài liệu trước.");
            forwardSlip(request, response, session);
            return;
        }

        int existingIndex = findLastLineIndex(session, document);
        String mode = trim(request.getParameter("mode"));
        boolean merge = existingIndex >= 0 && "merge".equals(mode);

        int quantity;
        double unitPrice = 0;
        try {
            quantity = Integer.parseInt(trim(request.getParameter("quantity")));
            if (!merge) {                       // cộng dồn giữ đơn giá cũ nên không cần đơn giá mới
                unitPrice = Double.parseDouble(trim(request.getParameter("unitPrice")));
            }
        } catch (NumberFormatException e) {
            request.setAttribute("message", merge
                    ? "Số lượng phải là số." : "Số lượng và đơn giá phải là số.");
            forwardSlip(request, response, session);
            return;
        }
        if (quantity <= 0) {
            request.setAttribute("message", "Số lượng phải lớn hơn 0.");
            forwardSlip(request, response, session);
            return;
        }
        if (!merge && unitPrice <= 0) {
            request.setAttribute("message", "Đơn giá không hợp lệ.");
            forwardSlip(request, response, session);
            return;
        }

        @SuppressWarnings("unchecked")
        List<StockInSlipDetail1> detailList = (List<StockInSlipDetail1>) session.getAttribute("detailList");

        // Đã có dòng của tài liệu nhưng chưa chọn cách xử lý: hỏi lại, không thêm gì
        if (existingIndex >= 0 && !merge && !"separate".equals(mode)) {
            forwardSlip(request, response, session);
            return;
        }

        // Cộng dồn: cập nhật dòng đã có (không tạo thực thể mới)
        if (merge) {
            StockInSlipDetail1 existing = detailList.get(existingIndex);
            existing.setQuantity(existing.getQuantity() + quantity);
            session.removeAttribute("currentDocument");
            request.setAttribute("message", "Đã cộng dồn số lượng vào dòng " + (existingIndex + 1) + ": "
                    + document.getTitle() + " (tổng " + existing.getQuantity() + ")");
            forwardSlip(request, response, session);
            return;
        }

        // Luồng chính / thêm dòng riêng: Servlet -> thực thể StockInSlipDetail1
        StockInSlipDetail1 detail = new StockInSlipDetail1(document, quantity, unitPrice);
        if (detailList == null) {
            detailList = new ArrayList<StockInSlipDetail1>();
            session.setAttribute("detailList", detailList);
        }
        detailList.add(detail);
        session.removeAttribute("currentDocument");

        request.setAttribute("message", "Đã thêm vào phiếu: " + document.getTitle());
        forwardSlip(request, response, session);
    }

    /**
     * Xóa một dòng chi tiết đã thêm nhầm khỏi detailList (trong session, chưa lưu CSDL).
     * Dòng được xác định bằng vị trí (index) và kiểm tra khớp id tài liệu, để không xóa nhầm
     * khi trang đã cũ (ví dụ đã xóa dòng khác ở tab khác). Không gọi DAO, không tạo thực thể.
     */
    private void removeLine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!hasSupplier(request, response, session)) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<StockInSlipDetail1> detailList = (List<StockInSlipDetail1>) session.getAttribute("detailList");

        int index = -1;
        int documentId = -1;
        try {
            index = Integer.parseInt(trim(request.getParameter("index")));
            documentId = Integer.parseInt(trim(request.getParameter("documentId")));
        } catch (NumberFormatException e) {
            index = -1;
        }

        StockInSlipDetail1 removed = null;
        if (detailList != null && index >= 0 && index < detailList.size()
                && detailList.get(index).getDocument().getId() == documentId) {
            removed = detailList.remove(index);
        }
        request.setAttribute("message", removed == null
                ? "Không tìm thấy dòng cần xóa (phiếu đã thay đổi), hãy thử lại."
                : "Đã xóa dòng " + (index + 1) + ": " + removed.getDocument().getTitle());
        forwardSlip(request, response, session);
    }

    // ------------------------------------------------------------------
    // Submit: lưu phiếu nhập -> sinh bản sao -> lấy hóa đơn
    // ------------------------------------------------------------------

    /** Bước 60-81. */
    private void submit(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession();
        if (!hasSupplier(request, response, session)) {
            return;
        }
        Supplier1 supplier = (Supplier1) session.getAttribute("supplier");
        Librarian1 librarian = (Librarian1) session.getAttribute("librarian"); // nhân viên đã đăng nhập
        @SuppressWarnings("unchecked")
        List<StockInSlipDetail1> detailList = (List<StockInSlipDetail1>) session.getAttribute("detailList");

        if (detailList == null || detailList.isEmpty()) {
            request.setAttribute("message", "Chưa có tài liệu nào trong phiếu nhập.");
            forwardSlip(request, response, session);
            return;
        }

        // Bước 61-63: Servlet -> thực thể StockInSlip1 (đóng gói supplier, librarian, detailList)
        StockInSlip1 stockInSlip = new StockInSlip1(supplier, librarian, detailList);
        // Bước 64-66: Servlet -> StockInSlipDAO1.createStockInSlip(stockInSlip)
        stockInSlipDAO.createStockInSlip(stockInSlip);

        // Bước 67-69: Servlet -> thực thể DocumentCopy1 (sinh bản sao theo quantity của từng chi tiết)
        Date today = new Date(System.currentTimeMillis());
        List<DocumentCopy1> copyList = new ArrayList<DocumentCopy1>();
        int line = 0;
        for (StockInSlipDetail1 detail : stockInSlip.getDetails()) {
            line++;
            for (int i = 1; i <= detail.getQuantity(); i++) {
                // số dòng nằm trong mã nên hai dòng cùng tài liệu vẫn không trùng mã bản sao
                String copyCode = String.format("BS-%s-%d-%s-%03d", stockInSlip.getStockInCode(), line,
                        detail.getDocument().getDocumentCode(), i);
                copyList.add(new DocumentCopy1(copyCode, DocumentCopy1.NEW_CONDITION,
                        DocumentCopy1.AVAILABLE, today, detail.getDocument()));
            }
        }
        // Bước 70-72: Servlet -> DocumentCopyDAO1.generateCopies(copyList)
        documentCopyDAO.generateCopies(copyList);

        // Bước 73-79: Servlet -> StockInSlipDAO1.getInvoiceInfo(stockInSlipCode)
        StockInSlip1 invoice = stockInSlipDAO.getInvoiceInfo(stockInSlip.getStockInCode());

        session.setAttribute("invoice", invoice);
        session.removeAttribute("detailList");
        session.removeAttribute("currentDocument");
        session.removeAttribute("supplier");

        request.setAttribute("stockInSlip", invoice);
        forward(request, response, "InvoiceScreen1.jsp");
    }

    /** Bước 83-91: LƯU dữ liệu - xác nhận thanh toán. */
    private void confirmPayment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession();
        String stockInSlipCode = trim(request.getParameter("stockInSlipCode"));
        String paymentMethod = trim(request.getParameter("paymentMethod"));

        StockInSlip1 stockInSlip = (StockInSlip1) session.getAttribute("invoice");
        if (stockInSlip == null || !stockInSlip.getStockInCode().equals(stockInSlipCode)) {
            request.setAttribute("message", "Không tìm thấy phiếu nhập cần thanh toán.");
            forward(request, response, "index.jsp");
            return;
        }
        request.setAttribute("stockInSlip", stockInSlip);
        if (!"Tiền mặt".equals(paymentMethod) && !"Chuyển khoản".equals(paymentMethod)
                && !"Công nợ".equals(paymentMethod)) {
            request.setAttribute("message", "Hãy chọn hình thức thanh toán hợp lệ.");
            forward(request, response, "InvoiceScreen1.jsp");
            return;
        }

        // Bước 84-86: Servlet -> thực thể StockInSlip1 cập nhật thông tin thanh toán
        stockInSlip.setPaymentMethod(paymentMethod);
        stockInSlip.setPaymentStatus("Công nợ".equals(paymentMethod)
                ? StockInSlip1.UNPAID : StockInSlip1.PAID);
        // Bước 87-89: Servlet -> StockInSlipDAO1.confirmPayment(stockInSlip)
        boolean saved = stockInSlipDAO.confirmPayment(stockInSlip);

        request.setAttribute("saved", saved);
        request.setAttribute("message", saved
                ? "Nhập tài liệu thành công! Phiếu " + stockInSlip.getStockInCode()
                        + " - trạng thái: " + stockInSlip.getPaymentStatus() + "."
                : "Không thể ghi nhận thanh toán.");
        forward(request, response, "InvoiceScreen1.jsp");
    }

    // ------------------------------------------------------------------
    // Hàm hỗ trợ
    // ------------------------------------------------------------------

    private boolean hasSupplier(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        if (session.getAttribute("supplier") == null) {
            request.setAttribute("message", "Hãy chọn nhà cung cấp trước.");
            forward(request, response, "SupplierSearchScreen1.jsp");
            return false;
        }
        return true;
    }

    /** Vị trí dòng GẦN NHẤT của tài liệu trong detailList (-1 nếu tài liệu chưa có dòng nào). */
    private int findLastLineIndex(HttpSession session, Document1 document) {
        @SuppressWarnings("unchecked")
        List<StockInSlipDetail1> detailList = (List<StockInSlipDetail1>) session.getAttribute("detailList");
        if (detailList == null || document == null) {
            return -1;
        }
        for (int i = detailList.size() - 1; i >= 0; i--) {
            if (detailList.get(i).getDocument().getId() == document.getId()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Forward về StockInSlipScreen1.jsp. Nếu tài liệu đang chọn (currentDocument) đã có dòng trong phiếu,
     * đính kèm duplicateLine và duplicateLineNo để trang hỏi: cộng dồn hay thêm dòng riêng.
     */
    private void forwardSlip(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        Document1 current = (Document1) session.getAttribute("currentDocument");
        int index = findLastLineIndex(session, current);
        if (index >= 0) {
            @SuppressWarnings("unchecked")
            List<StockInSlipDetail1> detailList = (List<StockInSlipDetail1>) session.getAttribute("detailList");
            request.setAttribute("duplicateLine", detailList.get(index));
            request.setAttribute("duplicateLineNo", index + 1);
        }
        forward(request, response, "StockInSlipScreen1.jsp");
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private int parseIntOrZero(String s) {
        return trim(s).isEmpty() ? 0 : Integer.parseInt(trim(s));
    }

    private double parseDoubleOrZero(String s) {
        return trim(s).isEmpty() ? 0 : Double.parseDouble(trim(s));
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String page)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }
}
