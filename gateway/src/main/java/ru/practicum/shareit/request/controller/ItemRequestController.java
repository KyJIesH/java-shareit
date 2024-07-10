package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validation.ValidationCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {

    private static final String TAG = "GATEWAY REQUEST CONTROLLER";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody @Validated(ValidationCreate.class) ItemRequestDto itemRequestDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} -  Пришел запрос на создание потребности {} от пользователя {}", TAG, itemRequestDto, userId);
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsUserSorted(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                            @Positive @RequestParam(defaultValue = "10") int size,
                                                            @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение списка всех потребностей пользователя {}", TAG, userId);
        return itemRequestClient.getItemRequestsUserSorted(from, size, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsSorted(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                           @Positive @RequestParam(defaultValue = "10") int size,
                                                           @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос пользователя {} на получение списка всех потребностей остальных пользователей", TAG, userId);
        return itemRequestClient.getAllItemRequestsSorted(from, size, userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequest(@NotNull @PathVariable Long requestId,
                                                 @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} -  Пришел запрос на получение пользователем {} потребности по id {}", TAG, userId, requestId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }
}
