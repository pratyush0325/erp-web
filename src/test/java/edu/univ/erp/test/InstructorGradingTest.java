package edu.univ.erp.test;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.domain.AssignmentScore;
import org.junit.jupiter.api.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InstructorGradingTest {

    private static InstructorApi api;

    // UPDATE THESE IDS TO MATCH YOUR DB
    private final int INSTRUCTOR_ID = 2; // Ensure this user is an Instructor
    private final int SECTION_ID = 1;    // Ensure this section belongs to INSTRUCTOR_ID
    private final int STUDENT_ID = 3;    // Ensure this student is in SECTION_ID
    private final int WRONG_ID = 999;

    @BeforeAll
    static void setup() {
        api = new InstructorApi();
    }

    @Test
    @Order(1)
    @DisplayName("Instructor should see their own assignments")
    void testFetchAssignments() {
        UserSession.getInstance().setSession(INSTRUCTOR_ID, "Instructor", "inst1");

        // --- FIX: INITIALIZE WEIGHTS FIRST ---
        // This creates the assignments in the DB so the list isn't empty!
        api.saveWeights(SECTION_ID, 20, 30, 50);
        // -------------------------------------

        List<AssignmentScore> assignments = api.getCourseAssignments(SECTION_ID);
        assertNotNull(assignments);
        assertTrue(assignments.size() >= 3, "Should have Quiz, Midterm, End-Sem configured");
    }

    @Test
    @Order(2)
    @DisplayName("Grading should fail if Instructor does not teach the section")
    void testSecurityCheck() {
        UserSession.getInstance().setSession(WRONG_ID, "Instructor", "hacker");

        // Try to update a grade for a section they don't own
        // We just use a dummy assignment ID (1) for the check
        boolean result = api.updateComponentScore(1, STUDENT_ID, 95.0);

        assertFalse(result, "Security Breach: Instructor graded a section they do not own!");
    }

    @Test
    @Order(3)
    @DisplayName("Valid grading should succeed")
    void testValidGrading() {
        UserSession.getInstance().setSession(INSTRUCTOR_ID, "Instructor", "inst1");

        List<AssignmentScore> assignments = api.getCourseAssignments(SECTION_ID);

        // Ensure we have assignments before trying to get(0)
        if (assignments.isEmpty()) {
            api.saveWeights(SECTION_ID, 20, 30, 50);
            assignments = api.getCourseAssignments(SECTION_ID);
        }

        int assignId = assignments.get(0).getAssignmentId();

        boolean result = api.updateComponentScore(assignId, STUDENT_ID, 88.5);
        assertTrue(result, "Grading failed for valid owner");

        Double score = api.getScore(STUDENT_ID, assignId);
        assertEquals(88.5, score, 0.01);
    }
}