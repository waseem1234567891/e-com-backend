package com.chak.E_Commerce_Back_End.service.services.order;

import com.chak.E_Commerce_Back_End.exception.OrderNotFoundException;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.service.EmailService;
import com.chak.E_Commerce_Back_End.service.NotificationService;
import com.chak.E_Commerce_Back_End.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUpdateOrderStatusTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    // ------------------------
    // Helpers
    // ------------------------

    private Order createUserOrder() {

        User user = new User();
        user.setUsername("john");
        user.setEmail("john@test.com");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(100.0);

        return order;
    }

    private Order createGuestOrder() {

        Order order = new Order();
        order.setId(2L);
        order.setUser(null);
        order.setGuestEmail("guest@test.com");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(200.0);

        return order;
    }

    // ------------------------
    // TEST CASES
    // ------------------------

    @Test
    void updateOrderStatus_userOrder_shouldUpdateAndNotify() {

        Order order = createUserOrder();

        when(orderRepo.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepo.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        Order updated =
                orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        // Status updated
        assertEquals(OrderStatus.SHIPPED, updated.getStatus());

        // Email sent
        verify(emailService).sendOrderEmail(
                eq("john@test.com"),
                eq(1L),
                eq(OrderStatus.SHIPPED),
                eq(100.0)
        );

        // Notification sent
        verify(notificationService).sendUserNotification(
                eq("john"),
                any(Map.class)
        );

        // Saved
        verify(orderRepo).save(order);
    }

    @Test
    void updateOrderStatus_guestOrder_shouldSendOnlyEmail() {

        Order order = createGuestOrder();

        when(orderRepo.findById(2L))
                .thenReturn(Optional.of(order));

        when(orderRepo.save(any(Order.class)))
                .thenReturn(order);

        Order updated =
                orderService.updateOrderStatus(2L, OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, updated.getStatus());

        // Email sent to guest
        verify(emailService).sendOrderEmail(
                eq("guest@test.com"),
                eq(2L),
                eq(OrderStatus.CONFIRMED),
                eq(200.0)
        );

        // No notification for guest
        verify(notificationService, never())
                .sendUserNotification(any(), any());

        verify(orderRepo).save(order);
    }

    @Test
    void updateOrderStatus_shouldChangeFromOldToNew() {

        Order order = createUserOrder();

        when(orderRepo.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepo.save(any()))
                .thenReturn(order);

        orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    void updateOrderStatus_orderNotFound_shouldThrowException() {

        when(orderRepo.findById(99L))
                .thenReturn(Optional.empty());

        OrderNotFoundException ex =
                assertThrows(OrderNotFoundException.class, () ->
                        orderService.updateOrderStatus(99L, OrderStatus.SHIPPED)
                );

        assertTrue(ex.getMessage().contains("99"));

        verify(orderRepo, never()).save(any());

        verify(emailService, never()).sendOrderEmail(
                anyString(),
                anyLong(),
                any(OrderStatus.class),
                anyDouble()
        );

        verify(notificationService, never())
                .sendUserNotification(anyString(), any(Map.class));
    }
}

