package com.tecmx.ordermanagement.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecmx.ordermanagement.exception.ValidationException;

/**
 * Represents a line item within an order.
 */
public class OrderItem {

    private static final Logger logger = LoggerFactory.getLogger(OrderItem.class);

    private Product product;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Calculates the subtotal for this order item (price * quantity).
     * Throws ValidationException if quantity <= 0.
     * Logs the calculation at DEBUG level.
     */
    public double getSubtotal() {

        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero", "quantity");
        }

        double price = product.getPrice();
        double subtotal = price * quantity;

        logger.debug("Calculated subtotal for product {}: {} x {} = {}",
                product.getId(), price, quantity, subtotal);

        return subtotal;
    }

    @Override
    public String toString() {
        return "OrderItem{product=" + product + ", quantity=" + quantity + "}";
    }
}