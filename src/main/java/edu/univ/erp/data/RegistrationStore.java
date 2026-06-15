package edu.univ.erp.data;

import edu.univ.erp.domain.RegistrationItem;
import edu.univ.erp.domain.StudentGradeView;
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
public class RegistrationStore {

    private static final Logger log = LoggerFactory.getLogger(RegistrationStore.class);

    private final DataSource dataSource;

    public RegistrationStore(@Qualifier("erpDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<RegistrationItem> findRegistrationsByStudentId(int studentId) {
        List<RegistrationItem> registrations = new ArrayList<>();
        String query = "SELECT s.section_id, c.code, c.title, s.day_time, s.room, e.status " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.code " +
                "WHERE e.student_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int sectionId = rs.getInt("section_id");
                    registrations.add(new RegistrationItem(
                            sectionId, rs.getString("code"), "SEC-" + sectionId,
                            rs.getString("title"), rs.getString("day_time"),
                            rs.getString("room"), rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to fetch registrations for studentId={}", studentId, e);
        }
        return registrations;
    }

    public boolean isAlreadyRegistered(int studentId, int sectionId) {
        String query = "SELECT 1 FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "WHERE e.student_id = ? " +
                "AND s.course_id = (SELECT course_id FROM sections WHERE section_id = ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Failed to check registration for student={} section={}", studentId, sectionId, e);
            return true; // fail-safe
        }
    }

    public int getSectionEnrollmentCount(int sectionId) {
        String query = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count enrollments for sectionId={}", sectionId, e);
        }
        return 0;
    }

    public int getSectionCapacity(int sectionId) {
        String query = "SELECT capacity FROM sections WHERE section_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("capacity");
            }
        } catch (SQLException e) {
            log.error("Failed to fetch capacity for sectionId={}", sectionId, e);
        }
        return 0;
    }

    public boolean createEnrollment(int studentId, int sectionId) {
        String query = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'registered')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to create enrollment for student={} section={}", studentId, sectionId, e);
            return false;
        }
    }

    public boolean deleteEnrollment(int studentId, int sectionId) {
        String query = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to delete enrollment for student={} section={}", studentId, sectionId, e);
            return false;
        }
    }

    public List<StudentGradeView> getGradesForStudent(int studentId) {
        List<StudentGradeView> grades = new ArrayList<>();
        String query = "SELECT c.code, c.title, ca.assignment_name, ss.score_obtained, ca.max_score, ca.weight_percent " +
                "FROM student_scores ss " +
                "JOIN course_assignments ca ON ss.assignment_id = ca.assignment_id " +
                "JOIN sections s ON ca.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.code " +
                "WHERE ss.student_id = ? " +
                "ORDER BY c.code, ca.assignment_name";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new StudentGradeView(
                            rs.getString("code"), rs.getString("title"),
                            rs.getString("assignment_name"), rs.getDouble("score_obtained"),
                            rs.getInt("max_score"), rs.getInt("weight_percent")));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to fetch grades for studentId={}", studentId, e);
        }
        return grades;
    }
}
