package com.tecmx.ordermanagement.exception;

public class BusinessRuleException extends OrderManagementException {

    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, "BUSINESS_RULE_VIOLATION", cause);
    }
}