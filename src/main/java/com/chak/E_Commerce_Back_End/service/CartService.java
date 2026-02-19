package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.cart.CartResponseDto;
import com.chak.E_Commerce_Back_End.exception.UserNotFoundException;
import com.chak.E_Commerce_Back_End.model.CartItem;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // ------------------ ADD TO CART ------------------
    @Transactional
    public void addToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userService.getCurrentUser();
        if (user == null) throw new UserNotFoundException("User not authenticated");

        Product product = productService.getProductbyId(productId);

        CartItem existing = cartRepository.findByUserAndProduct(user, product).orElse(null);
        int currentQuantityInCart = existing != null ? existing.getQuantity() : 0;

        if (currentQuantityInCart + quantity > product.getStock()) {
            int available = product.getStock() - currentQuantityInCart;
            throw new IllegalStateException(
                    "Cannot add " + quantity + " items. Only " + available + " available in stock."
            );
        }

        if (existing != null) {
            existing.setQuantity(currentQuantityInCart + quantity);
        } else {
            cartRepository.save(new CartItem(user, product, quantity));
        }
    }

    // ------------------ UPDATE QUANTITY ------------------
    @Transactional
    public void updateCartItemQuantity(Long productId, int newQuantity) {
        User user = userService.getCurrentUser();
        if (user == null) throw new UserNotFoundException("User not authenticated");

        Product product = productService.getProductbyId(productId);

        CartItem item = cartRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new IllegalStateException("Cart item not found"));

        if (newQuantity <= 0) {
            // Remove item if quantity <= 0
            cartRepository.delete(item);
        } else if (newQuantity > product.getStock()) {
            throw new IllegalStateException("Cannot set quantity. Only " + product.getStock() + " available in stock.");
        } else {
            item.setQuantity(newQuantity);
        }
    }

    // ------------------ GET CART ------------------
    @Transactional(readOnly = true)
    public List<CartResponseDto> getCurrentUserCart() {
        User user = userService.getCurrentUser();
        if (user == null) return Collections.emptyList();

        return mapToDto(cartRepository.findByUser(user));
    }

    // ------------------ CLEAR CART ------------------
    @Transactional
    public List<CartResponseDto> clearCurrentUserCart() {
        User user = userService.getCurrentUser();
        if (user == null) throw new UserNotFoundException("User not authenticated");

        cartRepository.deleteByUser(user);
        return Collections.emptyList();
    }

    // ------------------ REMOVE ITEM ------------------
    @Transactional
    public List<CartResponseDto> removeItemFromCurrentUserCart(Long productId) {
        User user = userService.getCurrentUser();
        if (user == null) return Collections.emptyList();

        Product product = productService.getProductbyId(productId);
        cartRepository.deleteByUserAndProduct_Id(user, productId);
        return mapToDto(cartRepository.findByUser(user));
    }

    // ------------------ PRIVATE MAPPER ------------------
    private List<CartResponseDto> mapToDto(List<CartItem> items) {
        return items.stream()
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
}
