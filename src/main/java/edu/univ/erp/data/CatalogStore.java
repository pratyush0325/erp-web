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
    private String dbPassword = "prabhi12";

    /**
     * Fetches the complete course catalog by joining sections, courses,
     * instructors, and the auth database for instructor names.
     *
     * @return A list of CatalogItem objects.
     */
    public List<CatalogItem> getCatalogItems() {
        List<CatalogItem> catalog = new ArrayList<>();

        // UPDATED QUERY:
        // 1. Added s.semester, s.year to SELECT
        // 2. Removed "WHERE s.semester = 'Fall'" so you can see ALL open sections
        String query = "SELECT s.section_id, c.code, c.title, c.credits, s.capacity, a.username, s.semester, s.year " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.code " +
                "JOIN instructors i ON s.instructor_id = i.user_id " +
                "JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                "ORDER BY s.year, s.semester, c.code";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CatalogItem item = new CatalogItem(
                        rs.getInt("section_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("username"),
                        rs.getInt("capacity"),
                        rs.getString("semester"), // <--- New
                        rs.getInt("year")         // <--- New
                );
                catalog.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalog;
    }
}