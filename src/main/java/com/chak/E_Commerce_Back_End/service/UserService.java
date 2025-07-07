package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.LoginDTO;
import com.chak.E_Commerce_Back_End.dto.UserDTO;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        return userRepository.save(user);
    }

    public User loginUser(LoginDTO loginDTO) {
        return userRepository.findByUsername(loginDTO.getUsername())
                .filter(user -> passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
                .orElse(null);
    }



    public User getCurrentUser()
    {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser"))
        {
            throw new RuntimeException("Unauthenticated");
        }

        Object principle=authentication.getPrincipal();
        String userName;
        if (principle instanceof UserDetails)
        {
            userName=((UserDetails) principle).getUsername();
        }else {
            userName=principle.toString();
        }
        return userRepository.findByUsername(userName).orElseThrow(()->new RuntimeException("user not found"));
    }

    public List<User> getAllRegisterUsers() {
        return userRepository.findAll();
    }
}
