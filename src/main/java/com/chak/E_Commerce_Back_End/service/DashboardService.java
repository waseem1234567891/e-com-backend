package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.order.OrderItemDTO;
import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.dto.user.DashboardResponse;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.OrderItem;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;

    public DashboardResponse getDashboard(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = user.getOrders();
        List<OrderResponseDTO> collect = orders.stream().map(order -> {
            OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
            if (order.getUser() != null) {
                orderResponseDTO.setUserName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
            } else {
                orderResponseDTO.setUserName(order.getGuestName());
            }
            //mapping orderItems to OrderItemDto
            List<OrderItem> items = order.getItems();
            List<OrderItemDTO> collect1 = items.stream().map(OrderItemDTO::new).collect(Collectors.toList());
            orderResponseDTO.setItems(collect1);
            orderResponseDTO.setShippingAddress(order.getShippingAddress());
            orderResponseDTO.setOrderDate(order.getOrderDate());
            orderResponseDTO.setTotalAmount(order.getTotalAmount());
            orderResponseDTO.setId(order.getId());
            orderResponseDTO.setPaymentMethod(order.getPaymentMethod());
            orderResponseDTO.setStatus(order.getStatus());
            orderResponseDTO.setPaymentStatus(order.getPaymentStatus());
            return orderResponseDTO;
        }).collect(Collectors.toList());
        return new DashboardResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getAddresses(),
                collect);

    }
}
