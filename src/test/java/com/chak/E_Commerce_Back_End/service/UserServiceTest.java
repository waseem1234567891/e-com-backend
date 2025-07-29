package com.chak.E_Commerce_Back_End.service;

import com.chak.E_Commerce_Back_End.dto.LoginDTO;
import com.chak.E_Commerce_Back_End.dto.UserDTO;
import com.chak.E_Commerce_Back_End.model.User;
import com.chak.E_Commerce_Back_End.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        UserDTO userDTO = new UserDTO("ACTIVE","john_doe", "password123", "john@example.com","USER");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(userDTO);

        assertEquals(userDTO.getUsername(), savedUser.getUsername());
        assertTrue(passwordEncoder.matches(userDTO.getPassword(), savedUser.getPassword()));
        assertEquals(userDTO.getEmail(), savedUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginUser_Success() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(1L,"ACTIVE", "john_doe", encodedPassword, "john@example.com","USER",null);
        LoginDTO loginDTO = new LoginDTO("john_doe", rawPassword);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        User loggedInUser = userService.loginUser(loginDTO);

        assertNotNull(loggedInUser);
        assertEquals("john_doe", loggedInUser.getUsername());
    }

    @Test
    void testLoginUser_Failure() {
        LoginDTO loginDTO = new LoginDTO("john_doe", "wrongPassword");

        User user = new User(1L,"ACTIVE", "john_doe", passwordEncoder.encode("correctPassword"), "john@example.com","USER",null);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        User result = userService.loginUser(loginDTO);

        assertNull(result);
    }

    @Test
    void testGetAllRegisterUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L,"ACTIVE" ,"user1", "pwd1", "user1@example.com","USER",null),
                new User(2L,"ACTIVE","user2", "pwd2", "user2@example.com","USER",null)
        ));

        List<User> users = userService.getAllRegisterUsers();
        assertEquals(2, users.size());
    }

}
