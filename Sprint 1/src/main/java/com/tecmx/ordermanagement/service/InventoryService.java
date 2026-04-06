package com.tecmx.ordermanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecmx.ordermanagement.exception.ResourceNotFoundException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Product;
import com.tecmx.ordermanagement.repository.OrderRepository;

/**
 * Inventory/product management service.
 *
 * Students must complete the implementation.
 */
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final OrderRepository orderRepository;

    public InventoryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Registers a new product in the inventory.
     */
    public Product registerProduct(String id, String name, double price, int stockQuantity) {

        if (id == null || id.isBlank()) {
            throw new ValidationException("Product id cannot be null or empty", "id");
        }

        if (name == null || name.isBlank()) {
            throw new ValidationException("Product name cannot be null or empty", "name");
        }

        if (price <= 0) {
            throw new ValidationException("Product price must be greater than zero", "price");
        }

        if (stockQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative", "stockQuantity");
        }

        Product product = new Product(id, name, price, stockQuantity);

        orderRepository.saveProduct(product);

        logger.info("Product registered: {} - {} (stock: {}, price: {})",
                id, name, stockQuantity, price);

        return product;
    }

    /**
     * Updates the stock of an existing product.
     */
    public Product restockProduct(String productId, int additionalStock) {

        Product product = orderRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", productId));

        if (additionalStock <= 0) {
            throw new ValidationException("Additional stock must be greater than zero", "additionalStock");
        }

        int newTotal = product.getStockQuantity() + additionalStock;
        product.setStockQuantity(newTotal);

        orderRepository.saveProduct(product);

        logger.info("Stock updated for {}: +{} → new stock: {}",
                productId, additionalStock, newTotal);

        return product;
    }

    /**
     * Checks the current stock of a product.
     */
    public int checkStock(String productId) {

        Product product = orderRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", productId));

        int currentStock = product.getStockQuantity();

        logger.debug("Stock check for {}: {}", productId, currentStock);

        return currentStock;
    }
}