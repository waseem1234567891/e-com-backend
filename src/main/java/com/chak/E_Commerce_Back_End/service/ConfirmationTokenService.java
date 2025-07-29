package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.model.ConfirmationToken;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.ConfirmationTokenRepository;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConfirmationTokenService {

    @Autowired
    private ConfirmationTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepo;

    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid token"));

        if (confirmationToken.getConfirmedAt() != null) {
            return "Email already confirmed.";
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "Token expired.";
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        tokenRepo.save(confirmationToken);

        User user = confirmationToken.getUser();
        user.setStatus("ACTIVE"); // or a boolean like setEnabled(true)
        userRepo.save(user);

        return "Email confirmed successfully.";
    }
}

