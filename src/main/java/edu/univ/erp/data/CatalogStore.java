package edu.univ.erp.data;

import edu.univ.erp.domain.CatalogItem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogStore {

    // Connection details for the ERP database
    private String dbUrl = "jdbc:mysql://localhost:3306/erp_db";
    private String dbUser = "root";
    private String dbPassword = "password";

    /**
     * Fetches the complete course catalog by joining sections, courses,
     * instructors, and the auth database for instructor names.
     *
     * @return A list of CatalogItem objects.
     */
    public List<CatalogItem> getCatalogItems() {
        List<CatalogItem> catalog = new ArrayList<>();

        // This query joins across both databases to get the instructor's username
        // as per the schema
        String query = "SELECT s.section_id, c.code, c.title, c.credits, s.capacity, a.username " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.code " +
                "JOIN instructors i ON s.instructor_id = i.user_id " +
                "JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                "WHERE s.semester = 'Fall' AND s.year = 2025";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // v-- UPDATE THE CONSTRUCTOR CALL
                CatalogItem item = new CatalogItem(
                        rs.getInt("section_id"), // <-- ADD THIS
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("username"),
                        rs.getInt("capacity")
                );
                catalog.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., log them)
        }
        return catalog;
    }
}