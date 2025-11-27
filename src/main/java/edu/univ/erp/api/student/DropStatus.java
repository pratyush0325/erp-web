package edu.univ.erp.api.student;

public enum DropStatus {
    SUCCESS,
    NOT_REGISTERED, // Failsafe check
    ERROR_UNKNOWN,
    MAINTENANCE_MODE,
    DEADLINE_PASSED
    // We can add these later
    // ERROR_PAST_DEADLINE,
    // ERROR_MAINTENANCE_MODE
}