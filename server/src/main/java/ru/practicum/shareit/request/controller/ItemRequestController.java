package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.utils.CheckPage;
import ru.practicum.shareit.utils.SortPage;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {

    private static final String TAG = "REQUEST CONTROLLER";
    private final RequestService requestService;
    private final CheckPage checkPage;
    private final SortPage sortPage;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} -  Пришел запрос на создание потребности {} от пользователя {}", TAG, itemRequestDto, userId);
        ItemRequestDto result = requestService.createItemRequest(userId, itemRequestDto);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsUserSorted(@RequestParam(defaultValue = "0") int from,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение списка всех потребностей пользователя {}", TAG, userId);
        checkPage.checkPage(from, size);
        PageRequest pageRequest = sortPage.sortPageRequest(from, size);
        return new ResponseEntity<>(requestService.getItemRequestsUserSorted(userId, pageRequest), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequestsSorted(@RequestParam(defaultValue = "0") int from,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос пользователя {} на получение списка всех потребностей остальных пользователей", TAG, userId);
        checkPage.checkPage(from, size);
        PageRequest pageRequest = sortPage.sortPageRequest(from, size);
        return new ResponseEntity<>(requestService.getAllItemRequestsSorted(userId, pageRequest), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@PathVariable Long requestId,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} -  Пришел запрос на получение пользователем {} потребности по id {}", TAG, userId, requestId);
        return new ResponseEntity<>(requestService.getItemRequest(requestId, userId), HttpStatus.OK);
    }
}
