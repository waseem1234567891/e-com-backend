package com.chak.E_Commerce_Back_End.controller;


import com.chak.E_Commerce_Back_End.model.CartItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
// add
@RestController
@RequestMapping("/cart")
public class CartController {


    @PostMapping("/add")
    public ResponseEntity<?> addToCart(HttpSession session, @RequestBody CartItem item)
    {
        List<CartItem> cart= (List<CartItem>) session.getAttribute("cart");
        if (cart==null)
        {
            cart=new ArrayList<>();
        }
            cart.add(item);
            session.setAttribute("cart",cart);
            return  ResponseEntity.ok(cart);

    }

    @GetMapping
    public ResponseEntity<?> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        return ResponseEntity.ok(cart != null ? cart : new ArrayList<>());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return ResponseEntity.ok("Cart has been cleared.");
    }


    @DeleteMapping("/removeItem/{productId}")
    public ResponseEntity<?> removeItemFromCart(HttpSession session, @PathVariable Long productId) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            Iterator<CartItem> iterator = cart.iterator();
            while (iterator.hasNext()) {
                CartItem item = iterator.next();
                if (item.getProductId().equals(productId)) {
                    iterator.remove();
                    break;
                }
            }
            session.setAttribute("cart", cart);
        }
        return ResponseEntity.ok(cart != null ? cart : new ArrayList<>());
    }
}
