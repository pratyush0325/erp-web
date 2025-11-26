package edu.univ.erp.domain;

public class SectionAdminItem {
    private final int sectionId;
    private final String courseCode;
    private final String instructorName; // "Prof. Smith"
    private final String schedule;       // "Mon 10:00"
    private final int capacity;

    public SectionAdminItem(int sectionId, String courseCode, String instructorName, String schedule, int capacity) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.instructorName = instructorName;
        this.schedule = schedule;
        this.capacity = capacity;
    }

    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getInstructorName() { return instructorName; }
    public String getSchedule() { return schedule; }
    public int getCapacity() { return capacity; }
}