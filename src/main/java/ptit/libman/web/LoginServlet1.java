package ptit.libman.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ptit.libman.dao.MemberDAO1;
import ptit.libman.model.Librarian1;
import ptit.libman.model.Reader1;

@WebServlet("/login")
public class LoginServlet1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MemberDAO1 memberDAO;

    @Override
    public void init() {
        memberDAO = new MemberDAO1();
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
                case "login":
                    login(request, response);
                    break;
                case "logout":
                    logout(request, response);
                    break;
                default:
                    forward(request, response, "LoginScreen1.jsp");
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    /** Kiểm tra tài khoản; đúng thì lưu nhân viên vào session, sai thì báo lỗi và giữ tên đăng nhập. */
    private void login(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password") == null ? "" : request.getParameter("password");

        if (username.isEmpty() || password.isEmpty()) {
            request.setAttribute("message", "Hãy nhập tên đăng nhập và mật khẩu.");
            forward(request, response, "LoginScreen1.jsp");
            return;
        }

        // 1. Thử kiểm tra xem có phải Nhân viên thư viện không
        Librarian1 librarian = memberDAO.loginLibrarian(username, password);
        if (librarian != null) {
            HttpSession session = request.getSession();
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("librarian", librarian); // Lưu session nhân viên
            response.sendRedirect(request.getContextPath() + "/SupplierSearchScreen1.jsp");
            return;
        }

        // 2. Thử kiểm tra xem có phải Bạn đọc không
        Reader1 reader = memberDAO.loginReader(username, password);
        if (reader != null) {
            HttpSession session = request.getSession();
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("reader", reader); // Lưu session bạn đọc
            response.sendRedirect(request.getContextPath() + "/DocumentSearchScreen1.jsp");
            return;
        }

        // 3. Nếu cả hai đều không khớp
        request.setAttribute("message", "Sai tên đăng nhập hoặc mật khẩu.");
        forward(request, response, "LoginScreen1.jsp");
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/LoginScreen1.jsp?loggedOut=1");
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String page)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }
}
