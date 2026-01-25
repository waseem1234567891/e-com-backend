package com.chak.E_Commerce_Back_End.service.services.userservice;


import com.chak.E_Commerce_Back_End.dto.user.UserResponseDto;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import com.chak.E_Commerce_Back_End.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllRegisterUsers_Success() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john123");
        user1.setEmail("john@example.com");
        user1.setRole("USER");
        user1.setStatus("ACTIVE");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("jane456");
        user2.setEmail("jane@example.com");
        user2.setRole("ADMIN");
        user2.setStatus("INACTIVE");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponseDto> result = userService.getAllRegisterUsers();

        assertEquals(2, result.size());
        assertEquals("john123", result.get(0).getUsername());
        assertEquals("jane456", result.get(1).getUsername());
    }

    @Test
    void testGetAllRegisterUsers_Empty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> result = userService.getAllRegisterUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllUserUsingPagination_NoSearch_ReturnsAll() {
        int page = 0, size = 2;
        Pageable pageable = PageRequest.of(page, size);

        User user1 = new User();
        user1.setUsername("john123");
        User user2 = new User();
        user2.setUsername("jane456");

        Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponseDto> result = userService.getAllUserUsingPagination(page, size, null);

        assertEquals(2, result.getContent().size());
        assertEquals("john123", result.getContent().get(0).getUsername());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetAllUserUsingPagination_SearchProvided() {
        int page = 0, size = 2;
        String search = "john";
        Pageable pageable = PageRequest.of(page, size);

        User user = new User();
        user.setUsername("john123");

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable))
                .thenReturn(userPage);

        Page<UserResponseDto> result = userService.getAllUserUsingPagination(page, size, search);

        assertEquals(1, result.getContent().size());
        assertEquals("john123", result.getContent().get(0).getUsername());
        verify(userRepository).findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
    }

    @Test
    void testGetAllUserUsingPagination_EmptyResult() {
        int page = 0, size = 2;
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponseDto> result = userService.getAllUserUsingPagination(page, size, null);

        assertTrue(result.getContent().isEmpty());
    }


}
