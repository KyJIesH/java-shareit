package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.utils.ErrorMessage;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.ValidationItem;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private static final String TAG = "ITEM CONTROLLER";

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody @Validated(ValidationItem.Create.class) ItemDto itemDto,
                                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
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
    public ResponseEntity<CommentDto> createItemComment(@PathVariable @Valid Long itemId,
                                                        @RequestBody @Valid CommentDto commentDto,
                                                        @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на создание отзывов о вещи по id {}", TAG, itemId);
        return new ResponseEntity<>(itemService.createComment(commentDto, itemId, userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable @Valid Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение вещи по id {}", TAG, itemId);
        itemService.checkItemId(itemId);
        return new ResponseEntity<>(itemService.getItem(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение всех вещей", TAG);
        return new ResponseEntity<>(itemService.getAllItemsByUserId(userId), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestBody @Valid ItemDto itemDto,
                                          @PathVariable Long id,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
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
    public ResponseEntity<List<ItemDto>> searchByText(@RequestParam @NotBlank String text,
                                                      @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("{} - Пришел запрос на поиск вещей по названию {}", TAG, text);
        List<ItemDto> result = itemService.searchByText(text, userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.info("{} - Пришел запрос на удаление вещи по id {}", TAG, id);
        itemService.checkItemId(id);
        itemService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleException(ConstraintViolationException e) {
        return new ErrorMessage(e.getMessage());
    }
}