package com.chak.E_Commerce_Back_End.service.services;

import com.chak.E_Commerce_Back_End.dto.user.AddressDTO;
import com.chak.E_Commerce_Back_End.model.Address;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.AddressRepo;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepo addressRepo;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private Address address;
    private AddressDTO addressDTO;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        address = new Address();
        address.setId(10L);
        address.setHouseNumber(12);
        address.setStreet("Main Street");
        address.setCity("NY");
        address.setState("NY");
        address.setPostalCode("10001");
        address.setCountry("USA");
        address.setUser(user);

        addressDTO = new AddressDTO(
                10L,
                12,
                "Main Street",
                "NY",
                "NY",
                "10001",
                "USA"
        );
    }

    // ===========================
    // getAddressesByUserId
    // ===========================

    @Test
    void getAddressesByUserId_success() {
        when(addressRepo.findByUserId(1L)).thenReturn(List.of(address));

        List<Address> result = addressService.getAddressesByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(address, result.get(0));
    }

    @Test
    void getAddressesByUserId_empty() {
        when(addressRepo.findByUserId(1L)).thenReturn(Collections.emptyList());

        List<Address> result = addressService.getAddressesByUserId(1L);

        assertTrue(result.isEmpty());
    }

    // ===========================
    // addUserAddress
    // ===========================

    @Test
    void addUserAddress_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepo.save(any(Address.class))).thenReturn(address);

        Address saved = addressService.addUserAddress(1L, addressDTO);

        assertNotNull(saved);
        verify(addressRepo, times(1)).save(any(Address.class));
    }

    @Test
    void addUserAddress_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> addressService.addUserAddress(1L, addressDTO));

        verify(addressRepo, never()).save(any());
    }

    // ===========================
    // deleteAddress
    // ===========================

    @Test
    void deleteAddress_success() {
        when(addressRepo.findById(10L)).thenReturn(Optional.of(address));

        addressService.deleteAddress(1L, 10L);

        verify(addressRepo, times(1)).delete(address);
    }

    @Test
    void deleteAddress_notFound() {
        when(addressRepo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> addressService.deleteAddress(1L, 10L));
    }

    @Test
    void deleteAddress_unauthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        address.setUser(anotherUser);

        when(addressRepo.findById(10L)).thenReturn(Optional.of(address));

        assertThrows(RuntimeException.class,
                () -> addressService.deleteAddress(1L, 10L));

        verify(addressRepo, never()).delete(any());
    }

    // ===========================
    // updateAddress
    // ===========================

    @Test
    void updateAddress_success() {
        when(addressRepo.findById(10L)).thenReturn(Optional.of(address));
        when(addressRepo.save(any(Address.class))).thenReturn(address);

        AddressDTO updated = addressService.updateAddress(addressDTO);

        assertEquals(12, updated.getHouseNumber());
        assertEquals("Main Street", updated.getStreet());
        assertEquals("USA", updated.getCountry());

        verify(addressRepo, times(1)).save(address);
    }

    @Test
    void updateAddress_notFound() {
        when(addressRepo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> addressService.updateAddress(addressDTO));
    }
}
