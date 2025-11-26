package edu.univ.erp.api.instructor;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.domain.AssignmentScore;
import edu.univ.erp.domain.InstructorCourseItem;
import edu.univ.erp.domain.StudentGradeItem;

import java.util.List;

public class InstructorApi {
    private final InstructorStore instructorStore = new InstructorStore();

    public List<InstructorCourseItem> getMyCourses() {
        int instructorId = UserSession.getInstance().getUserId();
        return instructorStore.getCoursesByInstructorId(instructorId);
    }


    public List<StudentGradeItem> getClassList(int sectionId) {
        return instructorStore.getStudentsBySectionId(sectionId);
    }

    public boolean assignGrade(int studentId, int sectionId, String grade) {
        return instructorStore.updateGrade(studentId, sectionId, grade);
    }

    public List<AssignmentScore> getCourseAssignments(int sectionId) {
        return instructorStore.getAssignmentsForSection(sectionId);
    }

    public Double getScore(int studentId, int assignmentId) {
        return instructorStore.getStudentScore(studentId, assignmentId);
    }

    public boolean updateComponentScore(int assignmentId, int studentId, double score) {
        return instructorStore.updateStudentScore(assignmentId, studentId, score);
    }

    public boolean createAssignment(int sectionId, String name, int maxScore, int weight) {
        return instructorStore.addAssignment(sectionId, name, maxScore, weight);
    }
}