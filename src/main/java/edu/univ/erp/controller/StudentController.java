package edu.univ.erp.controller;

import edu.univ.erp.api.student.StudentApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentApi studentApi;

    public StudentController(StudentApi studentApi) {
        this.studentApi = studentApi;
    }

    @GetMapping("/registrations")
    public ResponseEntity<?> getRegistrations(@AuthenticationPrincipal Integer userId) {
        return ResponseEntity.ok(studentApi.getMyRegistrations(userId));
    }

    @PostMapping("/register/{sectionId}")
    public ResponseEntity<?> register(@AuthenticationPrincipal Integer userId, @PathVariable int sectionId) {
        return ResponseEntity.ok(studentApi.registerForSection(userId, sectionId));
    }

    @DeleteMapping("/drop/{sectionId}")
    public ResponseEntity<?> drop(@AuthenticationPrincipal Integer userId, @PathVariable int sectionId) {
        return ResponseEntity.ok(studentApi.dropSection(userId, sectionId));
    }

    @GetMapping("/grades")
    public ResponseEntity<?> getGrades(@AuthenticationPrincipal Integer userId) {
        return ResponseEntity.ok(studentApi.getMyGrades(userId));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Integer userId) {
        return ResponseEntity.ok(studentApi.getProfile(userId));
    }

    @GetMapping("/deadline")
    public ResponseEntity<?> getDeadline() {
        return ResponseEntity.ok(studentApi.getDeadline());
    }
}
