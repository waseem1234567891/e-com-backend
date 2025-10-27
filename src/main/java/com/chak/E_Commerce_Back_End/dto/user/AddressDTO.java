package com.chak.E_Commerce_Back_End.dto.user;

import com.chak.E_Commerce_Back_End.model.Address;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;           // Optional, needed for edit/delete
    private Integer houseNumber;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private boolean primary;

    public AddressDTO() {
    }
    public AddressDTO(Address address) {
        this.id=address.getId();
        this.houseNumber=address.getHouseNumber();
        this.street=address.getStreet();
        this.city=address.getCity();
        this.state=address.getState();
        this.postalCode=address.getPostalCode();
        this.country=address.getCountry();
    }

    public AddressDTO(Long id,Integer houseNumber, String street, String city, String state, String postalCode, String country) {
        this.id = id;
        this.houseNumber=houseNumber;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }



    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }
}
