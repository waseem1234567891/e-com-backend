package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.cart.CartItemDto;
import com.chak.E_Commerce_Back_End.dto.cart.CartResponseDto;
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
                        item.getProduct().getImagePath()
                ))
                .toList();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDto item) {
        CartItem cartItem=new CartItem();
        User user=userService.getUserByUserId(item.getUserId());
        Product product=productService.getProductbyId(item.getProductId());
        CartItem existing = cartService.getCartItemsByUserId(user).stream()
                .filter(ci -> ci.getProduct().getId().equals(item.getProductId()))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            cartService.addItemToCart(existing);
        } else {
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(item.getQuantity());

            cartService.addItemToCart(cartItem);
        }

        return ResponseEntity.ok("Product is added");
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        User user = userService.getCurrentUser();
        if (user==null)
        {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(toResponseDto(cartService.getCartItemsByUserId(user)));
        // return ResponseEntity.ok(cartService.getCartItemsByUserId(user));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        User user = userService.getCurrentUser();
        cartService.clearCartByUser(user);
        return ResponseEntity.ok(Collections.emptyList());
    }

    @DeleteMapping("/removeItem/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable Long productId) {
        User user = userService.getCurrentUser();
        cartService.deleteByUserAndProduct_Id(user, productId);
        return ResponseEntity.ok(toResponseDto(cartService.getCartItemsByUserId(user)));
    }
}
