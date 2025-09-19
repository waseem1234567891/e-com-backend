package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.model.CartItem;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Transactional
    public void addItemToCart(CartItem cartItem)
    {
        cartRepository.save(cartItem);
    }
    public List<CartItem> getCartItemsByUserId(User user)
    {
      return   cartRepository.findByUser(user);
    }
    @Transactional
    public void clearCartByUser(User user) {
        cartRepository.deleteByUser(user);
    }
@Transactional
    public void deleteByUserAndProduct_Id(User user, Long productId) {
        cartRepository.deleteByUserAndProduct_Id(user,productId);
    }
}
