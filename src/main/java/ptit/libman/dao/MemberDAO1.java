package ptit.libman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ptit.libman.model.Librarian1;
import ptit.libman.model.Reader1;

public class MemberDAO1 extends DAO {

    private static final String LOGIN_LIBRARIAN =
            "SELECT m.id, m.username, m.fullName, l.staffCode, l.position "
                    + "FROM member1 m JOIN librarian1 l ON l.id = m.id "
                    + "WHERE m.username = ? AND m.password = ?";

    private static final String LOGIN_READER =
            "SELECT m.id, m.username, m.fullName, r.readerCode, r.readerType, r.cardStatus "
                    + "FROM member1 m JOIN reader1 r ON r.id = m.id "
                    + "WHERE m.username = ? AND m.password = ?";

    public MemberDAO1() {}

    public Librarian1 loginLibrarian(String username, String password) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(LOGIN_LIBRARIAN)) {
            ps.setString(1, username);
            ps.setString(2, password); // Không dùng hàm hash() nữa, so sánh trực tiếp
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Librarian1(rs.getInt("id"), rs.getString("username"), rs.getString("fullName"),
                            rs.getString("staffCode"), rs.getString("position"));
                }
            }
        }
        return null;
    }

    public Reader1 loginReader(String username, String password) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(LOGIN_READER)) {
            ps.setString(1, username);
            ps.setString(2, password); // So sánh trực tiếp mật khẩu thô
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reader1 reader = new Reader1();
                    reader.setId(rs.getInt("id"));
                    reader.setUsername(rs.getString("username"));
                    reader.setFullName(rs.getString("fullName"));
                    reader.setReaderCode(rs.getString("readerCode"));
                    reader.setReaderType(rs.getString("readerType"));
                    reader.setCardStatus(rs.getString("cardStatus"));
                    return reader;
                }
            }
        }
        return null;
    }
}