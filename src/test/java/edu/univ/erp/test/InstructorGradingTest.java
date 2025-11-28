package edu.univ.erp.test;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.domain.AssignmentScore;
import edu.univ.erp.domain.StudentGradeItem;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InstructorGradingTest {

    private static InstructorApi api;

    // --- TEST DATA (Adjust to match your DB) ---
    private final int INSTRUCTOR_ID = 4; // Ensure this user is an Instructor
    private final int SECTION_ID = 5;    // Ensure this section belongs to INSTRUCTOR_ID
    private final int STUDENT_ID = 2;    // Ensure this student is in SECTION_ID
    private final int WRONG_INSTRUCTOR_ID = 999; // ID that does NOT teach this section

    @BeforeAll
    static void setup() {
        api = new InstructorApi();
    }

    @Test
    @Order(1)
    @DisplayName("Instructor should see their own assignments")
    void testFetchAssignments() {
        // Mock the session as the correct instructor
        UserSession.getInstance().setSession(INSTRUCTOR_ID, "Instructor", "inst1");

        List<AssignmentScore> assignments = api.getCourseAssignments(SECTION_ID);
        assertNotNull(assignments, "Assignments list should not be null");
        // We expect at least the 3 standard ones (Quiz, Mid, End)
        assertTrue(assignments.size() >= 3, "Should have Quiz, Midterm, End-Sem configured");
    }

    @Test
    @Order(2)
    @DisplayName("Grading should fail if Instructor does not teach the section")
    void testSecurityCheck() {
        // Mock session as a DIFFERENT user (hacker or other instructor)
        UserSession.getInstance().setSession(WRONG_INSTRUCTOR_ID, "Instructor", "hacker");

        // Try to update the grade
        // (Assuming Assignment ID 1 exists for this section, adjust if dynamic)
        List<AssignmentScore> assignments = api.getCourseAssignments(SECTION_ID);
        if (!assignments.isEmpty()) {
            int assignId = assignments.get(0).getAssignmentId();
            boolean result = api.updateComponentScore(assignId, STUDENT_ID, 95.0);

            assertFalse(result, "Security Breach: Instructor graded a section they do not own!");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Valid grading should succeed")
    void testValidGrading() {
        // Login correctly
        UserSession.getInstance().setSession(INSTRUCTOR_ID, "Instructor", "inst1");

        List<AssignmentScore> assignments = api.getCourseAssignments(SECTION_ID);
        int assignId = assignments.get(0).getAssignmentId();

        boolean result = api.updateComponentScore(assignId, STUDENT_ID, 88.5);
        assertTrue(result, "Grading failed for valid owner");

        // Verify the score was saved
        Double score = api.getScore(STUDENT_ID, assignId);
        assertEquals(88.5, score, 0.01, "Saved score does not match input");
    }
}