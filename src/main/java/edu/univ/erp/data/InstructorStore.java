package edu.univ.erp.data;


import edu.univ.erp.domain.AssignmentScore;
import edu.univ.erp.domain.InstructorCourseItem;
import edu.univ.erp.domain.StudentGradeItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class InstructorStore {
    private String dbUrl = "jdbc:mysql://localhost:3306/erp_db";
    private String dbUser = "root";
    private String dbPassword = "prabhi12";

    public List<InstructorCourseItem> getCoursesByInstructorId(int instructorId) {
        List<InstructorCourseItem> courses = new ArrayList<>();

        // We join sections with courses to get the title.
        // We also use a subquery to count how many students are enrolled.
        String query = "SELECT s.section_id, c.code, c.title, s.day_time, s.room, s.capacity, " + "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) as enrolled_count " + "FROM sections s " + "JOIN courses c ON s.course_id = c.code " + "WHERE s.instructor_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(new InstructorCourseItem(rs.getInt("section_id"), rs.getString("code"), rs.getString("title"), rs.getString("day_time"), rs.getString("room"), rs.getInt("enrolled_count"), rs.getInt("capacity")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }


    public List<StudentGradeItem> getStudentsBySectionId(int sectionId) {
        List<StudentGradeItem> students = new ArrayList<>();

        // Join enrollments with auth_db to get the student's name (username)
        String query = "SELECT e.student_id, a.username, e.grade " +
                "FROM enrollments e " +
                "JOIN auth_db.users_auth a ON e.student_id = a.user_id " +
                "WHERE e.section_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new StudentGradeItem(
                            rs.getInt("student_id"),
                            rs.getString("username"),
                            rs.getString("grade")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }


    // Secure version: Ensures the instructor actually teaches this section
    public boolean updateGrade(int instructorId, int studentId, int sectionId, String newGrade) {
        String query = "UPDATE enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "SET e.grade = ? " +
                "WHERE e.student_id = ? AND e.section_id = ? AND s.instructor_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newGrade);
            stmt.setInt(2, studentId);
            stmt.setInt(3, sectionId);
            stmt.setInt(4, instructorId); // <--- Security Check

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Secure version: Checks assignment ownership via section -> instructor
    public boolean updateStudentScore(int instructorId, int assignmentId, int studentId, double score) {
        // 1. First, verify ownership
        String checkSql = "SELECT 1 FROM course_assignments ca " +
                "JOIN sections s ON ca.section_id = s.section_id " +
                "WHERE ca.assignment_id = ? AND s.instructor_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, assignmentId);
                checkStmt.setInt(2, instructorId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) return false; // Instructor does not own this assignment
            }

            // 2. Perform the update
            String updateSql = "INSERT INTO student_scores (assignment_id, student_id, score_obtained) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE score_obtained = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, assignmentId);
                stmt.setInt(2, studentId);
                stmt.setDouble(3, score);
                stmt.setDouble(4, score);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches a single score for a specific student and assignment.
     * Returns null if no score exists.
     */
    public Double getStudentScore(int studentId, int assignmentId) {
        String query = "SELECT score_obtained FROM student_scores WHERE student_id = ? AND assignment_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("score_obtained");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No score found
    }

    // Fetch all assignments for a course
    public List<AssignmentScore> getAssignmentsForSection(int sectionId) {
        List<AssignmentScore> list = new ArrayList<>();
        // FIXED: Added ", weight_percent" to the SELECT list
        String query = "SELECT assignment_id, assignment_name, max_score, weight_percent FROM course_assignments WHERE section_id = ?";

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sectionId);

            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    list.add(new AssignmentScore(
                            rs.getInt("assignment_id"),
                            rs.getString("assignment_name"),
                            rs.getInt("max_score"),
                            rs.getInt("weight_percent"), // Now this will work!
                            null
                    ));
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update a specific student's score
    public boolean updateStudentScore(int assignmentId, int studentId, double score) {
        // Use "INSERT ON DUPLICATE KEY UPDATE" to handle both new scores and updates
        String query = "INSERT INTO student_scores (assignment_id, student_id, score_obtained) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score_obtained = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            stmt.setDouble(3, score);
            stmt.setDouble(4, score);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ... (existing methods like getStudentScore, updateStudentScore, etc.)

    /**
     * Creates a new assignment definition in the database.
     */
    public boolean configureWeights(int sectionId, int quizWeight, int midWeight, int endWeight) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            conn.setAutoCommit(false); // Transaction to ensure atomic update

            // Ensure all 3 exist and have correct weights
            if (!ensureComponent(conn, sectionId, "Quiz", quizWeight)) throw new SQLException("Failed Quiz");
            if (!ensureComponent(conn, sectionId, "Midterm", midWeight)) throw new SQLException("Failed Midterm");
            if (!ensureComponent(conn, sectionId, "End-Sem", endWeight)) throw new SQLException("Failed End-Sem");

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // Helper to Insert or Update a specific component
    private boolean ensureComponent(Connection conn, int sectionId, String name, int weight) throws SQLException {
        // 1. Check if it exists
        String checkSql = "SELECT assignment_id FROM course_assignments WHERE section_id = ? AND assignment_name = ?";
        int assignmentId = -1;

        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, sectionId);
            stmt.setString(2, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) assignmentId = rs.getInt(1);
            }
        }

        if (assignmentId != -1) {
            // 2. Update existing
            String updateSql = "UPDATE course_assignments SET weight_percent = ? WHERE assignment_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, weight);
                stmt.setInt(2, assignmentId);
                stmt.executeUpdate();
            }
        } else {
            // 3. Create new (Default Max Score = 100)
            String insertSql = "INSERT INTO course_assignments (section_id, assignment_name, max_score, weight_percent) VALUES (?, ?, 100, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, sectionId);
                stmt.setString(2, name);
                stmt.setInt(3, weight);
                stmt.executeUpdate();
            }
        }
        return true;
    }
} // <--- End of class

