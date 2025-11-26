package edu.univ.erp.api.admin;

import edu.univ.erp.data.AdminStore;
import edu.univ.erp.domain.UserAdminItem;
import java.util.List;

public class AdminApi {

    private final AdminStore adminStore = new AdminStore();

    public List<UserAdminItem> getUsers() {
        return adminStore.getAllUsers();
    }

    public boolean addUser(String username, String password, String role, String extra1, String extra2) {
        return adminStore.addUser(username, password, role, extra1, extra2);
    }

    public List<edu.univ.erp.domain.Course> getCourses() {
        return adminStore.getAllCourses();
    }

    public boolean addCourse(String code, String title, int credits) {
        return adminStore.addCourse(code, title, credits);
    }

    public List<edu.univ.erp.domain.SectionAdminItem> getSections() {
        return adminStore.getAllSections();
    }

    public boolean addSection(String courseId, int instructorId, String dayTime, String room, int capacity) {
        return adminStore.addSection(courseId, instructorId, dayTime, room, capacity);
    }

    public java.util.Map<Integer, String> getInstructors() {
        return adminStore.getInstructorsMap();
    }

    // --- MAINTENANCE ---
    public boolean isMaintenanceOn() {
        return edu.univ.erp.api.maintenance.MaintenanceApi.isMaintenanceOn();
    }

    public void setMaintenance(boolean on) {
        edu.univ.erp.api.maintenance.MaintenanceApi.setMaintenance(on);
    }
}