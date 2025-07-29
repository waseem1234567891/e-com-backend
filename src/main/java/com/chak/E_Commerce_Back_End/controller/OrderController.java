package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.OrderDTO;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
private OrderService orderService;



    @PostMapping("/addorder")
    public Order addOrder(@RequestBody OrderDTO orderDTO)
    {
     return orderService.placeOrder(orderDTO);
    }

    @GetMapping("/getallorders")
    public List<Order> getAllOrders()
    {
        return orderService.getAllOrders();
    }

    //update order status
    @PutMapping("/updateorderstatus/{id}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,@RequestBody String newStatus){
        Order order=orderService.updateOrderStatus(id,newStatus);
        return ResponseEntity.ok("Order status updated");
    }
    //getting orders for a user
    @GetMapping("/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId)
    {
        List<Order> orderByUserId = orderService.getOrderByUserId(userId);
        return ResponseEntity.ok(orderByUserId);
    }

}
