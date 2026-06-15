package edu.univ.erp.data;

import edu.univ.erp.domain.CatalogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogStore {

    private static final Logger log = LoggerFactory.getLogger(CatalogStore.class);

    private final DataSource dataSource;

    public CatalogStore(@Qualifier("erpDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<CatalogItem> getCatalogItems() {
        List<CatalogItem> catalog = new ArrayList<>();
        String query = "SELECT s.section_id, c.code, c.title, c.credits, s.capacity, a.username, s.semester, s.year " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.code " +
                "JOIN instructors i ON s.instructor_id = i.user_id " +
                "JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                "ORDER BY s.year, s.semester, c.code";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                catalog.add(new CatalogItem(
                        rs.getInt("section_id"), rs.getString("code"), rs.getString("title"),
                        rs.getInt("credits"), rs.getString("username"),
                        rs.getInt("capacity"), rs.getString("semester"), rs.getInt("year")));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch catalog items", e);
        }
        return catalog;
    }
}
