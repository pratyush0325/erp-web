package edu.univ.erp.api.student;

import edu.univ.erp.data.ProfileStore;
import edu.univ.erp.data.RegistrationStore;
import edu.univ.erp.data.SettingsStore;
import edu.univ.erp.domain.RegistrationItem;
import edu.univ.erp.domain.StudentGradeView;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentApi {

    private final RegistrationStore registrationStore;
    private final ProfileStore profileStore;
    private final SettingsStore settingsStore;
    private final StudentService studentService;

    public StudentApi(RegistrationStore registrationStore, ProfileStore profileStore,
                      SettingsStore settingsStore, StudentService studentService) {
        this.registrationStore = registrationStore;
        this.profileStore = profileStore;
        this.settingsStore = settingsStore;
        this.studentService = studentService;
    }

    public List<RegistrationItem> getMyRegistrations(int userId) {
        return registrationStore.findRegistrationsByStudentId(userId);
    }

    public RegistrationStatus registerForSection(int userId, int sectionId) {
        return studentService.registerStudent(userId, sectionId);
    }

    public DropStatus dropSection(int userId, int sectionId) {
        return studentService.dropStudent(userId, sectionId);
    }

    public List<StudentGradeView> getMyGrades(int userId) {
        return registrationStore.getGradesForStudent(userId);
    }

    public StudentProfile getProfile(int userId) {
        return profileStore.getStudentProfile(userId);
    }

    public String getDeadline() {
        LocalDate date = settingsStore.getRegistrationDeadline();
        return (date != null) ? date.toString() : "None";
    }
}
