package com.tecmx.ordermanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecmx.ordermanagement.exception.BusinessRuleException;
import com.tecmx.ordermanagement.exception.ResourceNotFoundException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Order;
import com.tecmx.ordermanagement.model.OrderItem;
import com.tecmx.ordermanagement.model.Product;
import com.tecmx.ordermanagement.repository.OrderRepository;

/**
 * Main order management service.
 */
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String orderId, String customerId) {

        if (orderId == null || orderId.isBlank()) {
            throw new ValidationException("Order id cannot be null or empty", "orderId");
        }

        if (customerId == null || customerId.isBlank()) {
            throw new ValidationException("Customer id cannot be null or empty", "customerId");
        }

        if (orderRepository.existsOrderById(orderId)) {
            throw new BusinessRuleException("Order with id already exists");
        }

        Order order = new Order(orderId, customerId);

        orderRepository.saveOrder(order);

        logger.info("Order created: {} for customer: {}", orderId, customerId);

        return order;
    }

    public Order addProductToOrder(String orderId, String productId, int quantity) {

        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", orderId));

        Product product = orderRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", productId));

        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero", "quantity");
        }

        if (product.getStockQuantity() < quantity) {
            throw new BusinessRuleException("Insufficient stock for product");
        }

        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);

        OrderItem item = new OrderItem(product, quantity);
        order.addItem(item);

        orderRepository.saveOrder(order);
        orderRepository.saveProduct(product);

        logger.info("Added {}x {} to order {}", quantity, product.getName(), orderId);
        logger.debug("Remaining stock for {}: {}", productId, newStock);

        return order;
    }

    public Order confirmOrder(String orderId) {

        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", orderId));

        if (order.getStatus() != Order.Status.CREATED) {
            throw new BusinessRuleException("Order cannot be confirmed in its current state");
        }

        if (order.getItems().isEmpty()) {
            throw new BusinessRuleException("Order must have at least one item to confirm");
        }

        order.setStatus(Order.Status.CONFIRMED);

        orderRepository.saveOrder(order);

        logger.info("Order {} confirmed with {} items, total: {}",
                orderId, order.getItems().size(), order.getTotal());

        return order;
    }

    public Order cancelOrder(String orderId) {

        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", orderId));

        if (order.getStatus() == Order.Status.DELIVERED ||
            order.getStatus() == Order.Status.CANCELLED) {

            throw new BusinessRuleException("Order cannot be cancelled in its current state");
        }

        // BONUS: restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int restoredStock = product.getStockQuantity() + item.getQuantity();
            product.setStockQuantity(restoredStock);
            orderRepository.saveProduct(product);
        }

        order.setStatus(Order.Status.CANCELLED);

        orderRepository.saveOrder(order);

        logger.warn("Order {} has been cancelled", orderId);

        return order;
    }

    public Order getOrder(String orderId) {

        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", orderId));

        logger.debug("Retrieved order: {}", orderId);

        return order;
    }
}