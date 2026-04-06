package com.tecmx.ordermanagement.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecmx.ordermanagement.exception.BusinessRuleException;

/**
 * Represents a purchase order.
 */
public class Order {

    private static final Logger logger = LoggerFactory.getLogger(Order.class);

    public enum Status {
        CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }

    private String id;
    private String customerId;
    private List<OrderItem> items;
    private Status status;
    private LocalDateTime createdAt;

    public Order() {
        this.items = new ArrayList<>();
        this.status = Status.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public Order(String id, String customerId) {
        this();
        this.id = id;
        this.customerId = customerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    /**
     * Calculates the total cost of the order.
     */
    public double getTotal() {

        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("Cannot calculate total for an order with no items");
        }

        double total = 0.0;

        for (OrderItem item : items) {
            total += item.getSubtotal();
        }

        logger.info("Calculated total for order {}: {}", id, total);

        return total;
    }

    @Override
    public String toString() {
        return "Order{id='" + id + "', customerId='" + customerId + "', status=" + status
                + ", items=" + items.size() + ", createdAt=" + createdAt + "}";
    }
}