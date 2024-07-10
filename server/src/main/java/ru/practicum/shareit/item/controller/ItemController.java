package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.ValidationItem;
import ru.practicum.shareit.utils.CheckPage;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private static final String TAG = "ITEM CONTROLLER";

    private final ItemService itemService;
    private final CheckPage checkPage;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody @Validated(ValidationItem.Create.class) ItemDto itemDto,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на добавление вещи {}", TAG, itemDto);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ItemDto result = itemService.create(itemDto, userId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createItemComment(@PathVariable Long itemId,
                                                        @RequestBody CommentDto commentDto,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на создание отзывов о вещи по id {}", TAG, itemId);
        return new ResponseEntity<>(itemService.createComment(commentDto, itemId, userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение вещи по id {}", TAG, itemId);
        itemService.checkItemId(itemId);
        return new ResponseEntity<>(itemService.getItem(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByUserId(@RequestParam(defaultValue = "0") int from,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение всех вещей", TAG);
        checkPage.checkPage(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return new ResponseEntity<>(itemService.getAllItemsByUserId(userId, pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestBody ItemDto itemDto,
                                          @PathVariable Long id,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на обновление вещи {}", TAG, itemDto);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ItemDto result = itemService.update(itemDto, id, userId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchByText(@RequestParam String text,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на поиск вещей по названию {}", TAG, text);
        checkPage.checkPage(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemDto> result = itemService.searchByText(text, userId, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.info("{} - Пришел запрос на удаление вещи по id {}", TAG, id);
        itemService.checkItemId(id);
        itemService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}