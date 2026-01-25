package com.chak.E_Commerce_Back_End.service.services.order;



import com.chak.E_Commerce_Back_End.dto.order.OrderItemDTO;
import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.model.*;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;


import com.chak.E_Commerce_Back_End.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceGetOrderByUserIdTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepo orderRepo;

    // -----------------------
    // Helper
    // -----------------------

    private Order createOrder(Long id) {

        Order order = new Order();
        order.setId(id);
        order.setTotalAmount(150.0);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus("Paid");
        order.setShippingAddress("NY Street");
        order.setPaymentMethod("CARD");
        order.setOrderDate(LocalDateTime.now());

        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");

        OrderItem item = new OrderItem();
        item.setOrderItemId(100L);
        item.setProduct(product);
        item.setQuantity(2);
        item.setOrder(order);

        order.setItems(List.of(item));

        return order;
    }

    // -----------------------
    // Tests
    // -----------------------

    @Test
    void getOrderByUserId_shouldReturnMappedOrders() {

        List<Order> orders = List.of(
                createOrder(1L),
                createOrder(2L)
        );

        when(orderRepo.findByUser_Id(5L))
                .thenReturn(orders);

        List<OrderResponseDTO> result =
                orderService.getOrderByUserId(5L);

        assertEquals(2, result.size());

        OrderResponseDTO dto = result.get(0);

        assertEquals(1L, dto.getId());
        assertEquals(150.0, dto.getTotalAmount());
        assertEquals(OrderStatus.CONFIRMED, dto.getStatus());
        assertEquals("Paid", dto.getPaymentStatus());
        assertEquals("NY Street", dto.getShippingAddress());
        assertEquals("CARD", dto.getPaymentMethod());

        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());

        OrderItemDTO itemDTO = dto.getItems().get(0);

        assertEquals(2, itemDTO.getQuantity());
        assertEquals(10L, itemDTO.getProductId());
    }

    @Test
    void getOrderByUserId_noOrders_shouldReturnEmptyList() {

        when(orderRepo.findByUser_Id(99L))
                .thenReturn(Collections.emptyList());

        List<OrderResponseDTO> result =
                orderService.getOrderByUserId(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderByUserId_shouldCallRepositoryOnce() {

        when(orderRepo.findByUser_Id(anyLong()))
                .thenReturn(Collections.emptyList());

        orderService.getOrderByUserId(1L);

        verify(orderRepo, times(1))
                .findByUser_Id(1L);
    }
}

