package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto, Long id);

    void deleteUser(Long id);

    int findUserByEmail(String email);
}
