package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.cart.CartItemDto;
import com.chak.E_Commerce_Back_End.dto.cart.CartResponseDto;
import com.chak.E_Commerce_Back_End.dto.cart.UpdateQuantityRequest;
import com.chak.E_Commerce_Back_End.model.CartItem;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.CartService;
import com.chak.E_Commerce_Back_End.service.ProductService;
import com.chak.E_Commerce_Back_End.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    private List<CartResponseDto> toResponseDto(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> new CartResponseDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getImagePath(),
                        item.getProduct().getStock()
                ))
                .toList();
    }


    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDto item) {
        cartService.addToCart( item.getProductId(), item.getQuantity());
        return ResponseEntity.ok("Product added to cart");
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        return ResponseEntity.ok(cartService.getCurrentUserCart());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        cartService.clearCurrentUserCart();
        return ResponseEntity.ok(Collections.emptyList());
    }

    @DeleteMapping("/removeItem/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable Long productId) {
        return ResponseEntity.ok(
                cartService.removeItemFromCurrentUserCart(productId)
        );
    }
    @PutMapping("/updateQuantity/{productId}")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long productId,
            @RequestBody UpdateQuantityRequest request
    ) {
        // request should have "quantity" field
        cartService.updateCartItemQuantity(productId, request.getQuantity());
        // return updated cart
        return ResponseEntity.ok(cartService.getCurrentUserCart());
    }
}
