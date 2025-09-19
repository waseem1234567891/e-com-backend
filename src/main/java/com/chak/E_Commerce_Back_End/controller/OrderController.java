package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.order.OrderDTO;
import com.chak.E_Commerce_Back_End.dto.StatusUpdateRequest;
import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getAllOrders()
    {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    //update order status
    @PutMapping("/updateorderstatus/{id}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        OrderStatus newStatus = OrderStatus.valueOf(request.getStatus());
        orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok("Order status updated");
    }
    //getting orders for a user
    @GetMapping("user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId)
    {
        List<OrderResponseDTO> orders = orderService.getOrderByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    //Get order by orderId
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderByOrderId(@PathVariable Long orderId)
    {
        OrderResponseDTO orderresponse = orderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(orderresponse);
    }
    //Delete An Order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrderByOrderId(@PathVariable Long orderId)
    {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}
