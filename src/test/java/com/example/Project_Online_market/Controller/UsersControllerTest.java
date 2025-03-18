package com.example.Project_Online_market.Controller;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.UsersRopository;
import com.example.Project_Online_market.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersRopository usersRepository;

    @InjectMocks
    private UsersController usersController;

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUser_ID(1);
        user.setFirst_Name("John");
        user.setLast_Name("Doe");
        user.setEmail("johndoe@gmail.com");
        user.setPassword("Password123");
    }

    @Test
    void testGetAllUsers() {
        List<Users> usersList = Arrays.asList(user);
        when(usersRepository.findAll()).thenReturn(usersList);

        ResponseEntity<List<Users>> response = usersController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetUserById_Success() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        Users foundUser = usersRepository.findById(1).orElseThrow(() -> new UserNotFoundException("User not found"));
        assertEquals(user.getUser_ID(), foundUser.getUser_ID());
    }

    @Test
    void testGetUserById_NotFound() {
        when(usersRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> usersRepository.findById(2).orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Test
    void testDeleteUser() {
        lenient().when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(usersRepository).delete(user);
        usersRepository.delete(user);
        verify(usersRepository, times(1)).delete(user);
    }
}
