package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.AuthResponse;
import com.chak.E_Commerce_Back_End.dto.LoginDTO;
import com.chak.E_Commerce_Back_End.dto.UserDTO;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.ConfirmationTokenService;
import com.chak.E_Commerce_Back_End.service.CustomUserDetailsService;
import com.chak.E_Commerce_Back_End.service.UserService;
import com.chak.E_Commerce_Back_End.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfirmationTokenService tokenService;

    // User Registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
       // userRepository.save(user);
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // Admin Registration
    @PostMapping("/admin-register")
    public ResponseEntity<?> adminRegister(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ADMIN");
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    //user login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userService.loginUser(request);
            if (user.getRole().equals("USER")) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

                return ResponseEntity.ok(new AuthResponse(token, user.getId(),user.getUsername()));
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Enter UserName and Password");
            }

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    //admin loggin
    @PostMapping("/adminlogin")
    public ResponseEntity<?> adminLogin(@RequestBody LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userService.loginUser(request);
            if (user.getRole().equals("ADMIN")) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

                return ResponseEntity.ok(new AuthResponse(token, user.getId(),user.getUsername()));
            }else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please Enter Admin Username and Password");
            }

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


// user dashboard
@GetMapping("/dashboard")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> userDashboard(Authentication authentication) {
    return ResponseEntity.ok("Welcome User: " + authentication.getName());
}

        //admin dashboard

    @GetMapping("/admindashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminDashboard(Authentication authentication) {
        return ResponseEntity.ok("Welcome Admin: " + authentication.getName());
    }

@GetMapping("/allusers")
    public List<User> getAllUsers()
{
    return userService.getAllRegisterUsers();
}

@DeleteMapping("user/{userId}")
@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId)
{
    try {
        userService.deleteUser(userId);
    }catch (Exception ex)
    {
        return ResponseEntity.badRequest().body("User is not exist");
    }
    return ResponseEntity.ok("User deleted successfully.");
}

    @PatchMapping("user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> editUser(@PathVariable Long userId,@RequestBody UserDTO userDTO)
    {
        try {
            userService.editUser(userId,userDTO);
        }catch (Exception ex)
        {
            return ResponseEntity.badRequest().body("User is not exist");
        }
        return ResponseEntity.ok("User update successfully.");
    }


    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        String result = tokenService.confirmToken(token);
        return ResponseEntity.ok(result);
    }

}
