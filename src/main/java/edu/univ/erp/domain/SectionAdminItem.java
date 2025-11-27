package edu.univ.erp.domain;

public class SectionAdminItem {
    private final int sectionId;
    private final String courseCode;
    private final String instructorName;
    private final String schedule;
    private final int capacity;
    // --- NEW FIELDS ---
    private final String semester;
    private final int year;

    public SectionAdminItem(int sectionId, String courseCode, String instructorName, String schedule, int capacity, String semester, int year) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.instructorName = instructorName;
        this.schedule = schedule;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getInstructorName() { return instructorName; }
    public String getSchedule() { return schedule; }
    public int getCapacity() { return capacity; }
    public String getSemester() { return semester; } // <--- New Getter
    public int getYear() { return year; }            // <--- New Getter
}