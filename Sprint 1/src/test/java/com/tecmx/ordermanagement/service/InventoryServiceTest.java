package com.tecmx.ordermanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tecmx.ordermanagement.exception.ResourceNotFoundException;
import com.tecmx.ordermanagement.exception.ValidationException;
import com.tecmx.ordermanagement.model.Product;
import com.tecmx.ordermanagement.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("PROD-001", "Laptop", 999.99, 10);
    }

    // =========================================================
    // registerProduct()
    // =========================================================
    @Nested
    @DisplayName("registerProduct()")
    class RegisterProductTests {

        @Test
        void shouldRegisterProductSuccessfully() {

            when(orderRepository.saveProduct(any(Product.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Product result = inventoryService.registerProduct("PROD-002", "Mouse", 29.99, 100);

            assertNotNull(result);
            assertEquals("PROD-002", result.getId());
            assertEquals("Mouse", result.getName());
            assertEquals(29.99, result.getPrice());
            assertEquals(100, result.getStockQuantity());

            verify(orderRepository, times(1)).saveProduct(any(Product.class));
        }

        @Test
        void shouldThrowValidationExceptionWhenIdIsNull() {
            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct(null, "Mouse", 29.99, 100));
        }

        @Test
        void shouldThrowValidationExceptionWhenIdIsEmpty() {
            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct("", "Mouse", 29.99, 100));
        }

        @Test
        void shouldThrowValidationExceptionWhenNameIsNull() {
            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct("PROD-002", null, 29.99, 100));
        }

        @Test
        void shouldThrowValidationExceptionWhenPriceIsInvalid() {

            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct("PROD-002", "Mouse", 0, 100));

            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct("PROD-002", "Mouse", -5, 100));
        }

        @Test
        void shouldThrowValidationExceptionWhenStockIsNegative() {
            assertThrows(ValidationException.class,
                    () -> inventoryService.registerProduct("PROD-002", "Mouse", 29.99, -1));
        }
    }

    // =========================================================
    // restockProduct()
    // =========================================================
    @Nested
    @DisplayName("restockProduct()")
    class RestockProductTests {

        @Test
        void shouldRestockSuccessfully() {

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.of(sampleProduct));

            when(orderRepository.saveProduct(any(Product.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Product result = inventoryService.restockProduct("PROD-001", 5);

            assertEquals(15, result.getStockQuantity());

            verify(orderRepository).saveProduct(any(Product.class));
        }

        @Test
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> inventoryService.restockProduct("PROD-001", 5));
        }

        @Test
        void shouldThrowValidationExceptionWhenAdditionalStockIsInvalid() {

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.of(sampleProduct));

            assertThrows(ValidationException.class,
                    () -> inventoryService.restockProduct("PROD-001", 0));

            assertThrows(ValidationException.class,
                    () -> inventoryService.restockProduct("PROD-001", -5));
        }
    }

    // =========================================================
    // checkStock()
    // =========================================================
    @Nested
    @DisplayName("checkStock()")
    class CheckStockTests {

        @Test
        void shouldReturnStockWhenProductExists() {

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.of(sampleProduct));

            int stock = inventoryService.checkStock("PROD-001");

            assertEquals(10, stock);
        }

        @Test
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {

            when(orderRepository.findProductById("PROD-001"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> inventoryService.checkStock("PROD-001"));
        }
    }
}