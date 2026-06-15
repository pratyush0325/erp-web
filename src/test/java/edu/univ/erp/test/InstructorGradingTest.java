package edu.univ.erp.test;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.AssignmentScore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InstructorGradingTest {

    @Autowired
    private InstructorApi api;

    // UPDATE THESE IDS TO MATCH YOUR DB
    private final int INSTRUCTOR_ID = 2;
    private final int SECTION_ID = 1;
    private final int STUDENT_ID = 3;
    private final int WRONG_ID = 999;

    @Test
    @Order(1)
    @DisplayName("Instructor should see their own assignments")
    void testFetchAssignments() {
        api.saveWeights(SECTION_ID, 20, 30, 50);
        List<AssignmentScore> assignments = api.getCourseAssignments(SECTION_ID);
        assertNotNull(assignments);
        assertTrue(assignments.size() >= 3, "Should have Quiz, Midterm, End-Sem configured");
    }

    @Test
    @Order(2)
    @DisplayName("Grading should fail if Instructor does not teach the section")
    void testSecurityCheck() {
        boolean result = api.updateComponentScore(WRONG_ID, 1, STUDENT_ID, 95.0);
        assertFalse(result, "Security Breach: Instructor graded a section they do not own!");
    }

    @Test
    @Order(3)
    @DisplayName("Valid grading should succeed")
    void testValidGrading() {
        List<AssignmentScore> assignments = api.getCourseAssignments(SECTION_ID);
        if (assignments.isEmpty()) {
            api.saveWeights(SECTION_ID, 20, 30, 50);
            assignments = api.getCourseAssignments(SECTION_ID);
        }
        int assignId = assignments.get(0).getAssignmentId();
        boolean result = api.updateComponentScore(INSTRUCTOR_ID, assignId, STUDENT_ID, 88.5);
        assertTrue(result, "Grading failed for valid owner");
        Double score = api.getScore(STUDENT_ID, assignId);
        assertEquals(88.5, score, 0.01);
    }
}
