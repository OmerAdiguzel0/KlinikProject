package com.psychiatryclinic.core.exceptions;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class ValidationErrorResponse {
    private int status;
    private long timestamp;
    private Map<String, String> errors = new HashMap<>();

    public void addError(String field, String message) {
        errors.put(field, message);
    }
} 