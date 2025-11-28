package edu.univ.erp.test;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.service.StudentService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MaintenanceModeTest {

    private static AdminApi adminApi;
    private static StudentService studentService;

    @BeforeAll
    static void setup() {
        adminApi = new AdminApi();
        studentService = new StudentService();
    }

    @AfterEach
    void reset() {
        // Always turn OFF maintenance after tests to not break the app
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
        // 1. Turn ON Maintenance
        adminApi.setMaintenance(true);

        // 2. Try to register (IDs don't matter, it should fail before DB check)
        RegistrationStatus status = studentService.registerStudent(1, 1);

        // 3. Verify
        assertEquals(RegistrationStatus.MAINTENANCE_MODE, status, "Should return MAINTENANCE_MODE status");
    }
}