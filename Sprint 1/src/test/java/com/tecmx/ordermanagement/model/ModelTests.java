package com.tecmx.ordermanagement.model;

import com.tecmx.ordermanagement.exception.BusinessRuleException;
import com.tecmx.ordermanagement.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the model classes (Order, OrderItem).
 *
 * These tests do NOT require Mockito — they are pure unit tests.
 */
class ModelTests {

    // =========================================================================
    // OrderItem.getSubtotal() tests
    // =========================================================================

    @Test
    @DisplayName("OrderItem.getSubtotal() should correctly calculate the subtotal")
    void orderItemSubtotalShouldBeCorrect() {

        // Arrange
        Product product = new Product("P1", "Laptop", 100.0, 10);
        OrderItem item = new OrderItem(product, 3);

        // Act
        double subtotal = item.getSubtotal();

        // Assert
        assertEquals(300.0, subtotal);
    }

    @Test
    @DisplayName("OrderItem.getSubtotal() should throw ValidationException if quantity <= 0")
    void orderItemSubtotalShouldThrowWhenQuantityInvalid() {

        // Arrange
        Product product = new Product("P1", "Laptop", 100.0, 10);
        OrderItem item = new OrderItem(product, 0);

        // Act + Assert
        assertThrows(ValidationException.class, item::getSubtotal);
    }

    // =========================================================================
    // Order.getTotal() tests
    // =========================================================================

    @Test
    @DisplayName("Order.getTotal() should calculate the sum of all subtotals")
    void orderTotalShouldSumAllSubtotals() {

        // Arrange
        Product p1 = new Product("P1", "Mouse", 50.0, 10);
        Product p2 = new Product("P2", "Keyboard", 30.0, 10);

        OrderItem item1 = new OrderItem(p1, 2); // 100
        OrderItem item2 = new OrderItem(p2, 3); // 90

        Order order = new Order("O1", "C1");
        order.addItem(item1);
        order.addItem(item2);

        // Act
        double total = order.getTotal();

        // Assert
        assertEquals(190.0, total);
    }

    @Test
    @DisplayName("Order.getTotal() should throw BusinessRuleException if it has no items")
    void orderTotalShouldThrowWhenNoItems() {

        // Arrange
        Order order = new Order("O1", "C1");

        // Act + Assert
        assertThrows(BusinessRuleException.class, order::getTotal);
    }

    // =========================================================================
    // Constructor and getter/setter tests
    // =========================================================================

    @Test
    @DisplayName("Order should be created with CREATED status by default")
    void orderShouldHaveCreatedStatusByDefault() {

        // Arrange
        Order order = new Order("O1", "C1");

        // Assert
        assertEquals(Order.Status.CREATED, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Product should correctly store all its fields")
    void productShouldStoreAllFields() {

        // Arrange
        Product product = new Product("P1", "Laptop", 999.99, 5);

        // Assert
        assertEquals("P1", product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice());
        assertEquals(5, product.getStockQuantity());
    }
}