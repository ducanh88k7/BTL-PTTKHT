package ptit.libman.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ptit.libman.dao.DocumentCopyDAO1;
import ptit.libman.dao.DocumentDAO1;
import ptit.libman.model.Document1;
import ptit.libman.model.DocumentCopy1;

@WebServlet("/document")
public class DocumentServlet1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DocumentDAO1 documentDAO;
    private DocumentCopyDAO1 documentCopyDAO;

    @Override
    public void init() {
        documentDAO = new DocumentDAO1();
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
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "search":
                    searchDocument(request, response);
                    break;
                case "detail":
                    showDetail(request, response);
                    break;
                default:
                    forward(request, response, "DocumentSearchScreen1.jsp");
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    /** Bước 4-17 của sequence: tìm tài liệu theo từ khóa rồi đếm số bản sao còn sẵn của từng tài liệu. */
    private void searchDocument(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String keyword = request.getParameter("keyword");

        List<Document1> listDocument = documentDAO.searchByKeyword(keyword);

        Map<String, Integer> availableCount = new LinkedHashMap<String, Integer>();
        for (Document1 document : listDocument) {
            availableCount.put(document.getDocumentCode(),
                    documentCopyDAO.countAvailable(document.getDocumentCode()));
        }

        request.setAttribute("keyword", keyword);
        request.setAttribute("listDocument", listDocument);
        request.setAttribute("availableCount", availableCount);
        if (listDocument.isEmpty()) {
            request.setAttribute("message",
                    "Không tìm thấy tài liệu phù hợp với từ khóa '" + (keyword == null ? "" : keyword) + "'.");
        }
        forward(request, response, "DocumentSearchScreen1.jsp");
    }

    /**
     * Xem chi tiết: lấy thông tin tài liệu (DocumentDAO1) rồi lấy danh sách các bản sao của tài liệu
     * (DocumentCopyDAO1.getCopiesByDocument). Số "sẵn có X/Y" tính từ danh sách bản sao đó, nên
     * KHÔNG gọi lại countAvailable và không cần truyền số bản sao từ trang tìm kiếm.
     */
    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String documentCode = request.getParameter("documentCode");
        String keyword = request.getParameter("keyword");

        Document1 document = documentDAO.getDocumentDetail(documentCode);
        if (document == null) {
            request.setAttribute("message", "Không tìm thấy tài liệu có mã " + documentCode);
            forward(request, response, "DocumentSearchScreen1.jsp");
            return;
        }

        List<DocumentCopy1> copyList = documentCopyDAO.getCopiesByDocument(documentCode);
        int availableCount = 0;
        for (DocumentCopy1 copy : copyList) {
            if (DocumentCopy1.AVAILABLE.equals(copy.getAvailabilityStatus())) {
                availableCount++;
            }
        }

        request.setAttribute("document", document);
        request.setAttribute("copyList", copyList);
        request.setAttribute("availableCount", availableCount);
        request.setAttribute("totalCount", copyList.size());
        request.setAttribute("keyword", keyword);   // để nút "Quay lại kết quả" tìm lại đúng từ khóa
        forward(request, response, "DocumentDetailScreen1.jsp");
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String page)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }
}
