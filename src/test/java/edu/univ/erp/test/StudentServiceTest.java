package edu.univ.erp.test;

import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.service.StudentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    private final int TEST_STUDENT_ID = 3;
    private final int TEST_SECTION_ID = 2;

    @BeforeEach
    void cleanSlate() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/erp_db", "root", "prabhi12");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM grades");
            stmt.executeUpdate("DELETE FROM student_scores WHERE student_id=" + TEST_STUDENT_ID);
            stmt.executeUpdate("DELETE FROM enrollments WHERE student_id=" + TEST_STUDENT_ID + " AND section_id=" + TEST_SECTION_ID);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully register for an open course")
    void testRegistrationSuccess() {
        RegistrationStatus status = studentService.registerStudent(TEST_STUDENT_ID, TEST_SECTION_ID);
        Assertions.assertEquals(RegistrationStatus.SUCCESS, status);
    }

    @Test
    @Order(2)
    @DisplayName("Should block duplicate registration")
    void testDuplicateRegistration() {
        studentService.registerStudent(TEST_STUDENT_ID, TEST_SECTION_ID);
        RegistrationStatus status = studentService.registerStudent(TEST_STUDENT_ID, TEST_SECTION_ID);
        Assertions.assertEquals(RegistrationStatus.ALREADY_REGISTERED, status);
    }
}
