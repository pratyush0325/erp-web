package edu.univ.erp.service;

import edu.univ.erp.api.maintenance.MaintenanceApi; // <--- Import this
import edu.univ.erp.api.student.DropStatus;
import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.data.RegistrationStore;

public class StudentService {

    private final RegistrationStore regStore = new RegistrationStore();

    public RegistrationStatus registerStudent(int studentId, int sectionId) {
        // 1. Check Maintenance Mode FIRST
        if (MaintenanceApi.isMaintenanceOn()) {
            return RegistrationStatus.MAINTENANCE_MODE;
        }

        // Rule 2: Check for duplicate registration
        if (regStore.isAlreadyRegistered(studentId, sectionId)) {
            return RegistrationStatus.ALREADY_REGISTERED;
        }

        // Rule 3: Check for available seats
        int currentEnrollment = regStore.getSectionEnrollmentCount(sectionId);
        int capacity = regStore.getSectionCapacity(sectionId);

        if (currentEnrollment >= capacity) {
            return RegistrationStatus.SECTION_FULL;
        }

        boolean success = regStore.createEnrollment(studentId, sectionId);
        return success ? RegistrationStatus.SUCCESS : RegistrationStatus.ERROR_UNKNOWN;
    }

    public DropStatus dropStudent(int studentId, int sectionId) {
        // 1. Check Maintenance Mode FIRST
        if (MaintenanceApi.isMaintenanceOn()) {
            return DropStatus.MAINTENANCE_MODE;
        }

        boolean success = regStore.deleteEnrollment(studentId, sectionId);

        if (success) {
            return DropStatus.SUCCESS;
        } else {
            if (!regStore.isAlreadyRegistered(studentId, sectionId)) {
                return DropStatus.NOT_REGISTERED;
            }
            return DropStatus.ERROR_UNKNOWN;
        }
    }
}