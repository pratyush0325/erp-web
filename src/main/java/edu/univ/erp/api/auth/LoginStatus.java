package edu.univ.erp.api.auth;

public enum LoginStatus {
    SUCCESS,
    USER_NOT_FOUND,
    INVALID_PASSWORD,
    ACCOUNT_LOCKED,
    ACCOUNT_INACTIVE
}