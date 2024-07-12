package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.ValidationCreate;
import ru.practicum.shareit.validation.ValidationUpdate;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    private static final String TAG = "GATEWAY ITEM CONTROLLER";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Validated(ValidationCreate.class) ItemDto itemDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на добавление вещи {}", TAG, itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createItemComment(@PathVariable Long itemId,
                                                    @RequestBody CommentDto commentDto,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на создание отзывов о вещи по id {}", TAG, itemId);
        return itemClient.createItemComment(itemId, commentDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение вещи по id {}", TAG, itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItemsByUserId(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                      @Positive @RequestParam(defaultValue = "10") int size,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение всех вещей", TAG);
        return itemClient.getAllItemsByUserId(from, size, userId);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId, @Validated(ValidationUpdate.class)
                                         @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на обновление вещи {}", TAG, itemDto);
        if (itemDto.getName() == null && itemDto.getDescription() == null && itemDto.getAvailable() == null) {
            throw new ValidationException("Некорректные данные");
        }
        return itemClient.update(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@NotNull @RequestParam(value = "text") String text,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "10") int size,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на поиск вещей по названию {}", TAG, text);
        return itemClient.searchByText(text, from, size, userId);
    }
}
