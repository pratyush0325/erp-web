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


    public boolean updateGrade(int studentId, int sectionId, String newGrade) {
        String query = "UPDATE enrollments SET grade = ? WHERE student_id = ? AND section_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newGrade);
            stmt.setInt(2, studentId);
            stmt.setInt(3, sectionId);

            int rows = stmt.executeUpdate();
            return rows > 0;
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
    public boolean addAssignment(int sectionId, String name, int maxScore, int weight) {
        String query = "INSERT INTO course_assignments (section_id, assignment_name, max_score, weight_percent) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sectionId);
            stmt.setString(2, name);
            stmt.setInt(3, maxScore);
            stmt.setInt(4, weight);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} // <--- End of class

