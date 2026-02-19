package com.chak.E_Commerce_Back_End.service.services;



import com.chak.E_Commerce_Back_End.dto.cart.CartResponseDto;
import com.chak.E_Commerce_Back_End.exception.UserNotFoundException;
import com.chak.E_Commerce_Back_End.model.CartItem;
import com.chak.E_Commerce_Back_End.model.Product;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.CartRepository;
import com.chak.E_Commerce_Back_End.service.CartService;
import com.chak.E_Commerce_Back_End.service.ProductService;
import com.chak.E_Commerce_Back_End.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        product = new Product();
        product.setId(100L);
        product.setStock(10);
        product.setName("Test Product");
        product.setPrice(50.0);
        product.setImagePath("/img.png");
    }

    // ------------------ ADD TO CART ------------------
    @Test
    void testAddToCart_NewItem_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        cartService.addToCart(product.getId(), 3);

        verify(cartRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddToCart_ExistingItem_Success() {
        CartItem existing = new CartItem(user, product, 2);
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existing));

        cartService.addToCart(product.getId(), 3);

        assertEquals(5, existing.getQuantity());
        verify(cartRepository, never()).save(any()); // should not call save on new item
    }

    @Test
    void testAddToCart_QuantityExceedsStock() {
        CartItem existing = new CartItem(user, product, 8);
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> cartService.addToCart(product.getId(), 5));

        assertTrue(ex.getMessage().contains("Only 2 available in stock"));
    }

    @Test
    void testAddToCart_NoUser() {
        when(userService.getCurrentUser()).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> cartService.addToCart(product.getId(), 1));
    }

    @Test
    void testAddToCart_NegativeQuantity() {
        when(userService.getCurrentUser()).thenReturn(user);
        assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(product.getId(), -1));
    }

    // ------------------ UPDATE QUANTITY ------------------
    @Test
    void testUpdateCartItemQuantity_Increase_Success() {
        CartItem item = new CartItem(user, product, 2);
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(item));

        cartService.updateCartItemQuantity(product.getId(), 5);

        assertEquals(5, item.getQuantity());
    }

    @Test
    void testUpdateCartItemQuantity_RemoveItem() {
        CartItem item = new CartItem(user, product, 2);
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(item));

        cartService.updateCartItemQuantity(product.getId(), 0);

        verify(cartRepository, times(1)).delete(item);
    }

    @Test
    void testUpdateCartItemQuantity_ExceedsStock() {
        CartItem item = new CartItem(user, product, 2);
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(item));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> cartService.updateCartItemQuantity(product.getId(), 20));

        assertTrue(ex.getMessage().contains("Only 10 available in stock"));
    }

    @Test
    void testUpdateCartItemQuantity_NoCartItem() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> cartService.updateCartItemQuantity(product.getId(), 2));

        assertEquals("Cart item not found", ex.getMessage());
    }

    // ------------------ GET CART ------------------
    @Test
    void testGetCurrentUserCart_Success() {
        CartItem item = new CartItem(user, product, 2);
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(List.of(item));

        List<CartResponseDto> cart = cartService.getCurrentUserCart();

        assertEquals(1, cart.size());
        assertEquals(product.getName(), cart.get(0).getProductName());
    }

    @Test
    void testGetCurrentUserCart_NoUser() {
        when(userService.getCurrentUser()).thenReturn(null);
        List<CartResponseDto> cart = cartService.getCurrentUserCart();
        assertTrue(cart.isEmpty());
    }

    // ------------------ CLEAR CART ------------------
    @Test
    void testClearCurrentUserCart_Success() {
        when(userService.getCurrentUser()).thenReturn(user);

        List<CartResponseDto> result = cartService.clearCurrentUserCart();

        verify(cartRepository, times(1)).deleteByUser(user);
        assertTrue(result.isEmpty());
    }

    @Test
    void testClearCurrentUserCart_NoUser() {
        when(userService.getCurrentUser()).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> cartService.clearCurrentUserCart());
    }

    // ------------------ REMOVE ITEM ------------------
    @Test
    void testRemoveItemFromCurrentUserCart_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductbyId(product.getId())).thenReturn(product);
        when(cartRepository.findByUser(user)).thenReturn(Collections.emptyList());

        List<CartResponseDto> result = cartService.removeItemFromCurrentUserCart(product.getId());

        verify(cartRepository, times(1)).deleteByUserAndProduct_Id(user, product.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void testRemoveItemFromCurrentUserCart_NoUser() {
        when(userService.getCurrentUser()).thenReturn(null);
        List<CartResponseDto> result = cartService.removeItemFromCurrentUserCart(product.getId());
        assertTrue(result.isEmpty());
    }
}

