package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.order.CustomOrderDto;
import com.chak.E_Commerce_Back_End.dto.order.OrderDTO;
import com.chak.E_Commerce_Back_End.dto.order.StatusUpdateRequest;
import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
private OrderService orderService;




    @PostMapping("/addorder")
    public OrderResponseDTO addOrder(@RequestBody OrderDTO orderDTO)
    {

        return orderService.placeOrder(orderDTO);
    }

    @GetMapping("/getallorders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ACTIVE") String status, // ACTIVE = not DELIVERED or CANCELLED
            @RequestParam(required = false) String search
    ) {
        Page<OrderResponseDTO> orders = orderService.getAllOrders(page, size, status,search);
        return ResponseEntity.ok(orders);
    }

    //update order status
    @PutMapping("/updateorderstatus/{id}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        OrderStatus newStatus = OrderStatus.valueOf(request.getStatus());
        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);

        return ResponseEntity.ok("Order status updated");
    }
    //Getting Active orders of a user
    @GetMapping("user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId)
    {
        List<OrderResponseDTO> orders = orderService.getOrderByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    //Getting Customised order of a user
    @GetMapping("user/custom/{userId}")
    public ResponseEntity<?> getCustomOrdersByUserId(@PathVariable Long userId)
    {
        List<CustomOrderDto> orders = orderService.getCustomOrdersByUserId(userId);
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
    public ResponseEntity<OrderResponseDTO> deleteOrderByOrderId(@PathVariable Long orderId)
    {
        Order order = orderService.cancelAnOrder(orderId);

        return ResponseEntity.ok(new OrderResponseDTO(order));
    }


}
