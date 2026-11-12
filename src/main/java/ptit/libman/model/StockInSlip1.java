package ptit.libman.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/** StockInSlip1.java - lớp thực thể Phiếu nhập (StockInSlip1). */
public class StockInSlip1 {
    public static final String UNPAID = "Chưa thanh toán";
    public static final String PAID = "Đã thanh toán";

    private int id;
    private String stockInCode;
    private Date importDate;
    private double totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private Supplier1 supplier;
    private Librarian1 librarian;
    private List<StockInSlipDetail1> details = new ArrayList<StockInSlipDetail1>();

    public StockInSlip1() {}


    public StockInSlip1(Supplier1 supplier, Librarian1 librarian, List<StockInSlipDetail1> details) {
        this.supplier = supplier;
        this.librarian = librarian;
        this.details = details;
        this.importDate = new Date(System.currentTimeMillis());
        this.paymentStatus = UNPAID;
        double total = 0;
        for (StockInSlipDetail1 d : details) {
            total += d.getAmount();
        }
        this.totalAmount = total;
    }

    /** Constructor đầy đủ (không có chi tiết), dùng khi DAO đọc dữ liệu từ CSDL. */
    public StockInSlip1(int id, String stockInCode, Date importDate, double totalAmount,
                       String paymentStatus, String paymentMethod,
                       Supplier1 supplier, Librarian1 librarian) {
        this.id = id;
        this.stockInCode = stockInCode;
        this.importDate = importDate;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.supplier = supplier;
        this.librarian = librarian;
    }

    public void addDetail(StockInSlipDetail1 detail) { this.details.add(detail); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStockInCode() { return stockInCode; }
    public void setStockInCode(String stockInCode) { this.stockInCode = stockInCode; }
    public Date getImportDate() { return importDate; }
    public void setImportDate(Date importDate) { this.importDate = importDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public Supplier1 getSupplier() { return supplier; }
    public void setSupplier(Supplier1 supplier) { this.supplier = supplier; }
    public Librarian1 getLibrarian() { return librarian; }
    public void setLibrarian(Librarian1 librarian) { this.librarian = librarian; }
    public List<StockInSlipDetail1> getDetails() { return details; }
    public void setDetails(List<StockInSlipDetail1> details) { this.details = details; }
}
