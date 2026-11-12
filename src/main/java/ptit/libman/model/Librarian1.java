package ptit.libman.model;

public class Librarian1 extends Member1 {
    private String staffCode;
    private String position;

    public Librarian1() {}

    public Librarian1(int id, String username, String fullName, String staffCode, String position) {
        super(id, username, fullName);
        this.staffCode = staffCode;
        this.position = position;
    }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
