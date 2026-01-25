package com.chak.E_Commerce_Back_End.service.services.order;

import com.chak.E_Commerce_Back_End.dto.order.OrderDTO;
import com.chak.E_Commerce_Back_End.dto.order.OrderItemDTO;
import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.exception.NotEnoughStock;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.ProductStockHistory;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.repository.ProductStockHistoryRepo;
import com.chak.E_Commerce_Back_End.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private ProductStockHistoryRepo productStockHistoryRepo;

    @Mock
    private NotificationService notificationService;

    // ------------------------------
    // Helper Methods
    // ------------------------------

    private Product createProduct(Long id, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName("Product " + id);
        p.setStock(stock);
        return p;
    }

    private OrderDTO createOrderDTO(int quantity) {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(1L);
        item.setQuantity(quantity);

        OrderDTO dto = new OrderDTO();
        dto.setItems(List.of(item));
        dto.setPaymentMethod("COD");
        dto.setTotalAmount(100.0);
        dto.setShippingAddress("Test Address");

        return dto;
    }

    private User createUser() {
        User u = new User();
        u.setEmail("test@test.com");
        u.setUsername("testuser");
        return u;
    }

    // ------------------------------
    // ✅ SUCCESS CASES
    // ------------------------------

    @Test
    void placeOrder_loggedInUser_success() {

        OrderDTO dto = createOrderDTO(2);
        Product product = createProduct(1L, 10);
        User user = createUser();

        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(1L)).thenReturn(product);
        when(productService.addProduct(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderRepo.save(any(Order.class)))
                .thenAnswer(i -> {
                    Order o = i.getArgument(0);
                    o.setId(1L);
                    return o;
                });

        OrderResponseDTO response = orderService.placeOrder(dto);

        // Verify stock deducted
        assertEquals(8, product.getStock());

        // Verify order saved
        verify(orderRepo).save(any(Order.class));

        // Verify stock history saved
        verify(productStockHistoryRepo).save(any(ProductStockHistory.class));

        // Verify email sent
        verify(emailService).sendOrderEmail(
                eq("test@test.com"),
                eq(1L),
                eq(OrderStatus.PENDING),
                eq(100.0)
        );

        // Verify notification sent
        verify(notificationService).sendUserNotification(
                eq("testuser"),
                anyMap()
        );
    }

    @Test
    void placeOrder_guestUser_success() {

        OrderDTO dto = createOrderDTO(1);
        dto.setGuestName("Guest");
        dto.setGuestEmail("guest@test.com");

        Product product = createProduct(1L, 5);

        when(userService.getCurrentUser()).thenThrow(new RuntimeException());
        when(productService.getProductbyId(1L)).thenReturn(product);
        when(productService.addProduct(any())).thenAnswer(i -> i.getArgument(0));

        when(orderRepo.save(any()))
                .thenAnswer(i -> {
                    Order o = i.getArgument(0);
                    o.setId(2L);
                    return o;
                });

        orderService.placeOrder(dto);

        verify(emailService).sendOrderEmail(
                eq("guest@test.com"),
                eq(2L),
                eq(OrderStatus.PENDING),
                eq(100.0)
        );

        verify(notificationService, never()).sendUserNotification(any(), any());
    }

    // ------------------------------
    // ❌ FAILURE CASES
    // ------------------------------

    @Test
    void placeOrder_notEnoughStock_throwsException() {

        OrderDTO dto = createOrderDTO(10);
        Product product = createProduct(1L, 5);

        when(userService.getCurrentUser()).thenReturn(createUser());
        when(productService.getProductbyId(1L)).thenReturn(product);

        assertThrows(NotEnoughStock.class, () -> {
            orderService.placeOrder(dto);
        });

        verify(orderRepo, never()).save(any());
        verify(productStockHistoryRepo, never()).save(any());
    }

    // ------------------------------
    // 🔍 MULTIPLE PRODUCTS
    // ------------------------------

    @Test
    void placeOrder_multipleProducts_success() {

        OrderItemDTO item1 = new OrderItemDTO();
        item1.setProductId(1L);
        item1.setQuantity(2);

        OrderItemDTO item2 = new OrderItemDTO();
        item2.setProductId(2L);
        item2.setQuantity(3);

        OrderDTO dto = new OrderDTO();
        dto.setItems(List.of(item1, item2));
        dto.setPaymentMethod("COD");
        dto.setTotalAmount(300.00);
        dto.setShippingAddress("Addr");

        Product p1 = createProduct(1L, 10);
        Product p2 = createProduct(2L, 10);

        when(userService.getCurrentUser()).thenReturn(createUser());
        when(productService.getProductbyId(1L)).thenReturn(p1);
        when(productService.getProductbyId(2L)).thenReturn(p2);
        when(productService.addProduct(any())).thenAnswer(i -> i.getArgument(0));

        when(orderRepo.save(any())).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(3L);
            return o;
        });

        orderService.placeOrder(dto);

        assertEquals(8, p1.getStock());
        assertEquals(7, p2.getStock());

        verify(productStockHistoryRepo, times(2)).save(any());
    }

    // ------------------------------
    // ⚠️ EDGE CASE
    // ------------------------------

    @Test
    void placeOrder_emptyItems_shouldStillSaveOrder() {

        OrderDTO dto = new OrderDTO();
        dto.setItems(new ArrayList<>());
        dto.setPaymentMethod("COD");
        dto.setTotalAmount(0.0);
        dto.setShippingAddress("Addr");

        when(userService.getCurrentUser()).thenReturn(createUser());
        when(orderRepo.save(any())).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(5L);
            return o;
        });

        orderService.placeOrder(dto);

        verify(orderRepo).save(any());
        verify(productStockHistoryRepo, never()).save(any());
    }





}
