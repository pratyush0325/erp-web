package edu.univ.erp.domain;

public class InstructorCourseItem {
    private final int sectionId;
    private final String courseCode;
    private final String courseTitle;
    private final String schedule;
    private final String room;
    private final int enrolledCount;
    private final int capacity;

    public InstructorCourseItem(int sectionId, String courseCode, String courseTitle, String schedule, String room, int enrolledCount, int capacity) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.schedule = schedule;
        this.room = room;
        this.enrolledCount = enrolledCount;
        this.capacity = capacity;
    }

    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getSchedule() { return schedule; }
    public String getRoom() { return room; }
    public int getEnrolledCount() { return enrolledCount; }
    public int getCapacity() { return capacity; }
}


