package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.LoginDTO;
import com.chak.E_Commerce_Back_End.dto.UserDTO;
import com.chak.E_Commerce_Back_End.model.ConfirmationToken;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.ConfirmationTokenRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(UserDTO userDTO) {
        // 1. Create new user
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setStatus("PENDING");

        // 2. Save the user FIRST
        User savedUser = userRepository.save(user);

        // 3. Generate confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                savedUser // Important: use the saved user
        );

        // 4. Save the token
        confirmationTokenRepository.save(confirmationToken);

        // 5. Send confirmation email
        String link = "http://localhost:8989/user/confirm?token=" + token;
        String emailBody = "Click the link to confirm your email: " + link;
        emailService.sendSimpleEmail(savedUser.getEmail(), "Confirm Your Email", emailBody);

        return savedUser;
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

    public void deleteUser(Long userId)
    {
    userRepository.deleteById(userId);
    }

    public void editUser(Long userId, UserDTO userDTO) {
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent())
        {
            User user=byId.get();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setRole(userDTO.getRole());
            user.setStatus(userDTO.getStatus());
            userRepository.save(user);

        }
    }
}
