package com.chak.E_Commerce_Back_End.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "Username cannot be empty")
    private String username;
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
