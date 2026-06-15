package edu.univ.erp.data;

import edu.univ.erp.domain.AssignmentScore;
import edu.univ.erp.domain.CourseStats;
import edu.univ.erp.domain.InstructorCourseItem;
import edu.univ.erp.domain.StudentGradeItem;
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
public class InstructorStore {

    private static final Logger log = LoggerFactory.getLogger(InstructorStore.class);

    private final DataSource dataSource;

    public InstructorStore(@Qualifier("erpDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<InstructorCourseItem> getCoursesByInstructorId(int instructorId) {
        List<InstructorCourseItem> courses = new ArrayList<>();
        String query = "SELECT s.section_id, c.code, c.title, s.day_time, s.room, s.capacity, " +
                "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) as enrolled_count " +
                "FROM sections s JOIN courses c ON s.course_id = c.code WHERE s.instructor_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(new InstructorCourseItem(
                            rs.getInt("section_id"), rs.getString("code"), rs.getString("title"),
                            rs.getString("day_time"), rs.getString("room"),
                            rs.getInt("enrolled_count"), rs.getInt("capacity")));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to fetch courses for instructorId={}", instructorId, e);
        }
        return courses;
    }

    public List<StudentGradeItem> getStudentsBySectionId(int sectionId) {
        List<StudentGradeItem> students = new ArrayList<>();
        String query = "SELECT e.student_id, a.username, e.grade " +
                "FROM enrollments e " +
                "JOIN auth_db.users_auth a ON e.student_id = a.user_id " +
                "WHERE e.section_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new StudentGradeItem(
                            rs.getInt("student_id"), rs.getString("username"), rs.getString("grade")));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to fetch students for sectionId={}", sectionId, e);
        }
        return students;
    }

    public boolean updateGrade(int instructorId, int studentId, int sectionId, String newGrade) {
        String query = "UPDATE enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "SET e.grade = ? " +
                "WHERE e.student_id = ? AND e.section_id = ? AND s.instructor_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newGrade);
            stmt.setInt(2, studentId);
            stmt.setInt(3, sectionId);
            stmt.setInt(4, instructorId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to update grade for student={} section={}", studentId, sectionId, e);
            return false;
        }
    }

    public boolean updateStudentScore(int instructorId, int assignmentId, int studentId, double score) {
        String checkSql = "SELECT 1 FROM course_assignments ca " +
                "JOIN sections s ON ca.section_id = s.section_id " +
                "WHERE ca.assignment_id = ? AND s.instructor_id = ?";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, assignmentId);
                checkStmt.setInt(2, instructorId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) return false;
                }
            }
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
            log.error("Failed to update score for assignment={} student={}", assignmentId, studentId, e);
            return false;
        }
    }

    public Double getStudentScore(int studentId, int assignmentId) {
        String query = "SELECT score_obtained FROM student_scores WHERE student_id = ? AND assignment_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("score_obtained");
            }
        } catch (SQLException e) {
            log.error("Failed to fetch score for student={} assignment={}", studentId, assignmentId, e);
        }
        return null;
    }

    public List<AssignmentScore> getAssignmentsForSection(int sectionId) {
        List<AssignmentScore> list = new ArrayList<>();
        String query = "SELECT assignment_id, assignment_name, max_score, weight_percent FROM course_assignments WHERE section_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new AssignmentScore(
                            rs.getInt("assignment_id"), rs.getString("assignment_name"),
                            rs.getInt("max_score"), rs.getInt("weight_percent"), null));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to fetch assignments for sectionId={}", sectionId, e);
        }
        return list;
    }

    public boolean updateStudentScore(int assignmentId, int studentId, double score) {
        String query = "INSERT INTO student_scores (assignment_id, student_id, score_obtained) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score_obtained = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            stmt.setDouble(3, score);
            stmt.setDouble(4, score);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error("Failed to update score for assignment={} student={}", assignmentId, studentId, e);
            return false;
        }
    }

    public boolean configureWeights(int sectionId, int quizWeight, int midWeight, int endWeight) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            if (!ensureComponent(conn, sectionId, "Quiz", quizWeight)) throw new SQLException("Failed Quiz");
            if (!ensureComponent(conn, sectionId, "Midterm", midWeight)) throw new SQLException("Failed Midterm");
            if (!ensureComponent(conn, sectionId, "End-Sem", endWeight)) throw new SQLException("Failed End-Sem");
            conn.commit();
            return true;
        } catch (SQLException e) {
            log.error("Failed to configure weights for sectionId={}", sectionId, e);
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { log.error("Rollback failed", ex); }
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { log.error("Close failed", ex); }
        }
    }

    private boolean ensureComponent(Connection conn, int sectionId, String name, int weight) throws SQLException {
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
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE course_assignments SET weight_percent = ? WHERE assignment_id = ?")) {
                stmt.setInt(1, weight);
                stmt.setInt(2, assignmentId);
                stmt.executeUpdate();
            }
        } else {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO course_assignments (section_id, assignment_name, max_score, weight_percent) VALUES (?, ?, 100, ?)")) {
                stmt.setInt(1, sectionId);
                stmt.setString(2, name);
                stmt.setInt(3, weight);
                stmt.executeUpdate();
            }
        }
        return true;
    }

    public CourseStats getSectionStatistics(int sectionId, String code, String title) {
        List<AssignmentScore> assignments = getAssignmentsForSection(sectionId);
        List<StudentGradeItem> students = getStudentsBySectionId(sectionId);

        if (students.isEmpty()) {
            return new CourseStats(code, title, 0, 0, 0, 0, 0);
        }

        int submittedCount = 0;
        double min = 100.0, max = 0.0, sum = 0.0;
        int countWithScores = 0;

        for (StudentGradeItem student : students) {
            double totalWeighted = 0.0;
            boolean hasAnyScore = false;
            for (AssignmentScore assign : assignments) {
                Double score = getStudentScore(student.getStudentId(), assign.getAssignmentId());
                if (score != null) {
                    hasAnyScore = true;
                    totalWeighted += (score / assign.getMaxScore()) * assign.getWeight();
                }
            }
            if (hasAnyScore) {
                submittedCount++;
                if (totalWeighted < min) min = totalWeighted;
                if (totalWeighted > max) max = totalWeighted;
                sum += totalWeighted;
                countWithScores++;
            }
        }

        double avg = (countWithScores > 0) ? (sum / countWithScores) : 0.0;
        if (countWithScores == 0) min = 0.0;

        return new CourseStats(code, title, students.size(), submittedCount, min, max, avg);
    }

    public int getPendingGradesCount(int instructorId) {
        String query = "SELECT COUNT(*) FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "WHERE s.instructor_id = ? AND (e.grade IS NULL OR e.grade = '')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count pending grades for instructorId={}", instructorId, e);
        }
        return 0;
    }
}
