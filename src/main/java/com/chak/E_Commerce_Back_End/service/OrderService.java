package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.controller.NotificationWebSocketController;
import com.chak.E_Commerce_Back_End.dto.order.CustomOrderDto;
import com.chak.E_Commerce_Back_End.dto.order.OrderDTO;
import com.chak.E_Commerce_Back_End.dto.order.OrderItemDTO;
import com.chak.E_Commerce_Back_End.dto.order.OrderResponseDTO;
import com.chak.E_Commerce_Back_End.exception.NotEnoughStock;
import com.chak.E_Commerce_Back_End.exception.OrderAlreadyCancelled;
import com.chak.E_Commerce_Back_End.exception.OrderNotFoundException;
import com.chak.E_Commerce_Back_End.model.*;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.repository.ProductStockHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ProductStockHistoryRepo productStockHistoryRepo;

    @Autowired
    private NotificationService notificationService;

    //place an order
    @Transactional
    public OrderResponseDTO placeOrder(OrderDTO orderDTO)
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
        // Map OrderDTO items to OrderItem entities
        List<OrderItem> items=orderDTO.getItems().stream().map(dto->{
            Product product=productService.getProductbyId(dto.getProductId());
            // Check stock availability
            if (product.getStock() < dto.getQuantity()) {
                throw new NotEnoughStock("Not enough stock for product: " + product.getName());
            }
            // Deduct stock
            product.setStock(product.getStock() - dto.getQuantity());
            productService.addProduct(product); // Save updated stock
            OrderItem orderItem=new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList());
            order.setItems(items);
        Order savedOrder = orderRepo.save(order);
        //saving history
        for (OrderItem item : savedOrder.getItems()) { Product product = item.getProduct();
            ProductStockHistory history = new ProductStockHistory();
            history.setProduct(product);
            history.setQuantityChanged(-item.getQuantity());
            history.setStockAfterChange(product.getStock());
            history.setReason("Order Placed - Order ID: " + savedOrder.getId());
            productStockHistoryRepo.save(history);
        }



        if (user != null) {

            emailService.sendOrderEmail(user.getEmail(), order.getId(), order.getStatus(), order.getTotalAmount());
            String username = user.getUsername();
            String message="Your order #" + savedOrder.getId() + " has been placed!";
            notificationService.sendUserNotification(
                    username,Map.of("message", message, "type", "info")

            );
            System.out.println("===========myorder========");
        } else {
            emailService.sendOrderEmail(order.getGuestEmail(), order.getId(), order.getStatus(), order.getTotalAmount());
        }



        return new OrderResponseDTO(savedOrder);


    }
    //Getting Order using pagination
    //Getting Order using pagination + filter + search
    public Page<OrderResponseDTO> getAllOrders(int page, int size, String status, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.PROCESSING,
                OrderStatus.SHIPPED
        );
        // Normalize inputs
        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean isActive = "ACTIVE".equalsIgnoreCase(status);
        boolean hasStatus = status != null && !status.trim().isEmpty() && !"ACTIVE".equalsIgnoreCase(status);

        Page<Order> orders;

        if (isActive && hasSearch) {
            // Active + search
            orders = orderRepo.findByStatusInAndSearch(activeStatuses, search.toLowerCase(), pageable);
        } else if (isActive) {
            // Active only
            orders = orderRepo.findByStatusIn(activeStatuses, pageable);
        } else if (hasStatus && hasSearch) {
            // Specific status + search
            OrderStatus statusEnum = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepo.findByStatusAndSearch(statusEnum, search.toLowerCase(), pageable);
        } else if (hasStatus) {
            // Only specific status
            OrderStatus statusEnum = OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepo.findByStatus(statusEnum, pageable);
        } else if (hasSearch) {
            // Only search
            orders = orderRepo.searchOrders(search.toLowerCase(), pageable);
        } else {
            // No filter, just all
            orders = orderRepo.findAll(pageable);
        }

        return orders.map(OrderResponseDTO::new);
    }

    //update status of an order
    public Order updateOrderStatus(Long orderId,OrderStatus newStatus)
    {

    Optional<Order> order=orderRepo.findById(orderId);
    if (order.isPresent())
    {
        Order order1=order.get();
        OrderStatus orderSOldtatus=order1.getStatus();
        order1.setStatus(newStatus);

        emailService.sendOrderEmail(order1.getUser().getEmail(),order1.getId(),newStatus,order1.getTotalAmount());
        String message="Your order #" + order1.getId() + "status has been updated from "+orderSOldtatus.name()+" to" +order1.getStatus().name();
        notificationService.sendUserNotification(
                order1.getUser().getUsername(),Map.of("message", message, "type", "info")

        );
        return orderRepo.save(order1);
    }else {
        throw new OrderNotFoundException("Order not found with id "+orderId);
    }


    }


    //Get Full orders by userId
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
                    //mapping orderItems to OrderItemDto
                    List<OrderItem> items = order.getItems();
                    List<OrderItemDTO> collect = items.stream().map(OrderItemDTO::new).collect(Collectors.toList());
                    dto.setItems(collect);
                    return dto;               // return DTO for each order
                })
                .collect(Collectors.toList()); // collect all DTOs into a list
    }
    //Get Customised Customer orders by UserId
    public List<CustomOrderDto> getCustomOrdersByUserId(Long userId)
    {
        List<Order> orders = orderRepo.findByUser_Id(userId);
        List<CustomOrderDto> collect = orders.stream().map(CustomOrderDto::new).collect(Collectors.toList());
        return collect;
    }

// Get order by Order id
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
            //mapping orderItems to OrderItemDto
            List<OrderItem> items = order.getItems();
            List<OrderItemDTO> collect = items.stream().map(OrderItemDTO::new).collect(Collectors.toList());
            orderResponseDTO.setItems(collect);
            orderResponseDTO.setStatus(order.getStatus());
            orderResponseDTO.setPaymentStatus(order.getPaymentStatus());
            orderResponseDTO.setPaymentMethod(order.getPaymentMethod());
            // login and guest user
            if(order.getUser()!=null) {
                orderResponseDTO.setUserName(order.getUser().getFirstName()+" "+order.getUser().getLastName());
            }
            else {
                orderResponseDTO.setUserName(order.getGuestName());
            }
            return orderResponseDTO;
        }else {
            throw new OrderNotFoundException("Order not found with Id "+orderId);
        }
    }
    // Cancel an order (soft cancel instead of delete)
    @Transactional
    public Order cancelAnOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepo.findById(orderId);
        if (orderOptional.isPresent())
        {
            // Prevent double cancellation
            Order order=orderOptional.get();
            if (order.getStatus() == OrderStatus.CANCELLED) {
                throw new OrderAlreadyCancelled("Order Already Cancelled with Id: "+orderId);
            }

              for(OrderItem item:order.getItems())
              {
                  Product product=item.getProduct();
                  product.setStock(product.getStock()+item.getQuantity());
                  productService.addProduct(product);
                  //update stock history
                  ProductStockHistory history = new ProductStockHistory();
                  history.setProduct(product);
                  history.setQuantityChanged(item.getQuantity());
                  history.setStockAfterChange(product.getStock());
                  history.setReason("Order Cancelled - Order ID: " + order.getId());
                  productStockHistoryRepo.save(history);
                  }

                  // Mark order as cancelled
                  order.setStatus(OrderStatus.CANCELLED);
                  Order save = orderRepo.save(order);

                  // Send cancellation email
                  if (order.getUser() != null) {
                      emailService.sendOrderEmail(order.getUser().getEmail(), order.getId(), order.getStatus(), order.getTotalAmount());
                  } else {
                      emailService.sendOrderEmail(order.getGuestEmail(), order.getId(), order.getStatus(), order.getTotalAmount());
                  }
                  return save;

              }
        else {
            throw new OrderNotFoundException("Oder Not Found with Id "+orderId);
        }

    }



}
