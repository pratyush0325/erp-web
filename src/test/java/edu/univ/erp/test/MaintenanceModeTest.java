package edu.univ.erp.test;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.service.StudentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MaintenanceModeTest {

    @Autowired
    private AdminApi adminApi;

    @Autowired
    private StudentService studentService;

    @AfterEach
    void reset() {
        adminApi.setMaintenance(false);
    }

    @Test
    @Order(1)
    @DisplayName("Admin can toggle maintenance mode")
    void testToggleMaintenance() {
        adminApi.setMaintenance(true);
        assertTrue(adminApi.isMaintenanceOn(), "Maintenance should be ON");
        adminApi.setMaintenance(false);
        assertFalse(adminApi.isMaintenanceOn(), "Maintenance should be OFF");
    }

    @Test
    @Order(2)
    @DisplayName("Student Registration is BLOCKED when Maintenance is ON")
    void testBlockRegistration() {
        adminApi.setMaintenance(true);
        RegistrationStatus status = studentService.registerStudent(1, 1);
        assertEquals(RegistrationStatus.MAINTENANCE_MODE, status, "Should return MAINTENANCE_MODE status");
    }
}
