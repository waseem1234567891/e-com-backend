package com.chak.E_Commerce_Back_End.service.services.order;


import com.chak.E_Commerce_Back_End.exception.OrderAlreadyCancelled;
import com.chak.E_Commerce_Back_End.exception.OrderNotFoundException;
import com.chak.E_Commerce_Back_End.model.*;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.repository.ProductStockHistoryRepo;


import com.chak.E_Commerce_Back_End.service.EmailService;
import com.chak.E_Commerce_Back_End.service.NotificationService;
import com.chak.E_Commerce_Back_End.service.OrderService;
import com.chak.E_Commerce_Back_End.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceCancelOrderTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private ProductService productService;

    @Mock
    private ProductStockHistoryRepo productStockHistoryRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    // ------------------------
    // Helpers
    // ------------------------

    private Order createOrder(Long orderId, Long userId) {

        User user = new User();
        user.setId(userId);
        user.setUsername("john");
        user.setEmail("john@test.com");

        Product product = new Product();
        product.setId(10L);
        product.setStock(5);

        OrderItem item = new OrderItem();
        item.setOrderItemId(1L);
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(100.0);
        order.setItems(List.of(item));

        return order;
    }

    // ------------------------
    // Tests
    // ------------------------

    @Test
    void cancelAnOrder_success_shouldRestoreStockAndCancel() {

        Order order = createOrder(1L, 5L);

        when(orderRepo.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepo.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        Order result = orderService.cancelAnOrder(1L, 5L);

        // Order cancelled
        assertEquals(OrderStatus.CANCELLED, result.getStatus());

        // Stock restored (5 + 2)
        assertEquals(7, order.getItems().get(0).getProduct().getStock());

        // Product updated
        verify(productService).addProduct(order.getItems().get(0).getProduct());

        // Stock history saved
        verify(productStockHistoryRepo).save(any(ProductStockHistory.class));

        // Email sent
        verify(emailService).sendOrderEmail(
                eq("john@test.com"),
                eq(1L),
                eq(OrderStatus.CANCELLED),
                eq(100.0)
        );

        // Notification sent
        verify(notificationService).sendUserNotification(
                eq("john"),
                any(Map.class)
        );

        verify(orderRepo).save(order);
    }

    @Test
    void cancelAnOrder_orderAlreadyCancelled_shouldThrow() {

        Order order = createOrder(1L, 5L);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepo.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(OrderAlreadyCancelled.class, () ->
                orderService.cancelAnOrder(1L, 5L)
        );

        verify(orderRepo, never()).save(any());
        verify(productService, never()).addProduct(any());
    }

    @Test
    void cancelAnOrder_unauthorizedUser_shouldThrowAccessDenied() {

        Order order = createOrder(1L, 5L);

        when(orderRepo.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () ->
                orderService.cancelAnOrder(1L, 99L)
        );

        verify(orderRepo, never()).save(any());
        verify(productService, never()).addProduct(any());
    }

    @Test
    void cancelAnOrder_orderNotFound_shouldThrow() {

        when(orderRepo.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderService.cancelAnOrder(99L, 1L)
        );

        verify(orderRepo, never()).save(any());
    }

    @Test
    void cancelAnOrder_shouldCreateStockHistoryWithCorrectValues() {

        Order order = createOrder(1L, 5L);

        when(orderRepo.findById(1L))
                .thenReturn(Optional.of(order));

        orderService.cancelAnOrder(1L, 5L);

        verify(productStockHistoryRepo).save(
                argThat(history ->
                        history.getQuantityChanged() == 2 &&
                                history.getStockAfterChange() == 7 &&
                                history.getReason().contains("Order Cancelled")
                )
        );
    }
}
