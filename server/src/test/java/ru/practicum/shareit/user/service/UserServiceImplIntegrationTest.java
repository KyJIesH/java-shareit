package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private UserDto userDto;


    @BeforeEach
    void setUp() {
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
    void createUser() {
        UserDto result = userService.createUser(userDto);
        assertNotNull(result);
    }

    @Test
    void getUser() {
        UserDto result = userService.getUser(user.getId());
        assertNotNull(result);
    }

    @Test
    void getAllUsers() {
        List<UserDto> result = userService.getAllUsers();
        assertNotNull(result);
    }

    @Test
    void updateUser() {
        UserDto result = userService.updateUser(userDto, user.getId());
        assertNotNull(result);
    }

    @Test
    void deleteUser() {
        userService.deleteUser(user.getId());
        assertEquals(Optional.empty(), userRepository.findById(user.getId()));
    }

    @Test
    void findUserByEmail() {
        userService.findUserByEmail(user.getEmail());
        assertNotNull(userRepository.findById(user.getId()));
        assertEquals("mailUser@mail.com", user.getEmail());
    }
}