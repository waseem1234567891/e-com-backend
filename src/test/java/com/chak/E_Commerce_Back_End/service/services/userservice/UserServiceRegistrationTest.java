package com.chak.E_Commerce_Back_End.service.services.userservice;

import com.chak.E_Commerce_Back_End.dto.auth.UserDTO;
import com.chak.E_Commerce_Back_End.exception.UserAlreadyExistsException;
import com.chak.E_Commerce_Back_End.model.ConfirmationToken;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.ConfirmationTokenRepository;
import com.chak.E_Commerce_Back_End.repository.OrderRepo;
import com.chak.E_Commerce_Back_End.repository.TokenRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.EmailService;
import com.chak.E_Commerce_Back_End.service.UserService;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRegistrationTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUsername("john123");
        userDTO.setEmail("john@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(userDTO);

        // Verify User is saved with correct fields
        assertEquals(userDTO.getFirstName(), savedUser.getFirstName());
        assertEquals(userDTO.getLastName(), savedUser.getLastName());
        assertEquals(userDTO.getUsername(), savedUser.getUsername());
        assertEquals(userDTO.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("PENDING", savedUser.getStatus());
        assertEquals(userDTO.getRole(), savedUser.getRole());

        // Verify token is saved
        ArgumentCaptor<ConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
        verify(confirmationTokenRepository).save(tokenCaptor.capture());
        ConfirmationToken token = tokenCaptor.getValue();
        assertNotNull(token.getToken());
        assertEquals(savedUser, token.getUser());
        assertTrue(token.getExpiresAt().isAfter(token.getCreatedAt()));

        // Verify email sent
        verify(emailService).sendSimpleEmail(eq(savedUser.getEmail()), eq("Confirm Your Email"), contains(token.getToken()));
    }


    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(userDTO);
        });

        assertEquals("This Email Address is Already Registered", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(confirmationTokenRepository, never()).save(any());
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(new User()));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(userDTO);
        });

        assertEquals("Username already exists", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(confirmationTokenRepository, never()).save(any());
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testPasswordIsEncoded() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(userDTO);

        assertEquals("encodedPassword", savedUser.getPassword());
        assertNotEquals(userDTO.getPassword(), savedUser.getPassword());
    }

    @Test
    void testConfirmationTokenExpiry() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(userDTO);

        ArgumentCaptor<ConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
        verify(confirmationTokenRepository).save(tokenCaptor.capture());
        ConfirmationToken token = tokenCaptor.getValue();

        assertEquals(token.getCreatedAt().plusMinutes(15), token.getExpiresAt());
    }

    @Test
    void testEmailContainsToken() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(userDTO);

        ArgumentCaptor<String> emailBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendSimpleEmail(eq(userDTO.getEmail()), eq("Confirm Your Email"), emailBodyCaptor.capture());

        String emailBody = emailBodyCaptor.getValue();
        assertTrue(emailBody.contains("http://localhost:8989/auth/confirm?token="));
    }

    @Test
    void testRegisterUser_EmailSendingFails() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        // Simulate user save normally
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Simulate email sending failure
        doThrow(new RuntimeException("Email service failure"))
                .when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        // Expect RuntimeException when sending email
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(userDTO);
        });

        assertEquals("Email service failure", exception.getMessage());

        // Verify that user was saved
        verify(userRepository).save(any(User.class));

        // Verify that confirmation token was saved
        verify(confirmationTokenRepository).save(any(ConfirmationToken.class));

        // Verify email sending was attempted
        verify(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
    }












}

