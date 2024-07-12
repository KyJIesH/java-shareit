package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationCreate;
import ru.practicum.shareit.validation.ValidationUpdate;

import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("users")
@Slf4j
@AllArgsConstructor
@Validated
public class UserController {

    private static final String TAG = "GATEWAY USER CONTROLLER";

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(ValidationCreate.class) UserDto userDto) {
        log.info("{} -  Пришел запрос на создание пользователя {}", TAG, userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@NotNull @PathVariable Long id) {
        log.info("{} -  Пришел запрос на получение пользователя по id {}", TAG, id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("{} - Пришел запрос на получение списка всех пользователей", TAG);
        return userClient.getAllUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Validated({ValidationUpdate.class}) @RequestBody UserDto userDto,
                                                  @PathVariable Long id) {
        return userClient.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@NumberFormat @PathVariable Long id) {
        return userClient.deleteUser(id);
    }
}
