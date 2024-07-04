package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, new UserMapper());

        user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setEmail("mailUser@mail.com");
        userRepository.save(user);

        userDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Test
    void createUserTestCorrect() {
        when(userRepository.save(user)).thenReturn(user);
        userService.createUser(userDto);
        verify(userRepository, times(2)).save(any());
    }

    @Test
    void getUserTestIncorrectUser() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> userService.getUser(user.getId())
        );
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void getUserTestCorrect() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.getUser(user.getId());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void getAllUsersTestCorrect() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        userService.getAllUsers();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUserTestIncorrectUser() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(userDto, user.getId())
        );
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void updateUserTestCorrect() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertNotEquals(null, userDto.getName());
        assertNotEquals("", userDto.getName());
        assertNotEquals(null, userDto.getEmail());
        assertNotEquals("", userDto.getEmail());

        when(userRepository.save(any())).thenReturn(user);
        userService.updateUser(userDto, user.getId());

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(2)).save(any());
    }

    @Test
    void deleteUserTestIncorrectUser() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser(user.getId())
        );
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void deleteUserTestCorrect() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    void findUserByEmailTestCorrect() {
        User testUser = new User();
        testUser.setEmail("mailUser@mail.com");

        when(userRepository.findAll()).thenReturn(List.of(user));
        assertEquals(testUser.getEmail(), user.getEmail());
        userService.findUserByEmail(testUser.getEmail());

        verify(userRepository, times(1)).findAll();
    }
}