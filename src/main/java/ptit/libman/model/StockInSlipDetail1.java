package ptit.libman.model;


public class StockInSlipDetail1 {
    private int id;
    private int quantity;
    private double unitPrice;
    private Document1 document;

    public StockInSlipDetail1() {}

    /** StockInSlipDetail1(document, quantity, unitPrice): đóng gói một dòng chi tiết. */
    public StockInSlipDetail1(Document1 document, int quantity, double unitPrice) {
        this.document = document;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /** Constructor đầy đủ, dùng khi DAO đọc dữ liệu từ CSDL. */
    public StockInSlipDetail1(int id, Document1 document, int quantity, double unitPrice) {
        this.id = id;
        this.document = document;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /** Thành tiền của dòng = số lượng x đơn giá (dùng cho JSP: ${d.amount}). */
    public double getAmount() { return quantity * unitPrice; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public Document1 getDocument() { return document; }
    public void setDocument(Document1 document) { this.document = document; }
}
