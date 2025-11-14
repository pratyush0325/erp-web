package edu.univ.erp.domain;

/**
 * A simple data class representing one row in the "My Registrations" list.
 * This is a "domain" object.
 */
public class RegistrationItem {

    private final int sectionId;
    private final String section;
    private final String title;
    private final String schedule;
    private final String room;
    private final String status;

    public RegistrationItem(int sectionId, String section, String title, String schedule, String room, String status) {
        this.sectionId = sectionId;
        this.section = section;
        this.title = title;
        this.schedule = schedule;
        this.room = room;
        this.status = status;
    }

    // --- Getters ---
    public int getSectionId() { return sectionId; }
    public String getSection() { return section; }
    public String getTitle() { return title; }
    public String getSchedule() { return schedule; }
    public String getRoom() { return room; }
    public String getStatus() { return status; }
}