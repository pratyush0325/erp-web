package edu.univ.erp.data;

import edu.univ.erp.domain.RegistrationItem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrationStore {

    private String dbUrl = "jdbc:mysql://localhost:3306/erp_db";
    private String dbUser = "root";
    private String dbPassword = "prabhi12";

    /**
     * Fetches all registered sections for a specific student.
     */
    public List<RegistrationItem> findRegistrationsByStudentId(int studentId) {
        List<RegistrationItem> registrations = new ArrayList<>();

        // UPDATED QUERY: Added c.code
        String query = "SELECT s.section_id, c.code, c.title, s.day_time, s.room, e.status " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.code " +
                "WHERE e.student_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int sectionId = rs.getInt("section_id");
                    String sectionStr = "SEC-" + sectionId;

                    RegistrationItem item = new RegistrationItem(
                            sectionId,
                            rs.getString("code"), // <--- Fetch Code
                            sectionStr,
                            rs.getString("title"),
                            rs.getString("day_time"),
                            rs.getString("room"),
                            rs.getString("status")
                    );
                    registrations.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    /**
     * Checks if a student is already enrolled in a specific section.
     * @return true if an enrollment record exists, false otherwise.
     */
    public boolean isAlreadyRegistered(int studentId, int sectionId) {
        String query = "SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if a record was found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Fail-safe: assume registered to prevent duplicates on error
        }
    }

    /**
     * Gets the current number of enrolled students for a section.
     */
    public int getSectionEnrollmentCount(int sectionId) {
        String query = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the total capacity for a specific section.
     */
    public int getSectionCapacity(int sectionId) {
        String query = "SELECT capacity FROM sections WHERE section_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capacity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Default to 0 if not found
    }

    /**
     * Inserts a new enrollment record into the database.
     * @return true if the insert was successful, false otherwise.
     */
    public boolean createEnrollment(int studentId, int sectionId) {
        String query = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'registered')";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an enrollment record from the database.
     * @return true if the delete was successful, false otherwise.
     */
    public boolean deleteEnrollment(int studentId, int sectionId) {
        // We check for studentId AND sectionId to ensure
        // students can only drop their own courses.
        String query = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Will be false if the record didn't exist
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all component grades (Quizzes, Exams) for the student.
     */
    public java.util.List<edu.univ.erp.domain.StudentGradeView> getGradesForStudent(int studentId) {
        java.util.List<edu.univ.erp.domain.StudentGradeView> grades = new java.util.ArrayList<>();

        // Updated Query: Fetches Title and Weight
        String query = "SELECT c.code, c.title, ca.assignment_name, ss.score_obtained, ca.max_score, ca.weight_percent " +
                "FROM student_scores ss " +
                "JOIN course_assignments ca ON ss.assignment_id = ca.assignment_id " +
                "JOIN sections s ON ca.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.code " +
                "WHERE ss.student_id = ? " +
                "ORDER BY c.code, ca.assignment_name";

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);

            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new edu.univ.erp.domain.StudentGradeView(
                            rs.getString("code"),
                            rs.getString("title"), // <--- Title
                            rs.getString("assignment_name"),
                            rs.getDouble("score_obtained"),
                            rs.getInt("max_score"),
                            rs.getInt("weight_percent") // <--- Weight
                    ));
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }
}