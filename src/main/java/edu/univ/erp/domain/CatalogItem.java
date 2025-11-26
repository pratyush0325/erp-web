package edu.univ.erp.domain;

public class CatalogItem {

    private int sectionId;
    private final String code;
    private final String title;
    private final int credits;
    private final String instructorName;
    private final int capacity;

    public CatalogItem(int sectionId, String code, String title, int credits, String instructorName, int capacity) {
        this.sectionId = sectionId; // <--- FIXED (removed "this." from the right side)
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.instructorName = instructorName;
        this.capacity = capacity;
    }

    // --- Getters ---
    public int getSectionId() { return sectionId; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructorName() { return instructorName; }
    public int getCapacity() { return capacity; }
}