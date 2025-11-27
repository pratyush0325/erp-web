package edu.univ.erp.domain;

public class CatalogItem {

    private final int sectionId;
    private final String code;
    private final String title;
    private final int credits;
    private final String instructorName;
    private final int capacity;
    // --- NEW FIELDS ---
    private final String semester;
    private final int year;

    public CatalogItem(int sectionId, String code, String title, int credits, String instructorName, int capacity, String semester, int year) {
        this.sectionId = sectionId;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.instructorName = instructorName;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    public int getSectionId() { return sectionId; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructorName() { return instructorName; }
    public int getCapacity() { return capacity; }
    public String getSemester() { return semester; } // <--- New Getter
    public int getYear() { return year; }            // <--- New Getter
}