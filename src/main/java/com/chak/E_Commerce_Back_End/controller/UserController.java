package com.chak.E_Commerce_Back_End.controller;

import com.chak.E_Commerce_Back_End.dto.auth.*;
import com.chak.E_Commerce_Back_End.dto.user.*;
import com.chak.E_Commerce_Back_End.dto.auth.UserDTO;
import com.chak.E_Commerce_Back_End.exception.EmailNotVerifiedException;
import com.chak.E_Commerce_Back_End.exception.PasswordInCorrectException;
import com.chak.E_Commerce_Back_End.exception.UserAlreadyExistsException;
import com.chak.E_Commerce_Back_End.exception.UserNotFoundException;
import com.chak.E_Commerce_Back_End.model.CustomUserDetails;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.ConfirmationTokenService;
import com.chak.E_Commerce_Back_End.service.CustomUserDetailsService;
import com.chak.E_Commerce_Back_End.service.DashboardService;
import com.chak.E_Commerce_Back_End.service.UserService;
import com.chak.E_Commerce_Back_End.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

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
        user.setRole("USER");
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // Admin Registration
    @PostMapping("/admin-register")
    public ResponseEntity<?> adminRegister(@RequestBody UserDTO user) {
        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());
        if (byUsername.isPresent())
        {
            throw new UserAlreadyExistsException("user name already exist");
        }else {
            User user1=new User();
            user1.setRole("ADMIN");
            user1.setUsername(user.getUsername());
            user1.setPassword(passwordEncoder.encode(user.getPassword()));
            user1.setEmail(user.getEmail());
            user1.setFirstName(user.getFirstName());
            user1.setLastName(user.getLastName());
            userRepository.save(user1);
        }


        return ResponseEntity.ok("Admin registered successfully");
    }

    //user login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {


      return   userService.loginUser(request);



    }




    //admin loggin
    @PostMapping("/adminlogin")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginDTO request) {
      return userService.loginUser(request);
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
    public ResponseEntity<Page<UserResponseDto>> getAllUserThroughPagination(@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue = "5")int size,@RequestParam(required = false) String search)
{
return ResponseEntity.ok(userService.getAllUserUsingPagination(page,size,search));
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

    @PostMapping("/forgot-username")
    public ResponseEntity<?> sendForgetUserName(@RequestBody EmailRequest email)
    {
        userService.forgetUserName(email.getEmail());
        return ResponseEntity.ok("User name has been sent to Your Registered Email Adress");
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailsDTO getUserDetails(@PathVariable Long userId)
    {
      return   userService.getUserDetailsByUserId(userId);
    }

    //check username available
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {

        if (!userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "available", true
            ));
        }

        List<String> suggestions = generateUsernameSuggestions(username);

        return ResponseEntity.ok(Map.of(
                "available", false,
                "suggestions", suggestions
        ));
    }


    private List<String> generateUsernameSuggestions(String baseUsername) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String candidate = baseUsername + i;
            if (!userRepository.findByUsername(candidate).isPresent()) {
                suggestions.add(candidate);
            }
        }

        // Add random option if still not enough
        if (suggestions.size() < 5) {
            suggestions.add(
                    baseUsername + "_" + UUID.randomUUID().toString().substring(0, 4)
            );
        }

        return suggestions;
    }

}
