package edu.univ.erp.domain;

/**
 * A simple data class representing one row in the course catalog.
 * This is a "domain" object.
 */
public class CatalogItem {

    private int sectionId = 0; // <-- ADD THIS
    private final String code;
    private final String title;
    private final int credits;
    private final String instructorName;
    private final int capacity;

    public CatalogItem(int sectionId, String code, String title, int credits, String instructorName, int capacity) {
        this.sectionId = this.sectionId; // <-- ADD THIS
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.instructorName = instructorName;
        this.capacity = capacity;
    }

    // --- Getters ---
    public int getSectionId() { return sectionId; } // <-- ADD THIS
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructorName() { return instructorName; }
    public int getCapacity() { return capacity; }
}