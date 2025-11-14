package edu.univ.erp.api.student;

public enum RegistrationStatus {
    SUCCESS,
    ALREADY_REGISTERED,
    SECTION_FULL,
    ERROR_UNKNOWN,
    // We'll add this later when we build the admin module
    // ERROR_MAINTENANCE_MODE
}