package com.chak.E_Commerce_Back_End.controller;


import com.chak.E_Commerce_Back_End.dto.user.AddressDTO;
import com.chak.E_Commerce_Back_End.model.Address;
import com.chak.E_Commerce_Back_End.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping("/{userId}")
    public List<Address> getUserAdresses(@PathVariable Long userId)
    {
        return addressService.getAddressesByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> addUserAdress(@RequestBody AddressDTO addressDTO)
    {
        Address address = addressService.addUserAddress(addressDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(address);
    }
    @DeleteMapping("/{userId}/address/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressDTO dto) {

        dto.setId(id); // ensure DTO has the ID of the address being updated
        AddressDTO updated = addressService.updateAddress(dto);
        return ResponseEntity.ok(updated);
    }
}
