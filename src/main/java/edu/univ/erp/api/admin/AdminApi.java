package edu.univ.erp.api.admin;

import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.data.AdminStore;
import edu.univ.erp.data.BackupManager;
import edu.univ.erp.data.SettingsStore;
import edu.univ.erp.domain.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminApi {

    private final AdminStore adminStore;
    private final SettingsStore settingsStore;
    private final BackupManager backupManager;
    private final MaintenanceApi maintenanceApi;

    public AdminApi(AdminStore adminStore, SettingsStore settingsStore,
                    BackupManager backupManager, MaintenanceApi maintenanceApi) {
        this.adminStore = adminStore;
        this.settingsStore = settingsStore;
        this.backupManager = backupManager;
        this.maintenanceApi = maintenanceApi;
    }

    public List<UserAdminItem> getUsers() { return adminStore.getAllUsers(); }

    public String getDeadline() {
        LocalDate date = settingsStore.getRegistrationDeadline();
        return (date != null) ? date.toString() : "";
    }

    public void setDeadline(String date) { settingsStore.setRegistrationDeadline(date); }

    public boolean addUser(String username, String password, String role,
                           String extra1, String extra2, String extra3) {
        return adminStore.addUser(username, password, role, extra1, extra2, extra3);
    }

    public List<Course> getCourses() { return adminStore.getAllCourses(); }

    public boolean addCourse(String code, String title, int credits) {
        return adminStore.addCourse(code, title, credits);
    }

    public List<SectionAdminItem> getSections() { return adminStore.getAllSections(); }

    public Map<Integer, String> getInstructors() { return adminStore.getInstructorsMap(); }

    public boolean isMaintenanceOn() { return maintenanceApi.isMaintenanceOn(); }

    public void setMaintenance(boolean on) { maintenanceApi.setMaintenance(on); }

    public AdminStats getStats() { return adminStore.getDashboardStats(); }

    public boolean deleteUser(int userId) { return adminStore.deleteUser(userId); }

    public boolean deleteCourse(String code) { return adminStore.deleteCourse(code); }

    public boolean deleteSection(int sectionId) { return adminStore.deleteSection(sectionId); }

    public boolean updateSection(int sectionId, String dayTime, String room, int capacity) {
        return adminStore.updateSection(sectionId, dayTime, room, capacity);
    }

    public boolean addSection(String courseId, int instructorId, String dayTime, String room,
                              int capacity, String semester, int year) {
        return adminStore.addSection(courseId, instructorId, dayTime, room, capacity, semester, year);
    }

    public boolean toggleUserStatus(int userId, String currentStatus) {
        String newStatus = "Active".equalsIgnoreCase(currentStatus) ? "Inactive" : "Active";
        return adminStore.updateUserStatus(userId, newStatus);
    }

    public boolean triggerBackup(File file) { return backupManager.backup(file); }

    public boolean triggerRestore(File file) { return backupManager.restore(file); }
}
