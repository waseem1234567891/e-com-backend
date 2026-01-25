package com.chak.E_Commerce_Back_End.service.services.userservice;




import com.chak.E_Commerce_Back_End.dto.auth.ProfileDto;
import com.chak.E_Commerce_Back_End.dto.user.UserDetailsDTO;
import com.chak.E_Commerce_Back_End.dto.user.UserUpdateDto;
import com.chak.E_Commerce_Back_End.model.*;
import com.chak.E_Commerce_Back_End.repository.TokenRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.EmailService;
import com.chak.E_Commerce_Back_End.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceAdditionalTest {

    @InjectMocks
    private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private EmailService emailService;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setUsername("john123");
        user.setPassword("encoded");
        user.setRole("USER");
        user.setStatus("ACTIVE");
    }

    // ================= deleteUser =================
    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    // ================= editUser =================
    @Test
    void testEditUser_UserExists() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername("newName");
        dto.setRole("ADMIN");
        dto.setStatus("ACTIVE");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.editUser(1L, dto);

        assertEquals("newName", user.getUsername());
        assertEquals("ADMIN", user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void testEditUser_UserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserUpdateDto dto = new UserUpdateDto();
        userService.editUser(1L, dto);
        verify(userRepository, never()).save(any());
    }

    // ================= updateProfile =================
    @Test
    void testUpdateProfile_UserExists() {
        ProfileDto dto = new ProfileDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User updated = userService.updateProfile(1L, dto);

        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ProfileDto dto = new ProfileDto();

        assertThrows(UsernameNotFoundException.class, () -> userService.updateProfile(1L, dto));
    }

    // ================= initiatePasswordReset =================
    @Test
    void testInitiatePasswordReset_UserExists() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        userService.initiatePasswordReset("john@example.com");

        verify(tokenRepository).save(any());
        verify(emailService).sendPasswordResetEmail(eq("john@example.com"), anyString());
    }

    @Test
    void testInitiatePasswordReset_UserNotFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.initiatePasswordReset("john@example.com"));
    }

    // ================= validateToken =================
    @Test
    void testValidateToken_Valid() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        ResponseEntity<String> response = userService.validateToken("token");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testValidateToken_Invalid() {
        when(tokenRepository.findByToken("token")).thenReturn(Optional.empty());
        ResponseEntity<String> response = userService.validateToken("token");
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testValidateToken_Expired() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        ResponseEntity<String> response = userService.validateToken("token");
        assertEquals(400, response.getStatusCodeValue());
    }

    // ================= resetPassword =================
    @Test
    void testResetPassword_Success() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token");
        token.setEmail("john@example.com");
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        ResponseEntity<String> response = userService.resetPassword("token", "newPass");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("encodedPass", user.getPassword());
        verify(tokenRepository).delete(token);
        verify(userRepository).save(user);
    }

    @Test
    void testResetPassword_TokenInvalid() {
        when(tokenRepository.findByToken("token")).thenReturn(Optional.empty());
        ResponseEntity<String> response = userService.resetPassword("token", "newPass");
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testResetPassword_TokenExpired() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        ResponseEntity<String> response = userService.resetPassword("token", "newPass");
        assertEquals(400, response.getStatusCodeValue());
    }

    // ================= getUserByUserId =================
    @Test
    void testGetUserByUserId_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User u = userService.getUserByUserId(1L);
        assertEquals("john123", u.getUsername());
    }

    @Test
    void testGetUserByUserId_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUserId(1L));
    }

    // ================= forgetUserName =================
    @Test
    void testForgetUserName_UserExists() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        userService.forgetUserName("john@example.com");
        verify(emailService).sendSimpleEmail(eq("john@example.com"), eq("User name"),
                contains(user.getUsername()));
    }

    @Test
    void testForgetUserName_UserNotFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.forgetUserName("john@example.com"));
    }

    // ================= getUserDetailsByUserId =================
    @Test
    void testGetUserDetailsByUserId_UserExists() {
        user.setOrders(List.of());
        user.setAddresses(List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDetailsDTO dto = userService.getUserDetailsByUserId(1L);

        assertEquals("john123", dto.getUsername());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void testGetUserDetailsByUserId_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserDetailsByUserId(1L));
    }
}

