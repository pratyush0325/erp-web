package edu.univ.erp.service;

import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.api.student.DropStatus;
import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.data.RegistrationStore;
import edu.univ.erp.data.SettingsStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StudentService {

    private final RegistrationStore regStore;
    private final SettingsStore settingsStore;
    private final MaintenanceApi maintenanceApi;

    public StudentService(RegistrationStore regStore, SettingsStore settingsStore, MaintenanceApi maintenanceApi) {
        this.regStore = regStore;
        this.settingsStore = settingsStore;
        this.maintenanceApi = maintenanceApi;
    }

    public RegistrationStatus registerStudent(int studentId, int sectionId) {
        if (maintenanceApi.isMaintenanceOn()) return RegistrationStatus.MAINTENANCE_MODE;
        if (isPastDeadline()) return RegistrationStatus.DEADLINE_PASSED;
        if (regStore.isAlreadyRegistered(studentId, sectionId)) return RegistrationStatus.ALREADY_REGISTERED;
        int current = regStore.getSectionEnrollmentCount(sectionId);
        int cap = regStore.getSectionCapacity(sectionId);
        if (current >= cap) return RegistrationStatus.SECTION_FULL;
        return regStore.createEnrollment(studentId, sectionId) ? RegistrationStatus.SUCCESS : RegistrationStatus.ERROR_UNKNOWN;
    }

    public DropStatus dropStudent(int studentId, int sectionId) {
        if (maintenanceApi.isMaintenanceOn()) return DropStatus.MAINTENANCE_MODE;
        if (isPastDeadline()) return DropStatus.DEADLINE_PASSED;
        boolean success = regStore.deleteEnrollment(studentId, sectionId);
        if (success) return DropStatus.SUCCESS;
        if (!regStore.isAlreadyRegistered(studentId, sectionId)) return DropStatus.NOT_REGISTERED;
        return DropStatus.ERROR_UNKNOWN;
    }

    private boolean isPastDeadline() {
        LocalDate deadline = settingsStore.getRegistrationDeadline();
        return deadline != null && LocalDate.now().isAfter(deadline);
    }
}
