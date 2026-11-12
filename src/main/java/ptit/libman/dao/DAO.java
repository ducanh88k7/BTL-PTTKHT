package ptit.libman.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DAO {
    private static final String JDBC_DRIVER = System.getProperty("libman.jdbc.driver", "com.mysql.cj.jdbc.Driver");
    private static final String JDBC_URL = System.getProperty("libman.jdbc.url",
            "jdbc:mysql://localhost:3306/libman?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
    private static final String JDBC_USERNAME = System.getProperty("libman.jdbc.user", "root");
    private static final String JDBC_PASSWORD = System.getProperty("libman.jdbc.password", "ducanh88k7");

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
    }

    /** Hủy giao dịch, không ném lỗi thêm. */
    protected void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
                // bỏ qua
            }
        }
    }

    /** Trả connection về chế độ auto-commit rồi đóng. */
    protected void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException ignore) {
                // bỏ qua
            }
        }
    }
}
