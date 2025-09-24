package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.auth.*;
import com.chak.E_Commerce_Back_End.dto.user.DashboardResponse;
import com.chak.E_Commerce_Back_End.dto.auth.UserDTO;
import com.chak.E_Commerce_Back_End.dto.user.UserResponseDto;
import com.chak.E_Commerce_Back_End.dto.user.UserUpdateDto;
import com.chak.E_Commerce_Back_End.exception.UserAlreadyExistsException;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.ConfirmationTokenService;
import com.chak.E_Commerce_Back_End.service.CustomUserDetailsService;
import com.chak.E_Commerce_Back_End.service.DashboardService;
import com.chak.E_Commerce_Back_End.service.UserService;
import com.chak.E_Commerce_Back_End.util.JwtUtil;
import jakarta.validation.Valid;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private DashboardService dashboardService;

    // User Registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: " + user.getEmail());
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userService.loginUser(request);
            if (user.getRole().equals("USER")&&user.getStatus().equals("ACTIVE")) {

                String token = jwtUtil.generateToken(user.getUsername(), user.getRole(),user.getId());

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
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userService.loginUser(request);
            if (user.getRole().equals("ADMIN")) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole(),user.getId());

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

    DashboardResponse dashboard = dashboardService.getDashboard(authentication.getName());
    return ResponseEntity.ok(dashboard);
}

        //admin dashboard

    @GetMapping("/admindashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminDashboard(Authentication authentication) {
        return ResponseEntity.ok("Welcome Admin: " + authentication.getName());
    }


@GetMapping("/allusers")
@PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getAllUsers()
{

    return userService.getAllRegisterUsers();
}
//Get Users By Pagination

@GetMapping("/alluser")
@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUserThroughPagination(@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue = "5")int size)
{
return ResponseEntity.ok(userService.getAllUserUsingPagination(page,size));
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
    public ResponseEntity<String> editUser(@PathVariable Long userId,@RequestBody UserUpdateDto userDTO)
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

    @PutMapping("/updateprofile/{userId}")

    public ResponseEntity<User> updateProfile(@PathVariable Long userId, @RequestBody ProfileDto profileDto)
    {

        User user1 = userService.updateProfile(userId,profileDto);
        return ResponseEntity.ok(user1);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("If that email exists, a reset link was sent.");
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> validateResetToken(@RequestParam("token") String token)
    {
        return userService.validateToken(token);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
       return userService.resetPassword(token,newPassword);
    }




}
