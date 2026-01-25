package com.chak.E_Commerce_Back_End.service.services.userservice;

import com.chak.E_Commerce_Back_End.dto.auth.AuthResponse;
import com.chak.E_Commerce_Back_End.dto.auth.LoginDTO;
import com.chak.E_Commerce_Back_End.exception.EmailNotVerifiedException;
import com.chak.E_Commerce_Back_End.exception.PasswordInCorrectException;
import com.chak.E_Commerce_Back_End.exception.UserNotFoundException;
import com.chak.E_Commerce_Back_End.model.CustomUserDetails;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.UserService;
import com.chak.E_Commerce_Back_End.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    private LoginDTO loginDTO;

    private User user;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setUsername("john123");
        loginDTO.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("john123");
        user.setPassword("encodedPassword");
        user.setRole("USER");
        user.setStatus("ACTIVE");
    }

    @Test
    void testLoginUser_UserNotFound() {
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.loginUser(loginDTO);
        });

        assertEquals("User Name does not exists", exception.getMessage());
    }

    @Test
    void testLoginUser_EmailNotVerified() {
        user.setStatus("PENDING");
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));

        EmailNotVerifiedException exception = assertThrows(EmailNotVerifiedException.class, () -> {
            userService.loginUser(loginDTO);
        });

        assertEquals("Please verify your email before logging in.", exception.getMessage());
    }

    @Test
    void testLoginUser_WrongPassword() {
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        PasswordInCorrectException exception = assertThrows(PasswordInCorrectException.class, () -> {
            userService.loginUser(loginDTO);
        });

        assertEquals("Wrong Password", exception.getMessage());
    }

    @Test
    void testLoginUser_Success() {
        when(userRepository.findByUsername(loginDTO.getUsername())).thenReturn(Optional.of(user));

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock JWT token generation
        when(jwtUtil.generateToken(userDetails.getUsername(), userDetails.getRole(), userDetails.getId()))
                .thenReturn("jwt-token");

        ResponseEntity<?> response = userService.loginUser(loginDTO);

        assertNotNull(response);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("jwt-token", authResponse.getToken());
        assertEquals(user.getId(), authResponse.getUserId());
        assertEquals(user.getUsername(), authResponse.getUserName());
        assertEquals(user.getRole(), authResponse.getRole());

        // Verify lastLogin updated
        verify(userRepository).save(user);
        assertNotNull(user.getLastLogin());
    }
}
