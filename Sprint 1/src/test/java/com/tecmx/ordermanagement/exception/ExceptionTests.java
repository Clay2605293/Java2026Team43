package com.tecmx.ordermanagement.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the custom exception classes.
 *
 * These tests validate that exceptions are built correctly, contain the
 * expected messages and codes, and maintain the correct hierarchy.
 */
class ExceptionTests {

    @Test
    @DisplayName("OrderManagementException should store message and errorCode")
    void orderManagementExceptionShouldStoreMessageAndCode() {
        OrderManagementException ex = new OrderManagementException("test error", "TEST_CODE");
        assertEquals("test error", ex.getMessage());
        assertEquals("TEST_CODE", ex.getErrorCode());
    }

    @Test
    @DisplayName("OrderManagementException should support chained cause")
    void orderManagementExceptionShouldSupportCause() {
        RuntimeException cause = new RuntimeException("root cause");
        OrderManagementException ex = new OrderManagementException("wrapper", "WRAP", cause);
        assertEquals("wrapper", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("ResourceNotFoundException should be an instance of OrderManagementException")
    void resourceNotFoundShouldExtendBase() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Order not found", "ORD-1");

        assertTrue(ex instanceof OrderManagementException);
    }

    @Test
    @DisplayName("ResourceNotFoundException should have errorCode RESOURCE_NOT_FOUND")
    void resourceNotFoundShouldHaveCorrectErrorCode() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Order not found", "ORD-1");

        assertEquals("RESOURCE_NOT_FOUND", ex.getErrorCode());
        assertEquals("ORD-1", ex.getResourceId());
    }

    @Test
    @DisplayName("ValidationException should be an instance of OrderManagementException")
    void validationExceptionShouldExtendBase() {
        ValidationException ex =
                new ValidationException("Invalid field");

        assertTrue(ex instanceof OrderManagementException);
    }

    @Test
    @DisplayName("ValidationException should store the fieldName")
    void validationExceptionShouldStoreFieldName() {
        ValidationException ex =
                new ValidationException("Invalid value", "price");

        assertEquals("price", ex.getFieldName());
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
    }

    @Test
    @DisplayName("BusinessRuleException should be an instance of OrderManagementException")
    void businessRuleShouldExtendBase() {
        BusinessRuleException ex =
                new BusinessRuleException("Rule violated");

        assertTrue(ex instanceof OrderManagementException);
    }

    @Test
    @DisplayName("BusinessRuleException should support constructor with cause")
    void businessRuleShouldSupportCause() {
        RuntimeException cause = new RuntimeException("root cause");

        BusinessRuleException ex =
                new BusinessRuleException("Rule violated", cause);

        assertEquals(cause, ex.getCause());
        assertEquals("BUSINESS_RULE_VIOLATION", ex.getErrorCode());
    }
}