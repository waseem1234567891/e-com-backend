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
import java.util.*;
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

    @Transactional
    public OrderResponseDTO placeOrder(OrderDTO orderDTO) {
        System.out.println(orderDTO);

        Order order = new Order();

        // ✅ Get current user
        User user = null;
        try {
            user = userService.getCurrentUser();
        } catch (Exception e) {
            // ignore (guest checkout)
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
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setTotalAmount(orderDTO.getTotalAmount());

        List<OrderItem> orderItems = new ArrayList<>();

        // ✅ Deduct stock and prepare order items (no history yet)
        for (OrderItemDTO dto : orderDTO.getItems()) {
            Product product = productService.getProductbyId(dto.getProductId());

            // Check stock
            if (product.getStock() < dto.getQuantity()) {
                throw new NotEnoughStock("Not enough stock for product: " + product.getName());
            }

            // Deduct stock
            int newStock = product.getStock() - dto.getQuantity();
            product.setStock(newStock);
            Product updatedProduct = productService.addProduct(product);

            // Create order item
            OrderItem item = new OrderItem();
            item.setProduct(updatedProduct);
            item.setQuantity(dto.getQuantity());
            item.setOrder(order);
            orderItems.add(item);
        }

        order.setItems(orderItems);

        // ✅ Save order first (we need the ID)
        Order savedOrder = orderRepo.save(order);

        // ✅ Now log stock history (with order ID)
        for (OrderItem item : savedOrder.getItems()) {
            Product product = item.getProduct();

            ProductStockHistory history = new ProductStockHistory();
            history.setProduct(product);
            history.setQuantityChanged(-item.getQuantity());
            history.setStockAfterChange(product.getStock());
            history.setReason("Order Placed - Order ID: " + savedOrder.getId());

            productStockHistoryRepo.save(history);
        }

        // ✅ Notify or email user
        if (user != null) {
            emailService.sendOrderEmail(user.getEmail(), order.getId(), order.getStatus(), order.getTotalAmount());
            String username = user.getUsername();
            String message = "Your order #" + savedOrder.getId() + " has been placed!";
            String link = "http://localhost:3000/order-for-user/" + savedOrder.getId();
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);
            payload.put("type", "info");
            payload.put("link", link);
            notificationService.sendUserNotification(
                    username,
                    payload);
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
        if (order1.getUser()!=null) {
            emailService.sendOrderEmail(order1.getUser().getEmail(), order1.getId(), newStatus, order1.getTotalAmount());
            String message = "Your order #" + order1.getId() + " status has been updated from " + orderSOldtatus.name() + " to " + order1.getStatus().name();
            String link = "http://localhost:3000/" + "order-for-user/" + order1.getId();
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);
            payload.put("type", "info");
            payload.put("link", link);
            notificationService.sendUserNotification(
                    order1.getUser().getUsername(), payload );
        }
        else {
            emailService.sendOrderEmail(order1.getGuestEmail(), order1.getId(), newStatus, order1.getTotalAmount());
        }
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
                      String message="Your order #" + order.getId() +" has been Canceled" ;
                      String link="http://localhost:3000/"+"order-for-user/"+order.getId();
                      Map<String, Object> payload = new HashMap<>();
                      payload.put("message", message);
                      payload.put("type", "info");
                      payload.put("link", link);
                      notificationService.sendUserNotification(
                              order.getUser().getUsername(),payload

                      );
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
