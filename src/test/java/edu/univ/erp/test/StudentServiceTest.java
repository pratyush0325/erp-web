package edu.univ.erp.test;

import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.data.RegistrationStore;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentServiceTest {

    private static StudentService studentService;
    private static RegistrationStore store;

    // ADJUST THESE IDS TO MATCH YOUR LOCAL DB FOR TESTING
// In StudentServiceTest.java
    private final int TEST_STUDENT_ID = 3; // Make sure Student 3 exists
    private final int TEST_SECTION_ID = 1; // Make sure Section 1 exists and has capacity!
    @BeforeAll
    static void setup() {
        studentService = new StudentService();
        store = new RegistrationStore();
    }

    @BeforeEach
    void cleanSlate() {
        // Cleanup: Remove enrollment if it exists so we start fresh
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/erp_db", "root", "prabhi12")) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM enrollments WHERE student_id=" + TEST_STUDENT_ID + " AND section_id=" + TEST_SECTION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully register for an open course")
    void testRegistrationSuccess() {
        RegistrationStatus status = studentService.registerStudent(TEST_STUDENT_ID, TEST_SECTION_ID);
        Assertions.assertEquals(RegistrationStatus.SUCCESS, status, "First registration should succeed");
    }

    @Test
    @Order(2)
    @DisplayName("Should block duplicate registration")
    void testDuplicateRegistration() {
        // 1. Register once
        studentService.registerStudent(TEST_STUDENT_ID, TEST_SECTION_ID);

        // 2. Try again
        RegistrationStatus status = studentService.registerStudent(TEST_STUDENT_ID, TEST_SECTION_ID);

        // 3. Verify
        Assertions.assertEquals(RegistrationStatus.ALREADY_REGISTERED, status, "Second attempt should fail");
    }
}