package edu.univ.erp.api.instructor;

import edu.univ.erp.api.maintenance.MaintenanceApi; // <--- Import
import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.domain.AssignmentScore;
import edu.univ.erp.domain.InstructorCourseItem;
import edu.univ.erp.domain.StudentGradeItem;

import java.util.List;

public class InstructorApi {
    private final InstructorStore instructorStore = new InstructorStore();
    private final edu.univ.erp.data.SettingsStore settingsStore = new edu.univ.erp.data.SettingsStore();

    public List<InstructorCourseItem> getMyCourses() {
        int instructorId = UserSession.getInstance().getUserId();
        return instructorStore.getCoursesByInstructorId(instructorId);
    }

    public List<StudentGradeItem> getClassList(int sectionId) {
        return instructorStore.getStudentsBySectionId(sectionId);
    }

    // --- WRITE OPERATIONS (Blocked in Maintenance) ---

    public boolean assignGrade(int studentId, int sectionId, String grade) {
        if (MaintenanceApi.isMaintenanceOn()) return false;
        int instructorId = UserSession.getInstance().getUserId(); // <--- Get ID
        return instructorStore.updateGrade(instructorId, studentId, sectionId, grade);
    }

    public List<AssignmentScore> getCourseAssignments(int sectionId) {
        return instructorStore.getAssignmentsForSection(sectionId);
    }

    public Double getScore(int studentId, int assignmentId) {
        return instructorStore.getStudentScore(studentId, assignmentId);
    }

    public boolean updateComponentScore(int assignmentId, int studentId, double score) {
        if (MaintenanceApi.isMaintenanceOn()) return false;
        int instructorId = UserSession.getInstance().getUserId(); // <--- Get ID
        return instructorStore.updateStudentScore(instructorId, assignmentId, studentId, score);
    }

    public boolean saveWeights(int sectionId, int q, int m, int e) {
        if (MaintenanceApi.isMaintenanceOn()) return false;
        return instructorStore.configureWeights(sectionId, q, m, e);
    }

    public java.util.List<edu.univ.erp.domain.CourseStats> getAllCourseStatistics() {
        // 1. Get all my sections
        List<InstructorCourseItem> courses = getMyCourses();
        java.util.List<edu.univ.erp.domain.CourseStats> statsList = new java.util.ArrayList<>();

        // 2. Calculate stats for each
        for (InstructorCourseItem c : courses) {
            statsList.add(instructorStore.getSectionStatistics(c.getSectionId(), c.getCourseCode(), c.getCourseTitle()));
        }
        return statsList;
    }

    public String getDeadline() {
        java.time.LocalDate date = settingsStore.getRegistrationDeadline();
        return (date != null) ? date.toString() : "None";
    }

    public int getPendingGrades() {
        int instructorId = UserSession.getInstance().getUserId();
        return instructorStore.getPendingGradesCount(instructorId);
    }
}