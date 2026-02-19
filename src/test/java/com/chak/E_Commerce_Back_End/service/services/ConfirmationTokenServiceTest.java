package com.chak.E_Commerce_Back_End.service.services;



import com.chak.E_Commerce_Back_End.exception.EmailAlreadyConfirmedException;
import com.chak.E_Commerce_Back_End.model.ConfirmationToken;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.ConfirmationTokenRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository tokenRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    private ConfirmationToken confirmationToken;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setStatus("PENDING");

        confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("valid-token");
        confirmationToken.setUser(user);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        confirmationToken.setConfirmedAt(null);
    }

    // 1️⃣ Invalid token
    @Test
    void shouldThrowExceptionWhenTokenNotFound() {
        when(tokenRepo.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> confirmationTokenService.confirmToken("invalid-token")
        );

        assertEquals("Invalid token", exception.getMessage());
        verify(tokenRepo, never()).save(any());
        verify(userRepo, never()).save(any());
    }

    // 2️⃣ Email already confirmed
    @Test
    void shouldThrowExceptionWhenEmailAlreadyConfirmed() {
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        when(tokenRepo.findByToken("valid-token"))
                .thenReturn(Optional.of(confirmationToken));

        assertThrows(
                EmailAlreadyConfirmedException.class,
                () -> confirmationTokenService.confirmToken("valid-token")
        );

        verify(tokenRepo, never()).save(any());
        verify(userRepo, never()).save(any());
    }

    // 3️⃣ Token expired
    @Test
    void shouldReturnExpiredMessageWhenTokenIsExpired() {
        confirmationToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(tokenRepo.findByToken("valid-token"))
                .thenReturn(Optional.of(confirmationToken));

        String result = confirmationTokenService.confirmToken("valid-token");

        assertEquals("Token expired.", result);
        verify(tokenRepo, never()).save(any());
        verify(userRepo, never()).save(any());
    }

    // 4️⃣ Successful confirmation
    @Test
    void shouldConfirmTokenAndActivateUser() {
        when(tokenRepo.findByToken("valid-token"))
                .thenReturn(Optional.of(confirmationToken));

        String result = confirmationTokenService.confirmToken("valid-token");

        assertEquals("Email confirmed successfully.", result);
        assertNotNull(confirmationToken.getConfirmedAt());
        assertEquals("ACTIVE", user.getStatus());

        verify(tokenRepo, times(1)).save(confirmationToken);
        verify(userRepo, times(1)).save(user);
    }
}

