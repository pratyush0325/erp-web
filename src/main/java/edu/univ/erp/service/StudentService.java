package edu.univ.erp.service;

import edu.univ.erp.api.student.DropStatus;
import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.data.RegistrationStore;

public class StudentService {

    private final RegistrationStore regStore = new RegistrationStore();

    /**
     * Contains the business logic for registering a student in a section.
     */
    public RegistrationStatus registerStudent(int studentId, int sectionId) {
        // Rule 1: Check for duplicate registration
        if (regStore.isAlreadyRegistered(studentId, sectionId)) {
            return RegistrationStatus.ALREADY_REGISTERED;
        }

        // Rule 2: Check for available seats
        int currentEnrollment = regStore.getSectionEnrollmentCount(sectionId);
        int capacity = regStore.getSectionCapacity(sectionId);

        if (currentEnrollment >= capacity) {
            return RegistrationStatus.SECTION_FULL;
        }

        // Rule 3 (Future): Check maintenance mode
        // if (settingsStore.isMaintenanceModeOn()) {
        //     return RegistrationStatus.ERROR_MAINTENANCE_MODE;
        // }

        // All checks passed. Proceed to enroll.
        boolean success = regStore.createEnrollment(studentId, sectionId);

        return success ? RegistrationStatus.SUCCESS : RegistrationStatus.ERROR_UNKNOWN;
    }

    public DropStatus dropStudent(int studentId, int sectionId) {
        // Rule 1 (Future): Check drop deadline
        // if (isPastDropDeadline(sectionId)) {
        //     return DropStatus.ERROR_PAST_DEADLINE;
        // }

        // Rule 2 (Future): Check maintenance mode
        // if (settingsStore.isMaintenanceModeOn()) {
        //     return DropStatus.ERROR_MAINTENANCE_MODE;
        // }

        // Attempt to delete the enrollment
        boolean success = regStore.deleteEnrollment(studentId, sectionId);

        if (success) {
            return DropStatus.SUCCESS;
        } else {
            // If delete failed, it's likely because the record didn't exist
            // (or a DB error occurred)
            if (!regStore.isAlreadyRegistered(studentId, sectionId)) {
                return DropStatus.NOT_REGISTERED;
            }
            return DropStatus.ERROR_UNKNOWN;
        }
    }
}