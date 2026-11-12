package ptit.libman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ptit.libman.model.Document1;
import ptit.libman.model.Librarian1;
import ptit.libman.model.StockInSlip1;
import ptit.libman.model.StockInSlipDetail1;
import ptit.libman.model.Supplier1;


public class StockInSlipDAO1 extends DAO {

    private static final String INSERT_SLIP =
            "INSERT INTO stockinslip1 (stockInCode, importDate, totalAmount, paymentStatus, "
                    + "paymentMethod, supplierId, librarianId) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SLIP_CODE = "UPDATE stockinslip1 SET stockInCode = ? WHERE id = ?";
    private static final String INSERT_DETAIL =
            "INSERT INTO stockinslipdetail1 (stockInSlipId, quantity, unitPrice, documentId) VALUES (?, ?, ?, ?)";

    private static final String SELECT_SLIP =
            "SELECT s.id, s.stockInCode, s.importDate, s.totalAmount, s.paymentStatus, s.paymentMethod, "
                    + "p.id AS supplierId, p.supplierCode, p.name, p.address, p.phone, p.email, p.taxCode, "
                    + "p.description AS supplierDescription, "
                    + "l.id AS librarianId, l.staffCode, l.position, m.username AS librarianUsername, m.fullName AS librarianName "
                    + "FROM stockinslip1 s "
                    + "JOIN supplier1 p ON s.supplierId = p.id "
                    + "JOIN librarian1 l ON s.librarianId = l.id "
                    + "JOIN member1 m ON m.id = l.id "
                    + "WHERE s.stockInCode = ?";
    private static final String SELECT_DETAILS =
            "SELECT sd.id, sd.quantity, sd.unitPrice, d.id AS documentId, d.documentCode, d.title "
                    + "FROM stockinslipdetail1 sd JOIN document1 d ON sd.documentId = d.id "
                    + "WHERE sd.stockInSlipId = ? ORDER BY sd.id";
    private static final String UPDATE_PAYMENT =
            "UPDATE stockinslip1 SET paymentMethod = ?, paymentStatus = ? WHERE stockInCode = ?";

    public StockInSlipDAO1() {}

    /**
     * createStockInSlip(stockInSlip) - LƯU dữ liệu: nhận đối tượng StockInSlip1 (đã chứa supplier,
     * librarian, detailList) được Servlet đóng gói. Lưu phiếu và TẤT CẢ chi tiết trong một giao dịch.
     * Sau khi lưu, gán lại id và stockInCode (PN000001, ...) vào đối tượng.
     */
    public boolean createStockInSlip(StockInSlip1 slip) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            int slipId = 0;
            try (PreparedStatement ps = connection.prepareStatement(INSERT_SLIP, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "TMP" + System.nanoTime()); // mã tạm, đổi ngay sau khi có id
                ps.setDate(2, slip.getImportDate());
                ps.setDouble(3, slip.getTotalAmount());
                ps.setString(4, slip.getPaymentStatus());
                ps.setString(5, slip.getPaymentMethod());
                ps.setInt(6, slip.getSupplier().getId());
                ps.setInt(7, slip.getLibrarian().getId());
                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        slipId = keys.getInt(1);
                    }
                }
            }

            String code = String.format("PN%06d", slipId);
            try (PreparedStatement ps = connection.prepareStatement(UPDATE_SLIP_CODE)) {
                ps.setString(1, code);
                ps.setInt(2, slipId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(INSERT_DETAIL)) {
                for (StockInSlipDetail1 detail : slip.getDetails()) {
                    ps.setInt(1, slipId);
                    ps.setInt(2, detail.getQuantity());
                    ps.setDouble(3, detail.getUnitPrice());
                    ps.setInt(4, detail.getDocument().getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            connection.commit();

            slip.setId(slipId);
            slip.setStockInCode(code);
            return true;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(connection);
        }
    }

    /**
     * getInvoiceInfo(stockInSlipCode) - LẤY dữ liệu: phiếu nhập đầy đủ (nhà cung cấp, thủ thư, chi tiết).
     * DAO gọi lớp StockInSlip1 đóng gói phiếu, rồi lấy các chi tiết (StockInSlipDetail1) gắn vào phiếu.
     */
    public StockInSlip1 getInvoiceInfo(String stockInSlipCode) throws SQLException {
        StockInSlip1 slip = null;
        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_SLIP)) {
                ps.setString(1, stockInSlipCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Supplier1 supplier = new Supplier1(
                                rs.getInt("supplierId"), rs.getString("supplierCode"), rs.getString("name"),
                                rs.getString("address"), rs.getString("phone"), rs.getString("email"),
                                rs.getString("taxCode"), rs.getString("supplierDescription"));
                        Librarian1 librarian = new Librarian1(
                                rs.getInt("librarianId"), rs.getString("librarianUsername"), rs.getString("librarianName"),
                                rs.getString("staffCode"), rs.getString("position"));
                        slip = new StockInSlip1(
                                rs.getInt("id"), rs.getString("stockInCode"), rs.getDate("importDate"),
                                rs.getDouble("totalAmount"), rs.getString("paymentStatus"),
                                rs.getString("paymentMethod"), supplier, librarian);
                    }
                }
            }
            if (slip == null) {
                return null;
            }
            // StockInSlip1 lấy các StockInSlipDetail1 của phiếu (bước StockInSlip1 -> StockInSlipDetail1)
            try (PreparedStatement ps = connection.prepareStatement(SELECT_DETAILS)) {
                ps.setInt(1, slip.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Document1 document = new Document1(
                                rs.getInt("documentId"), rs.getString("documentCode"), rs.getString("title"));
                        slip.addDetail(new StockInSlipDetail1(
                                rs.getInt("id"), document, rs.getInt("quantity"), rs.getDouble("unitPrice")));
                    }
                }
            }
        }
        return slip;
    }

    /** confirmPayment(stockInSlip) - LƯU dữ liệu: ghi hình thức và trạng thái thanh toán. */
    public boolean confirmPayment(StockInSlip1 slip) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_PAYMENT)) {
            ps.setString(1, slip.getPaymentMethod());
            ps.setString(2, slip.getPaymentStatus());
            ps.setString(3, slip.getStockInCode());
            return ps.executeUpdate() > 0;
        }
    }
}
