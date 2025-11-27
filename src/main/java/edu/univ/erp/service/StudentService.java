package edu.univ.erp.service;

import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.api.student.DropStatus;
import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.data.RegistrationStore;
import edu.univ.erp.data.SettingsStore; // <--- Import
import java.time.LocalDate;

public class StudentService {

    private final RegistrationStore regStore = new RegistrationStore();
    private final SettingsStore settingsStore = new SettingsStore(); // <--- New Instance

    public RegistrationStatus registerStudent(int studentId, int sectionId) {
        // 1. Maintenance Check
        if (MaintenanceApi.isMaintenanceOn()) return RegistrationStatus.MAINTENANCE_MODE;

        // 2. Deadline Check (NEW)
        if (isPastDeadline()) return RegistrationStatus.DEADLINE_PASSED;

        // 3. Logic Checks
        if (regStore.isAlreadyRegistered(studentId, sectionId)) return RegistrationStatus.ALREADY_REGISTERED;

        int current = regStore.getSectionEnrollmentCount(sectionId);
        int cap = regStore.getSectionCapacity(sectionId);
        if (current >= cap) return RegistrationStatus.SECTION_FULL;

        return regStore.createEnrollment(studentId, sectionId) ? RegistrationStatus.SUCCESS : RegistrationStatus.ERROR_UNKNOWN;
    }

    public DropStatus dropStudent(int studentId, int sectionId) {
        // 1. Maintenance Check
        if (MaintenanceApi.isMaintenanceOn()) return DropStatus.MAINTENANCE_MODE;

        // 2. Deadline Check (NEW)
        if (isPastDeadline()) return DropStatus.DEADLINE_PASSED;

        // 3. Logic Checks
        boolean success = regStore.deleteEnrollment(studentId, sectionId);
        if (success) return DropStatus.SUCCESS;

        if (!regStore.isAlreadyRegistered(studentId, sectionId)) return DropStatus.NOT_REGISTERED;
        return DropStatus.ERROR_UNKNOWN;
    }

    // --- Helper Method ---
    private boolean isPastDeadline() {
        LocalDate deadline = settingsStore.getRegistrationDeadline();
        if (deadline == null) return false; // No deadline set = always open
        return LocalDate.now().isAfter(deadline);
    }
}