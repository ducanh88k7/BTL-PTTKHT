package ptit.libman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ptit.libman.model.DocumentCopy1;


public class DocumentCopyDAO1 extends DAO {

    private static final String SELECT_AVAILABLE =
            "SELECT c.id, c.copyCode, c.conditionStatus, c.availabilityStatus, c.importDate "
                    + "FROM documentcopy1 c JOIN document1 d ON c.documentId = d.id "
                    + "WHERE d.documentCode = ? AND c.availabilityStatus = ?";
    private static final String SELECT_BY_DOCUMENT =
            "SELECT c.id, c.copyCode, c.conditionStatus, c.availabilityStatus, c.importDate "
                    + "FROM documentcopy1 c JOIN document1 d ON c.documentId = d.id "
                    + "WHERE d.documentCode = ? ORDER BY c.copyCode";
    private static final String INSERT_COPY =
            "INSERT INTO documentcopy1 (copyCode, conditionStatus, availabilityStatus, importDate, documentId) "
                    + "VALUES (?, ?, ?, ?, ?)";

    public DocumentCopyDAO1() {}

    /**
     * countAvailable(documentCode) - LẤY dữ liệu: đếm số bản sao đang "Có sẵn" của một tài liệu.
     * Đúng sequence: DAO gọi lớp DocumentCopy1 đóng gói từng bản sao rồi đếm.
     */
    public int countAvailable(String documentCode) throws SQLException {
        List<DocumentCopy1> copies = new ArrayList<DocumentCopy1>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_AVAILABLE)) {
            ps.setString(1, documentCode);
            ps.setString(2, DocumentCopy1.AVAILABLE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    copies.add(new DocumentCopy1(
                            rs.getInt("id"),
                            rs.getString("copyCode"),
                            rs.getString("conditionStatus"),
                            rs.getString("availabilityStatus"),
                            rs.getDate("importDate")));
                }
            }
        }
        return copies.size();
    }

    /**
     * getCopiesByDocument(documentCode) - LẤY dữ liệu: danh sách TẤT CẢ bản sao của một tài liệu
     * (mã bản sao, tình trạng, trạng thái sẵn có) để hiển thị ở trang chi tiết tài liệu.
     * Mỗi dòng kết quả: DAO gọi lớp DocumentCopy1 đóng gói thông tin rồi thêm vào danh sách trả về.
     */
    public List<DocumentCopy1> getCopiesByDocument(String documentCode) throws SQLException {
        List<DocumentCopy1> copies = new ArrayList<DocumentCopy1>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_DOCUMENT)) {
            ps.setString(1, documentCode == null ? "" : documentCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    copies.add(new DocumentCopy1(
                            rs.getInt("id"),
                            rs.getString("copyCode"),
                            rs.getString("conditionStatus"),
                            rs.getString("availabilityStatus"),
                            rs.getDate("importDate")));
                }
            }
        }
        return copies;
    }

    /**
     * generateCopies(copyList) - LƯU dữ liệu: nhận danh sách DocumentCopy1 đã được Servlet đóng gói.
     * Lưu tất cả trong một giao dịch (hoặc lưu hết, hoặc không lưu gì).
     */
    public boolean generateCopies(List<DocumentCopy1> copyList) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement(INSERT_COPY)) {
                for (DocumentCopy1 copy : copyList) {
                    ps.setString(1, copy.getCopyCode());
                    ps.setString(2, copy.getCondition());
                    ps.setString(3, copy.getAvailabilityStatus());
                    ps.setDate(4, copy.getImportDate());
                    ps.setInt(5, copy.getDocument().getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(connection);
        }
    }
}
