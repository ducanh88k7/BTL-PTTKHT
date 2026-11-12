package ptit.libman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ptit.libman.model.Supplier1;


public class SupplierDAO1 extends DAO {

    private static final String SEARCH_BY_NAME =
            "SELECT id, supplierCode, name, address, phone, email, taxCode, description "
                    + "FROM supplier1 WHERE name LIKE ? ORDER BY name";
    private static final String INSERT_SUPPLIER =
            "INSERT INTO supplier1 (supplierCode, name, address, phone, email, taxCode, description) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_CODE = "UPDATE supplier1 SET supplierCode = ? WHERE id = ?";

    public SupplierDAO1() {}

    /**
     * searchByName(keyword) - LẤY dữ liệu:
     * DAO duyệt ResultSet và gọi lớp Supplier1 đóng gói thông tin (new Supplier1(...)).
     */
    public List<Supplier1> searchByName(String keyword) throws SQLException {
        List<Supplier1> suppliers = new ArrayList<Supplier1>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SEARCH_BY_NAME)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(new Supplier1(
                            rs.getInt("id"),
                            rs.getString("supplierCode"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("taxCode"),
                            rs.getString("description")));
                }
            }
        }
        return suppliers;
    }

    /**
     * addSupplier(supplier) - LƯU dữ liệu: nhận đối tượng Supplier1 đã được Servlet đóng gói.
     * Sau khi lưu, gán lại id và supplierCode (NCC0001, ...) vào đối tượng.
     */
    public boolean addSupplier(Supplier1 supplier) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            int newId = 0;
            try (PreparedStatement ps = connection.prepareStatement(INSERT_SUPPLIER, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "TMP" + System.nanoTime()); // mã tạm, đổi ngay sau khi có id
                ps.setString(2, supplier.getName());
                ps.setString(3, supplier.getAddress());
                ps.setString(4, supplier.getPhone());
                ps.setString(5, supplier.getEmail());
                ps.setString(6, supplier.getTaxCode());
                ps.setString(7, supplier.getDescription());
                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        newId = keys.getInt(1);
                    }
                }
            }

            String code = String.format("NCC%04d", newId);
            try (PreparedStatement ps = connection.prepareStatement(UPDATE_CODE)) {
                ps.setString(1, code);
                ps.setInt(2, newId);
                ps.executeUpdate();
            }
            connection.commit();

            supplier.setId(newId);
            supplier.setSupplierCode(code);
            return true;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(connection);
        }
    }
}
