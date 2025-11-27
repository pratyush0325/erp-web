package edu.univ.erp.domain;

public class AdminStats {
    private final int totalUsers;
    private final int totalCourses;
    private final int totalSections;
    private final boolean maintenanceOn;

    public AdminStats(int totalUsers, int totalCourses, int totalSections, boolean maintenanceOn) {
        this.totalUsers = totalUsers;
        this.totalCourses = totalCourses;
        this.totalSections = totalSections;
        this.maintenanceOn = maintenanceOn;
    }

    public int getTotalUsers() { return totalUsers; }
    public int getTotalCourses() { return totalCourses; }
    public int getTotalSections() { return totalSections; }
    public boolean isMaintenanceOn() { return maintenanceOn; }
}