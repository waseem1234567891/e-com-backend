package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.auth.AuthResponse;
import com.chak.E_Commerce_Back_End.dto.auth.LoginDTO;
import com.chak.E_Commerce_Back_End.dto.auth.ProfileDto;
import com.chak.E_Commerce_Back_End.dto.auth.UserDTO;
import com.chak.E_Commerce_Back_End.dto.order.CustomOrderDto;
import com.chak.E_Commerce_Back_End.dto.order.OrderDTO;
import com.chak.E_Commerce_Back_End.dto.user.AddressDTO;
import com.chak.E_Commerce_Back_End.dto.user.UserDetailsDTO;
import com.chak.E_Commerce_Back_End.dto.user.UserResponseDto;
import com.chak.E_Commerce_Back_End.dto.user.UserUpdateDto;
import com.chak.E_Commerce_Back_End.exception.EmailNotVerifiedException;
import com.chak.E_Commerce_Back_End.exception.PasswordInCorrectException;
import com.chak.E_Commerce_Back_End.exception.UserAlreadyExistsException;
import com.chak.E_Commerce_Back_End.exception.UserNotFoundException;
import com.chak.E_Commerce_Back_End.model.ConfirmationToken;
import com.chak.E_Commerce_Back_End.model.CustomUserDetails;
import com.chak.E_Commerce_Back_End.model.PasswordResetToken;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.model.enums.UserStatus;
import com.chak.E_Commerce_Back_End.repository.ConfirmationTokenRepository;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.repository.TokenRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;



    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
     //register a new User
     @Transactional
    public User registerUser(@Valid UserDTO userDTO) {
        //check Email already exists
        Optional<User> excistedUser = userRepository.findByEmail(userDTO.getEmail());
        if (excistedUser.isPresent())
        {
            throw new UserAlreadyExistsException("This Email Address is Already Registered");
        }
        //check if UserName already exists
         if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
             throw new UserAlreadyExistsException("Username already exists");
         }
        // 1. Create new user
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
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
        String link = "http://localhost:8989/auth/confirm?token=" + token;
        String emailBody = "Click the link to confirm your email: " + link;
        emailService.sendSimpleEmail(savedUser.getEmail(), "Confirm Your Email", emailBody);

        return savedUser;
    }

    @Transactional
    public ResponseEntity<?> loginUser(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());

        if (userOpt.isEmpty()) {
            // no such username → invalid credentials
            throw new UserNotFoundException("User Name does not exists");
        }

        User user = userOpt.get();

        // ✅ handle email verification first
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())&&!"INACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(), loginDTO.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtUtil.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole(),
                    userDetails.getId()
            );

            // optionally update last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return ResponseEntity.ok(
                    new AuthResponse(token, userDetails.getId(),
                            userDetails.getUsername(), userDetails.getRole())
            );

        } catch (BadCredentialsException e) {
            throw new PasswordInCorrectException("Wrong Password");
        }
    }


    // get current logged in user
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
// getting all register users
    public List<UserResponseDto> getAllRegisterUsers() {

        List<User> users = userRepository.findAll();
        List<UserResponseDto> collect = users.stream().map(user -> {
            UserResponseDto userResponseDto = new UserResponseDto();
            userResponseDto.setId(user.getId());
            userResponseDto.setEmail(user.getEmail());
            userResponseDto.setRole(user.getRole());
            userResponseDto.setStatus(user.getStatus());
            userResponseDto.setFirstName(user.getFirstName());
            userResponseDto.setLastName(user.getLastName());
            userResponseDto.setUsername(user.getUsername());
            return userResponseDto;
        }).collect(Collectors.toList());
        return collect;
    }
    //Getting All user By pagination

    public Page<UserResponseDto> getAllUserUsingPagination(int page,int size,String search)
    {
        Pageable pageable= PageRequest.of(page,size);
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            // Search by username or email (case-insensitive)
            userPage = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable
            );
        } else {
            // No search → return all users
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(user -> new UserResponseDto(user));
    }



    //Delete a user

    public void deleteUser(Long userId)
    {
    userRepository.deleteById(userId);
    }

    public void editUser(Long userId, UserUpdateDto userDTO) {
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent())
        {
            User user=byId.get();
            user.setUsername(userDTO.getUsername());
            //user.setPassword(userDTO.getPassword());
            user.setRole(userDTO.getRole());
            user.setStatus(userDTO.getStatus());
            userRepository.save(user);

        }
    }

    public User updateProfile(Long userId, ProfileDto profileDto) {
        User myUser=new User();
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent())
        {
            myUser = byId.get();
            myUser.setFirstName(profileDto.getFirstName());
            myUser.setLastName(profileDto.getLastName());
            userRepository.save(myUser);
        }else {
            throw new UsernameNotFoundException("User not Exist");
        }
        return myUser;
    }
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(email, token);
    }

    public ResponseEntity<String> validateToken(String token) {
        Optional<PasswordResetToken> byToken = tokenRepository.findByToken(token);
        if (byToken.isEmpty())
        {
            return ResponseEntity.badRequest().body("Invalid reset link.");
        }
        PasswordResetToken passwordResetToken=byToken.get();
        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now()))
        {
            return ResponseEntity.badRequest().body("Reset link has expired.");
        }
        return ResponseEntity.ok("Valid token. You can reset your password.");
    }

    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> byToken = tokenRepository.findByToken(token);
        if (byToken.isEmpty())
        {
            return ResponseEntity.badRequest().body("Invalid token.");

        }
        PasswordResetToken resetToken= byToken.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token has expired.");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Invalidate token after use
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok("Password has been successfully reset.");
    }
    public User getUserByUserId(Long userId)
    {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent())
        {
            return userOpt.get();
        }else {
            throw new UsernameNotFoundException("user not exist with id  "+userId);
        }
    }

    public void forgetUserName(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent())
        {
            User user = userOpt.get();
            emailService.sendSimpleEmail(user.getEmail(),"User name","Your user name is "+user.getUsername());
        }else {
            throw new UsernameNotFoundException("User name not exist with this email adress "+email);
        }
    }

    public UserDetailsDTO getUserDetailsByUserId(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent())
        {
            User user = userOpt.get();
            UserDetailsDTO userDetailsDTO=new UserDetailsDTO();
            userDetailsDTO.setId(user.getId());
            userDetailsDTO.setFirstName(user.getFirstName());
            userDetailsDTO.setLastName(user.getLastName());
            userDetailsDTO.setStatus(user.getStatus());
            userDetailsDTO.setRole(user.getRole());
            userDetailsDTO.setEmail(user.getEmail());
            userDetailsDTO.setCreatedAt(user.getCreatedAt());
            userDetailsDTO.setLastLogin(user.getLastLogin());
            userDetailsDTO.setUsername(user.getUsername());
            userDetailsDTO.setOrders(user.getOrders().stream().map(CustomOrderDto::new).collect(Collectors.toList()));
            userDetailsDTO.setAddresses(user.getAddresses().stream().map(AddressDTO::new).collect(Collectors.toList()));
       return userDetailsDTO;
        }else {
            throw new UsernameNotFoundException("User not found with id "+userId);
        }
    }


}

