package edu.univ.erp.controller;

import edu.univ.erp.api.instructor.InstructorApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorController {

    private final InstructorApi instructorApi;

    public InstructorController(InstructorApi instructorApi) {
        this.instructorApi = instructorApi;
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getCourses(@AuthenticationPrincipal Integer userId) {
        return ResponseEntity.ok(instructorApi.getMyCourses(userId));
    }

    @GetMapping("/sections/{sectionId}/students")
    public ResponseEntity<?> getStudents(@PathVariable int sectionId) {
        return ResponseEntity.ok(instructorApi.getClassList(sectionId));
    }

    @PutMapping("/grades")
    public ResponseEntity<?> assignGrade(@AuthenticationPrincipal Integer userId,
                                         @RequestBody Map<String, Object> body) {
        int studentId = (int) body.get("studentId");
        int sectionId = (int) body.get("sectionId");
        String grade = (String) body.get("grade");
        boolean ok = instructorApi.assignGrade(userId, studentId, sectionId, grade);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @GetMapping("/sections/{sectionId}/assignments")
    public ResponseEntity<?> getAssignments(@PathVariable int sectionId) {
        return ResponseEntity.ok(instructorApi.getCourseAssignments(sectionId));
    }

    @PutMapping("/scores")
    public ResponseEntity<?> updateScore(@AuthenticationPrincipal Integer userId,
                                         @RequestBody Map<String, Object> body) {
        int assignmentId = (int) body.get("assignmentId");
        int studentId = (int) body.get("studentId");
        double score = ((Number) body.get("score")).doubleValue();
        boolean ok = instructorApi.updateComponentScore(userId, assignmentId, studentId, score);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@AuthenticationPrincipal Integer userId) {
        return ResponseEntity.ok(instructorApi.getAllCourseStatistics(userId));
    }

    @GetMapping("/pending-grades")
    public ResponseEntity<?> getPendingGrades(@AuthenticationPrincipal Integer userId) {
        return ResponseEntity.ok(instructorApi.getPendingGrades(userId));
    }

    @GetMapping("/deadline")
    public ResponseEntity<?> getDeadline() {
        return ResponseEntity.ok(instructorApi.getDeadline());
    }
}
