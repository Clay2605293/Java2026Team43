package com.tecmx.ordermanagement.exception;

/**
 * Thrown when there is a validation error in the input data.
 */
public class ValidationException extends OrderManagementException {

    private final String fieldName;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.fieldName = null;
    }

    public ValidationException(String message, String fieldName) {
        super(message, "VALIDATION_ERROR");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}