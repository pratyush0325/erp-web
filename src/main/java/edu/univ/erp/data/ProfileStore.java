package edu.univ.erp.data;

import edu.univ.erp.domain.StudentProfile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileStore {

    private String dbUrl = "jdbc:mysql://localhost:3306/erp_db";
    private String dbUser = "root";
    private String dbPassword = "prabhi12";

    public StudentProfile getStudentProfile(int userId) {
        // Join ERP DB (students) with Auth DB (users_auth) to get the name
        String query = "SELECT a.username, s.roll_no, s.program, s.year " +
                "FROM students s " +
                "JOIN auth_db.users_auth a ON s.user_id = a.user_id " +
                "WHERE s.user_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentProfile(
                            rs.getString("username"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}