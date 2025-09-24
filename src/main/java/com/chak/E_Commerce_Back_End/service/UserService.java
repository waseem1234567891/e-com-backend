package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.auth.LoginDTO;
import com.chak.E_Commerce_Back_End.dto.auth.ProfileDto;
import com.chak.E_Commerce_Back_End.dto.auth.UserDTO;
import com.chak.E_Commerce_Back_End.dto.user.UserResponseDto;
import com.chak.E_Commerce_Back_End.dto.user.UserUpdateDto;
import com.chak.E_Commerce_Back_End.model.ConfirmationToken;
import com.chak.E_Commerce_Back_End.model.PasswordResetToken;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.ConfirmationTokenRepository;
import com.chak.E_Commerce_Back_End.repository.TokenRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(UserDTO userDTO) {
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

    public Page<UserResponseDto> getAllUserUsingPagination(int page,int size)
    {
        Pageable pageable= PageRequest.of(page,size);
        return userRepository.findAll(pageable).map(user -> new UserResponseDto(user));
    }

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
}
