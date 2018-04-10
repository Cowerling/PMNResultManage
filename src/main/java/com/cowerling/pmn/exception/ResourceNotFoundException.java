package com.cowerling.pmn.exception;

public class ResourceNotFoundException extends Exception {
    private String reason;

    public ResourceNotFoundException(String reason) {
        this.reason = reason;
    }
}
