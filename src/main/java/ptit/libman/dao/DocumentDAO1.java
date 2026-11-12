package ptit.libman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ptit.libman.model.Document1;
import ptit.libman.model.DocumentType1;


public class DocumentDAO1 extends DAO {

    private static final String SELECT_DOCUMENT =
            "SELECT d.id, d.documentCode, d.title, d.author, d.publisher, d.publishYear, "
                    + "d.location, d.price, d.description, "
                    + "t.id AS typeId, t.typeCode, t.typeName, t.description AS typeDescription "
                    + "FROM document1 d LEFT JOIN documenttype1 t ON d.documentTypeId = t.id ";
    private static final String SEARCH_BY_KEYWORD = SELECT_DOCUMENT + "WHERE d.title LIKE ? ORDER BY d.title";
    private static final String SELECT_BY_CODE = SELECT_DOCUMENT + "WHERE d.documentCode = ?";
    /** findByCode không cần loại tài liệu nên không nối bảng documenttype1. */
    private static final String SELECT_BASIC_BY_CODE =
            "SELECT id, documentCode, title, author, publisher, publishYear, location, price, description "
                    + "FROM document1 WHERE documentCode = ?";
    private static final String INSERT_DOCUMENT =
            "INSERT INTO document1 (documentCode, title, author, publisher, publishYear, location, "
                    + "price, description, documentTypeId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public DocumentDAO1() {}

    /** searchByKeyword(keyword) - LẤY dữ liệu: danh sách tài liệu có tên chứa từ khóa. */
    public List<Document1> searchByKeyword(String keyword) throws SQLException {
        List<Document1> documents = new ArrayList<Document1>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SEARCH_BY_KEYWORD)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    documents.add(mapDocument(rs)); // gọi lớp Document1 đóng gói
                }
            }
        }
        return documents;
    }

    /** getDocumentDetail(documentCode) - LẤY dữ liệu: chi tiết một tài liệu. */
    public Document1 getDocumentDetail(String documentCode) throws SQLException {
        return selectByCode(documentCode);
    }

    /**
     * findByCode(documentCode) - LẤY dữ liệu: tìm tài liệu theo mã quét/nhập (null nếu không có).
     * Chỉ đóng gói lớp Document1 (documentType = null), đúng sequence module Nhập tài liệu.
     */
    public Document1 findByCode(String documentCode) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BASIC_BY_CODE)) {
            ps.setString(1, documentCode == null ? "" : documentCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Document1(
                            rs.getInt("id"),
                            rs.getString("documentCode"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("publisher"),
                            rs.getInt("publishYear"),
                            rs.getString("location"),
                            rs.getDouble("price"),
                            rs.getString("description"),
                            null);
                }
            }
        }
        return null;
    }

    /**
     * addDocument(document) - LƯU dữ liệu: nhận đối tượng Document1 đã được Servlet đóng gói.
     * @return false nếu mã tài liệu bị trùng (hoặc loại tài liệu không hợp lệ)
     */
    public boolean addDocument(Document1 document) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_DOCUMENT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, document.getDocumentCode());
            ps.setString(2, document.getTitle());
            ps.setString(3, document.getAuthor());
            ps.setString(4, document.getPublisher());
            ps.setInt(5, document.getPublishYear());
            ps.setString(6, document.getLocation());
            ps.setDouble(7, document.getPrice());
            ps.setString(8, document.getDescription());
            ps.setInt(9, document.getDocumentType().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    document.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) { // trùng khóa UNIQUE / sai khóa ngoại
                return false;
            }
            throw e;
        }
    }

    private Document1 selectByCode(String documentCode) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_CODE)) {
            ps.setString(1, documentCode == null ? "" : documentCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDocument(rs);
                }
            }
        }
        return null;
    }

    /** Gọi lớp Document1 (và DocumentType1) đóng gói một dòng ResultSet. */
    private Document1 mapDocument(ResultSet rs) throws SQLException {
        int typeId = rs.getInt("typeId");
        DocumentType1 type = null;
        if (!rs.wasNull()) {
            type = new DocumentType1(typeId, rs.getString("typeCode"),
                    rs.getString("typeName"), rs.getString("typeDescription"));
        }
        return new Document1(
                rs.getInt("id"),
                rs.getString("documentCode"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher"),
                rs.getInt("publishYear"),
                rs.getString("location"),
                rs.getDouble("price"),
                rs.getString("description"),
                type);
    }
}
