package ptit.libman.model;


public class DocumentType1 {
    private int id;
    private String typeCode;
    private String typeName;
    private String description;

    public DocumentType1() {}

    public DocumentType1(int id, String typeCode, String typeName, String description) {
        this.id = id;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
