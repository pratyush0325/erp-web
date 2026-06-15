package edu.univ.erp.data;

import edu.univ.erp.domain.StudentProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ProfileStore {

    private static final Logger log = LoggerFactory.getLogger(ProfileStore.class);

    private final DataSource dataSource;

    public ProfileStore(@Qualifier("erpDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public StudentProfile getStudentProfile(int userId) {
        String query = "SELECT a.username, s.roll_no, s.program, s.year " +
                "FROM students s " +
                "JOIN auth_db.users_auth a ON s.user_id = a.user_id " +
                "WHERE s.user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentProfile(
                            rs.getString("username"), rs.getString("roll_no"),
                            rs.getString("program"), rs.getInt("year"));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to fetch profile for userId={}", userId, e);
        }
        return null;
    }
}
