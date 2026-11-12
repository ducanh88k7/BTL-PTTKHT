package ptit.libman.model;

import java.util.Date;

public class Reader1 extends Member1 {
    private String readerCode;
    private String readerType;
    private Date cardIssueDate;
    private Date cardExpiryDate;
    private String cardStatus;

    public Reader1() {
        super();
    }

    public Reader1(int id, String username, String fullName, String readerCode, String readerType, String cardStatus) {
        super(id, username, fullName);
        this.readerCode = readerCode;
        this.readerType = readerType;
        this.cardStatus = cardStatus;
    }

    // Getters and Setters
    public String getReaderCode() { return readerCode; }
    public void setReaderCode(String readerCode) { this.readerCode = readerCode; }

    public String getReaderType() { return readerType; }
    public void setReaderType(String readerType) { this.readerType = readerType; }

    public Date getCardIssueDate() { return cardIssueDate; }
    public void setCardIssueDate(Date cardIssueDate) { this.cardIssueDate = cardIssueDate; }

    public Date getCardExpiryDate() { return cardExpiryDate; }
    public void setCardExpiryDate(Date cardExpiryDate) { this.cardExpiryDate = cardExpiryDate; }

    public String getCardStatus() { return cardStatus; }
    public void setCardStatus(String cardStatus) { this.cardStatus = cardStatus; }
}