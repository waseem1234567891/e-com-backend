package com.chak.E_Commerce_Back_End.service.services.order;

import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceGetAllOrdersTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepo orderRepo;

    // ------------------------
    // Helpers
    // ------------------------

    private Page<Order> mockPage() {

        Order o1 = new Order();
        o1.setId(1L);
        o1.setItems(new ArrayList<>()); // IMPORTANT

        Order o2 = new Order();
        o2.setId(2L);
        o2.setItems(new ArrayList<>()); // IMPORTANT

        return new PageImpl<>(List.of(o1, o2));
    }

    // ------------------------
    // TEST CASES
    // ------------------------

    @Test
    void getAllOrders_activeWithSearch() {

        when(orderRepo.findByStatusInAndSearch(anyList(), eq("john"), any()))
                .thenReturn(mockPage());

        Page<OrderResponseDTO> result =
                orderService.getAllOrders(0, 10, "ACTIVE", "john");

        assertEquals(2, result.getContent().size());

        verify(orderRepo).findByStatusInAndSearch(
                anyList(),
                eq("john"),
                any(Pageable.class)
        );
    }

    @Test
    void getAllOrders_activeOnly() {

        when(orderRepo.findByStatusIn(anyList(), any()))
                .thenReturn(mockPage());

        Page<OrderResponseDTO> result =
                orderService.getAllOrders(0, 5, "ACTIVE", null);

        assertEquals(2, result.getTotalElements());

        verify(orderRepo).findByStatusIn(anyList(), any());
    }

    @Test
    void getAllOrders_specificStatusWithSearch() {

        when(orderRepo.findByStatusAndSearch(eq(OrderStatus.PENDING), eq("order"), any()))
                .thenReturn(mockPage());

        Page<OrderResponseDTO> result =
                orderService.getAllOrders(0, 10, "PENDING", "order");

        assertEquals(2, result.getContent().size());

        verify(orderRepo).findByStatusAndSearch(
                eq(OrderStatus.PENDING),
                eq("order"),
                any(Pageable.class)
        );
    }

    @Test
    void getAllOrders_specificStatusOnly() {

        when(orderRepo.findByStatus(eq(OrderStatus.SHIPPED), any()))
                .thenReturn(mockPage());

        Page<OrderResponseDTO> result =
                orderService.getAllOrders(0, 10, "SHIPPED", "");

        assertEquals(2, result.getContent().size());

        verify(orderRepo).findByStatus(eq(OrderStatus.SHIPPED), any());
    }

    @Test
    void getAllOrders_searchOnly() {

        when(orderRepo.searchOrders(eq("abc"), any()))
                .thenReturn(mockPage());

        Page<OrderResponseDTO> result =
                orderService.getAllOrders(0, 10, null, "abc");

        assertEquals(2, result.getContent().size());

        verify(orderRepo).searchOrders(eq("abc"), any());
    }

    @Test
    void getAllOrders_noFilter() {

        when(orderRepo.findAll(any(Pageable.class)))
                .thenReturn(mockPage());

        Page<OrderResponseDTO> result =
                orderService.getAllOrders(0, 10, null, null);

        assertEquals(2, result.getContent().size());

        verify(orderRepo).findAll(any(Pageable.class));
    }

    @Test
    void getAllOrders_invalidStatus_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.getAllOrders(0, 10, "WRONG_STATUS", null);
        });
    }

    @Test
    void getAllOrders_emptyResult() {

        when(orderRepo.findAll(any(Pageable.class))).thenReturn(Page.empty());


        Page<OrderResponseDTO> result =
                orderService.getAllOrders(0, 10, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllOrders_shouldApplyPaginationAndSort() {

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        when(orderRepo.findAll(any(Pageable.class)))
                .thenReturn(mockPage());

        orderService.getAllOrders(2, 20, null, null);

        verify(orderRepo).findAll(captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(2, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());

        Sort.Order sortOrder =
                pageable.getSort().getOrderFor("orderDate");

        assertNotNull(sortOrder);
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection());
    }

}

