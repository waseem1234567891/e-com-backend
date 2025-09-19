package com.chak.E_Commerce_Back_End.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class ProfileDto {
    @NotBlank(message = "First name cannot be empty")
    private String firstName; // ✅ New field

    @NotBlank(message = "Last name cannot be empty")
    private String lastName; // ✅ New field



    public ProfileDto() {
    }

    public ProfileDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


}
