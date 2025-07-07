package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.OrderDTO;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.OrderItem;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
//place an order
    public Order placeOrder(OrderDTO orderDTO)
    {
        System.out.println(orderDTO);
        Order order=new Order();
        order.setUser(userService.getCurrentUser());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setPaymentStatus("UnPaid");
        order.setStatus("Pending");
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
        return orderRepo.save(order);
    }
    public List<Order> getAllOrders()
    {
       return orderRepo.findAll();
    }
    //update status of an order
    public Order updateOrderStatus(Long orderId,String newStatus)
    {
    Order order=orderRepo.findById(orderId).get();
    order.setStatus(newStatus);
    return orderRepo.save(order);
    }

}
