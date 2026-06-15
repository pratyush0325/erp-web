package edu.univ.erp.controller;

import edu.univ.erp.api.admin.AdminApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminApi adminApi;

    public AdminController(AdminApi adminApi) {
        this.adminApi = adminApi;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() { return ResponseEntity.ok(adminApi.getUsers()); }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody Map<String, String> body) {
        boolean ok = adminApi.addUser(body.get("username"), body.get("password"), body.get("role"),
                body.get("extra1"), body.get("extra2"), body.get("extra3"));
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        return ResponseEntity.ok(Map.of("success", adminApi.deleteUser(userId)));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable int userId, @RequestBody Map<String, String> body) {
        boolean ok = adminApi.toggleUserStatus(userId, body.get("currentStatus"));
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getCourses() { return ResponseEntity.ok(adminApi.getCourses()); }

    @PostMapping("/courses")
    public ResponseEntity<?> addCourse(@RequestBody Map<String, Object> body) {
        boolean ok = adminApi.addCourse((String) body.get("code"), (String) body.get("title"),
                (int) body.get("credits"));
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @DeleteMapping("/courses/{code}")
    public ResponseEntity<?> deleteCourse(@PathVariable String code) {
        return ResponseEntity.ok(Map.of("success", adminApi.deleteCourse(code)));
    }

    @GetMapping("/sections")
    public ResponseEntity<?> getSections() { return ResponseEntity.ok(adminApi.getSections()); }

    @PostMapping("/sections")
    public ResponseEntity<?> addSection(@RequestBody Map<String, Object> body) {
        boolean ok = adminApi.addSection(
                (String) body.get("courseId"),
                (int) body.get("instructorId"),
                (String) body.get("dayTime"),
                (String) body.get("room"),
                (int) body.get("capacity"),
                (String) body.get("semester"),
                (int) body.get("year"));
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PatchMapping("/sections/{sectionId}")
    public ResponseEntity<?> updateSection(@PathVariable int sectionId, @RequestBody Map<String, Object> body) {
        boolean ok = adminApi.updateSection(sectionId, (String) body.get("dayTime"),
                (String) body.get("room"), (int) body.get("capacity"));
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<?> deleteSection(@PathVariable int sectionId) {
        return ResponseEntity.ok(Map.of("success", adminApi.deleteSection(sectionId)));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() { return ResponseEntity.ok(adminApi.getStats()); }

    @GetMapping("/maintenance")
    public ResponseEntity<?> getMaintenance() {
        return ResponseEntity.ok(Map.of("maintenanceOn", adminApi.isMaintenanceOn()));
    }

    @PostMapping("/maintenance")
    public ResponseEntity<?> setMaintenance(@RequestBody Map<String, Boolean> body) {
        adminApi.setMaintenance(body.get("enabled"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deadline")
    public ResponseEntity<?> getDeadline() {
        return ResponseEntity.ok(Map.of("deadline", adminApi.getDeadline()));
    }

    @PostMapping("/deadline")
    public ResponseEntity<?> setDeadline(@RequestBody Map<String, String> body) {
        adminApi.setDeadline(body.get("deadline"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/backup")
    public ResponseEntity<?> backup(@RequestBody Map<String, String> body) {
        boolean ok = adminApi.triggerBackup(new File(body.get("path")));
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PostMapping("/restore")
    public ResponseEntity<?> restore(@RequestBody Map<String, String> body) {
        boolean ok = adminApi.triggerRestore(new File(body.get("path")));
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @GetMapping("/instructors")
    public ResponseEntity<?> getInstructors() { return ResponseEntity.ok(adminApi.getInstructors()); }
}
