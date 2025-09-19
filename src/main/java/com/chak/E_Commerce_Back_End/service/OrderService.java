package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.order.OrderDTO;
import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.exception.OrderNotFoundException;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.OrderItem;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
//place an order
    public Order placeOrder(OrderDTO orderDTO)
    {
        System.out.println(orderDTO);
        Order order=new Order();
        User user=null;
        try {
            user = userService.getCurrentUser();
        }catch (Exception e)
        {

        }
        if (user != null) {
            order.setUser(user);
        } else {
            order.setUser(null);
            order.setGuestName(orderDTO.getGuestName());
            order.setGuestEmail(orderDTO.getGuestEmail());
        }

        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setPaymentStatus("UnPaid");
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setTotalAmount(orderDTO.getTotalAmount());

        List<OrderItem> items=orderDTO.getItems().stream().map(dto->{
            Product product=productService.getProductbyId(dto.getProductId());
            OrderItem orderItem=new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList());
            order.setItems(items);
        Order save = orderRepo.save(order);
        if (user != null) {
            emailService.sendOrderEmail(user.getEmail(), order.getId(), order.getStatus(), order.getTotalAmount());
        } else {
            emailService.sendOrderEmail(order.getGuestEmail(), order.getId(), order.getStatus(), order.getTotalAmount());
        }
        return save;


    }
    public List<OrderResponseDTO> getAllOrders()
    {
        List<Order> orders = orderRepo.findAll();
        return orders.stream()                     // create a stream of orders
                .map(order -> {              // map each Order to OrderResponseDTO
                    OrderResponseDTO dto = new OrderResponseDTO();
                    dto.setId(order.getId());
                    if (order.getUser()!=null) {
                        dto.setUserName(order.getUser().getFirstName()+" "+order.getUser().getLastName());
                    }else {
                        dto.setUserName(order.getGuestName());
                    }
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setStatus(order.getStatus());
                    dto.setPaymentStatus(order.getPaymentStatus());
                    dto.setShippingAddress(order.getShippingAddress());
                    dto.setPaymentMethod(order.getPaymentMethod());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setItems(order.getItems());
                    return dto;               // return DTO for each order
                })
                .collect(Collectors.toList());
    }
    //update status of an order
    public Order updateOrderStatus(Long orderId,OrderStatus newStatus)
    {

    Optional<Order> order1=orderRepo.findById(orderId);
    if (order1.isPresent())
    {
        Order order2=order1.get();
        order2.setStatus(newStatus);

        emailService.sendOrderEmail(order2.getUser().getEmail(),order2.getId(),newStatus,order2.getTotalAmount());
        return orderRepo.save(order2);
    }else {
        throw new OrderNotFoundException("Order not found with id "+orderId);
    }


    }


    //Get an order by userId
    public List<OrderResponseDTO> getOrderByUserId(Long userId)
    {
        List<Order> orders = orderRepo.findByUser_Id(userId);
        return orders.stream()                     // create a stream of orders
                .map(order -> {              // map each Order to OrderResponseDTO
                    OrderResponseDTO dto = new OrderResponseDTO();
                    dto.setId(order.getId());
                    //dto.setUserId(order.getUser().getId());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setStatus(order.getStatus());
                    dto.setPaymentStatus(order.getPaymentStatus());
                    dto.setShippingAddress(order.getShippingAddress());
                    dto.setPaymentMethod(order.getPaymentMethod());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setItems(order.getItems());
                    return dto;               // return DTO for each order
                })
                .collect(Collectors.toList()); // collect all DTOs into a list
    }


    public OrderResponseDTO getOrderByOrderId(Long orderId) {
        Optional<Order> byId = orderRepo.findById(orderId);
        if (byId.isPresent())
        {
            Order order = byId.get();
            OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
            orderResponseDTO.setId(order.getId());
            orderResponseDTO.setTotalAmount(order.getTotalAmount());
            orderResponseDTO.setShippingAddress(order.getShippingAddress());
            orderResponseDTO.setOrderDate(order.getOrderDate());
            orderResponseDTO.setItems(order.getItems());
            orderResponseDTO.setStatus(order.getStatus());
            orderResponseDTO.setPaymentStatus(order.getPaymentStatus());
            orderResponseDTO.setPaymentMethod(order.getPaymentMethod());
           // orderResponseDTO.setUserId(order.getUser().getId());
            return orderResponseDTO;
        }else {
            throw new OrderNotFoundException("Order not found with Id "+orderId);
        }
    }

    public void deleteOrder(Long orderId) {
        orderRepo.deleteById(orderId);
    }
}
