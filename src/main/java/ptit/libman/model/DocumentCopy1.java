package ptit.libman.model;

import java.sql.Date;


public class DocumentCopy1 {
    public static final String AVAILABLE = "Có sẵn";
    public static final String BORROWED = "Đang được mượn";
    public static final String NEW_CONDITION = "Mới";

    private int id;
    private String copyCode;
    private String condition;
    private String availabilityStatus;
    private Date importDate;
    private Document1 document;

    public DocumentCopy1() {}

    /** Constructor dùng khi SINH bản sao mới (generateCopies). */
    public DocumentCopy1(String copyCode, String condition, String availabilityStatus,
                        Date importDate, Document1 document) {
        this.copyCode = copyCode;
        this.condition = condition;
        this.availabilityStatus = availabilityStatus;
        this.importDate = importDate;
        this.document = document;
    }

    /** Constructor đầy đủ, dùng khi DAO đọc dữ liệu từ CSDL. */
    public DocumentCopy1(int id, String copyCode, String condition, String availabilityStatus,
                        Date importDate) {
        this.id = id;
        this.copyCode = copyCode;
        this.condition = condition;
        this.availabilityStatus = availabilityStatus;
        this.importDate = importDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCopyCode() { return copyCode; }
    public void setCopyCode(String copyCode) { this.copyCode = copyCode; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public Date getImportDate() { return importDate; }
    public void setImportDate(Date importDate) { this.importDate = importDate; }
    public Document1 getDocument() { return document; }
    public void setDocument(Document1 document) { this.document = document; }
}
