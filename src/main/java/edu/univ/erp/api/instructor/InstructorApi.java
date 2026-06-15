package edu.univ.erp.api.instructor;

import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.data.SettingsStore;
import edu.univ.erp.domain.AssignmentScore;
import edu.univ.erp.domain.CourseStats;
import edu.univ.erp.domain.InstructorCourseItem;
import edu.univ.erp.domain.StudentGradeItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstructorApi {

    private final InstructorStore instructorStore;
    private final SettingsStore settingsStore;
    private final MaintenanceApi maintenanceApi;

    public InstructorApi(InstructorStore instructorStore, SettingsStore settingsStore, MaintenanceApi maintenanceApi) {
        this.instructorStore = instructorStore;
        this.settingsStore = settingsStore;
        this.maintenanceApi = maintenanceApi;
    }

    public List<InstructorCourseItem> getMyCourses(int userId) {
        return instructorStore.getCoursesByInstructorId(userId);
    }

    public List<StudentGradeItem> getClassList(int sectionId) {
        return instructorStore.getStudentsBySectionId(sectionId);
    }

    public boolean assignGrade(int instructorId, int studentId, int sectionId, String grade) {
        if (maintenanceApi.isMaintenanceOn()) return false;
        return instructorStore.updateGrade(instructorId, studentId, sectionId, grade);
    }

    public List<AssignmentScore> getCourseAssignments(int sectionId) {
        return instructorStore.getAssignmentsForSection(sectionId);
    }

    public Double getScore(int studentId, int assignmentId) {
        return instructorStore.getStudentScore(studentId, assignmentId);
    }

    public boolean updateComponentScore(int instructorId, int assignmentId, int studentId, double score) {
        if (maintenanceApi.isMaintenanceOn()) return false;
        return instructorStore.updateStudentScore(instructorId, assignmentId, studentId, score);
    }

    public boolean saveWeights(int sectionId, int q, int m, int e) {
        if (maintenanceApi.isMaintenanceOn()) return false;
        return instructorStore.configureWeights(sectionId, q, m, e);
    }

    public List<CourseStats> getAllCourseStatistics(int userId) {
        List<InstructorCourseItem> courses = getMyCourses(userId);
        List<CourseStats> statsList = new ArrayList<>();
        for (InstructorCourseItem c : courses) {
            statsList.add(instructorStore.getSectionStatistics(c.getSectionId(), c.getCourseCode(), c.getCourseTitle()));
        }
        return statsList;
    }

    public String getDeadline() {
        LocalDate date = settingsStore.getRegistrationDeadline();
        return (date != null) ? date.toString() : "None";
    }

    public int getPendingGrades(int userId) {
        return instructorStore.getPendingGradesCount(userId);
    }
}
