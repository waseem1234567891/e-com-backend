package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.user.AddressDTO;
import com.chak.E_Commerce_Back_End.model.Address;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.AddressRepo;

import com.chak.E_Commerce_Back_End.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    @Autowired
    private AddressRepo adressRepo;

    @Autowired
    private UserRepository userRepository;

    public List<Address> getAddressesByUserId(Long userId) {
        // Assuming your repository returns List<Address> (empty if no addresses)
        return adressRepo.findByUserId(userId);
    }

    public Address addUserAddress(Long userId,AddressDTO addressDTO)
    {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Address address = new Address();
            address.setHouseNumber(addressDTO.getHouseNumber());
            address.setId(addressDTO.getId());
            address.setCity(addressDTO.getCity());
            address.setCountry(addressDTO.getCountry());
            address.setPostalCode(addressDTO.getPostalCode());
            address.setState(addressDTO.getState());
            address.setStreet(addressDTO.getStreet());
            address.setUser(user);
            return adressRepo.save(address);
        }else {
            throw new UsernameNotFoundException("User not found with id "+userId);
        }
    }



    public void deleteAddress(Long userId, Long addressId) {
        Address address = adressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // optional safety: ensure address belongs to user
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this address");
        }

        adressRepo.delete(address);
    }

    public AddressDTO updateAddress(AddressDTO dto) {
        Address address = adressRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Address not found"));



        // Update fields
        address.setHouseNumber(dto.getHouseNumber());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());

        Address saved = adressRepo.save(address);

        // Convert to DTO to return
        AddressDTO updatedDto = new AddressDTO(
                saved.getId(),
                saved.getHouseNumber(),
                saved.getStreet(),
                saved.getCity(),
                saved.getState(),
                saved.getPostalCode(),
                saved.getCountry()

        );

        return updatedDto;
    }
}
