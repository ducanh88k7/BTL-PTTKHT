package ptit.libman.model;


public class Document1 {
    private int id;
    private String documentCode;
    private String title;
    private String author;
    private String publisher;
    private int publishYear;
    private String location;
    private double price;
    private String description;
    private DocumentType1 documentType;

    public Document1() {}

    /** Constructor rút gọn: dùng khi DAO dựng tài liệu bên trong chi tiết phiếu nhập. */
    public Document1(int id, String documentCode, String title) {
        this.id = id;
        this.documentCode = documentCode;
        this.title = title;
    }

    /** Constructor dùng khi THÊM MỚI tài liệu (từ form DocumentAddScreen1). */
    public Document1(String documentCode, String title, String author, String publisher,
                    int publishYear, String location, double price, String description,
                    int documentTypeId) {
        this.documentCode = documentCode;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.location = location;
        this.price = price;
        this.description = description;
        this.documentType = new DocumentType1();
        this.documentType.setId(documentTypeId);
    }

    /** Constructor đầy đủ, dùng khi DAO đọc dữ liệu từ CSDL. */
    public Document1(int id, String documentCode, String title, String author, String publisher,
                    int publishYear, String location, double price, String description,
                    DocumentType1 documentType) {
        this.id = id;
        this.documentCode = documentCode;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.location = location;
        this.price = price;
        this.description = description;
        this.documentType = documentType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDocumentCode() { return documentCode; }
    public void setDocumentCode(String documentCode) { this.documentCode = documentCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public int getPublishYear() { return publishYear; }
    public void setPublishYear(int publishYear) { this.publishYear = publishYear; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public DocumentType1 getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType1 documentType) { this.documentType = documentType; }
}
