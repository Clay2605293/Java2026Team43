package com.tecmx.ordermanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.tecmx.ordermanagement.exception.BusinessRuleException;
import com.tecmx.ordermanagement.exception.ResourceNotFoundException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Order;
import com.tecmx.ordermanagement.model.OrderItem;
import com.tecmx.ordermanagement.model.Product;
import com.tecmx.ordermanagement.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Product sampleProduct;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("PROD-001", "Laptop", 999.99, 10);
        sampleOrder = new Order("ORD-001", "CUST-001");
    }

    // =========================================================
    // createOrder()
    // =========================================================
    @Nested
    @DisplayName("createOrder()")
    class CreateOrderTests {

        @Test
        void shouldCreateOrderSuccessfully() {

            when(orderRepository.existsOrderById("ORD-001")).thenReturn(false);
            when(orderRepository.saveOrder(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Order result = orderService.createOrder("ORD-001", "CUST-001");

            assertNotNull(result);
            assertEquals("ORD-001", result.getId());
            assertEquals("CUST-001", result.getCustomerId());

            verify(orderRepository, times(1)).saveOrder(any(Order.class));
        }

        @Test
        void shouldThrowValidationExceptionWhenOrderIdIsNull() {
            assertThrows(ValidationException.class,
                    () -> orderService.createOrder(null, "CUST-001"));
        }

        @Test
        void shouldThrowValidationExceptionWhenOrderIdIsEmpty() {
            assertThrows(ValidationException.class,
                    () -> orderService.createOrder("", "CUST-001"));
        }

        @Test
        void shouldThrowValidationExceptionWhenCustomerIdIsNull() {
            assertThrows(ValidationException.class,
                    () -> orderService.createOrder("ORD-001", null));
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenOrderAlreadyExists() {

            when(orderRepository.existsOrderById("ORD-001")).thenReturn(true);

            assertThrows(BusinessRuleException.class,
                    () -> orderService.createOrder("ORD-001", "CUST-001"));

            verify(orderRepository, never()).saveOrder(any());
        }
    }

    // =========================================================
    // addProductToOrder()
    // =========================================================
    @Nested
    @DisplayName("addProductToOrder()")
    class AddProductToOrderTests {

        @Test
        void shouldAddProductToOrderSuccessfully() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.of(sampleProduct));

            when(orderRepository.saveOrder(any(Order.class)))
                    .thenAnswer(i -> i.getArgument(0));

            when(orderRepository.saveProduct(any(Product.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Order result = orderService.addProductToOrder("ORD-001", "PROD-001", 3);

            assertEquals(1, result.getItems().size());
            assertEquals(7, sampleProduct.getStockQuantity());

            verify(orderRepository).saveOrder(any());
            verify(orderRepository).saveProduct(any());
        }

        @Test
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.addProductToOrder("ORD-001", "PROD-001", 3));
        }

        @Test
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.addProductToOrder("ORD-001", "PROD-001", 3));
        }

        @Test
        void shouldThrowValidationExceptionWhenQuantityInvalid() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.of(sampleProduct));

            assertThrows(ValidationException.class,
                    () -> orderService.addProductToOrder("ORD-001", "PROD-001", 0));

            assertThrows(ValidationException.class,
                    () -> orderService.addProductToOrder("ORD-001", "PROD-001", -1));
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenInsufficientStock() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.of(sampleProduct));

            assertThrows(BusinessRuleException.class,
                    () -> orderService.addProductToOrder("ORD-001", "PROD-001", 15));

            verify(orderRepository, never()).saveOrder(any());
            verify(orderRepository, never()).saveProduct(any());
        }
    }

    // =========================================================
    // confirmOrder()
    // =========================================================
    @Nested
    @DisplayName("confirmOrder()")
    class ConfirmOrderTests {

        @Test
        void shouldConfirmOrderSuccessfully() {

            sampleOrder.addItem(new OrderItem(sampleProduct, 1));

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            when(orderRepository.saveOrder(any(Order.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Order result = orderService.confirmOrder("ORD-001");

            assertEquals(Order.Status.CONFIRMED, result.getStatus());
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenOrderNotCreated() {

            sampleOrder.setStatus(Order.Status.CONFIRMED);

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            assertThrows(BusinessRuleException.class,
                    () -> orderService.confirmOrder("ORD-001"));
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenOrderHasNoItems() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            assertThrows(BusinessRuleException.class,
                    () -> orderService.confirmOrder("ORD-001"));
        }

        @Test
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.confirmOrder("ORD-001"));
        }
    }

    // =========================================================
    // cancelOrder()
    // =========================================================
    @Nested
    @DisplayName("cancelOrder()")
    class CancelOrderTests {

        @Test
        void shouldCancelCreatedOrder() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            when(orderRepository.saveOrder(any(Order.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Order result = orderService.cancelOrder("ORD-001");

            assertEquals(Order.Status.CANCELLED, result.getStatus());
        }

        @Test
        void shouldCancelConfirmedOrder() {

            sampleOrder.setStatus(Order.Status.CONFIRMED);

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            when(orderRepository.saveOrder(any(Order.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Order result = orderService.cancelOrder("ORD-001");

            assertEquals(Order.Status.CANCELLED, result.getStatus());
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenOrderDelivered() {

            sampleOrder.setStatus(Order.Status.DELIVERED);

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            assertThrows(BusinessRuleException.class,
                    () -> orderService.cancelOrder("ORD-001"));
        }

        @Test
        void shouldThrowBusinessRuleExceptionWhenOrderAlreadyCancelled() {

            sampleOrder.setStatus(Order.Status.CANCELLED);

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            assertThrows(BusinessRuleException.class,
                    () -> orderService.cancelOrder("ORD-001"));
        }

        @Test
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.cancelOrder("ORD-001"));
        }
    }

    // =========================================================
    // getOrder()
    // =========================================================
    @Nested
    @DisplayName("getOrder()")
    class GetOrderTests {

        @Test
        void shouldReturnOrderWhenFound() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.of(sampleOrder));

            Order result = orderService.getOrder("ORD-001");

            assertNotNull(result);
            assertEquals("ORD-001", result.getId());
        }

        @Test
        void shouldThrowResourceNotFoundWhenOrderDoesNotExist() {

            when(orderRepository.findOrderById("ORD-001"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.getOrder("ORD-001"));
        }
    }
}