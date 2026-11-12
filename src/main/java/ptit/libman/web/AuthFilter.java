package ptit.libman.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {
        // Các URL của Nhân viên thư viện (Nhập tài liệu)
        "/stockin", "/SupplierSearchScreen1.jsp", "/SupplierAddScreen1.jsp",
        "/StockInSlipScreen1.jsp", "/DocumentAddScreen1.jsp", "/InvoiceScreen1.jsp",
        // Các URL của Bạn đọc (Tìm tài liệu & xem chi tiết)
        "/document", "/DocumentSearchScreen1.jsp", "/DocumentDetailScreen1.jsp"
})
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String requestURI = req.getRequestURI();

        boolean isLoggedIn = (session != null &&
                (session.getAttribute("librarian") != null || session.getAttribute("reader") != null));

        if (!isLoggedIn) {
            // Nếu chưa đăng nhập bất kỳ tài khoản nào, đá về trang Login kèm thông báo
            res.sendRedirect(req.getContextPath() + "/LoginScreen1.jsp?required=1");
            return;
        }

        // Tùy chọn nâng cao: Phân quyền chéo (Ví dụ: Bạn đọc không được vào trang Nhập tài liệu của Librarian)
        if (requestURI.contains("Supplier") || requestURI.contains("StockIn") || requestURI.contains("Invoice") || requestURI.contains("stockin")) {
            if (session.getAttribute("librarian") == null) {
                // Bạn đọc cố tình truy cập trang của nhân viên -> Chặn hoặc báo lỗi
                res.sendRedirect(req.getContextPath() + "/DocumentSearchScreen1.jsp");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}