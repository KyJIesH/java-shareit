package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {

    private static final String TAG = "REQUEST CONTROLLER";
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("{} -  Пришел запрос на создание потребности {}", TAG, itemRequestDto);
        ItemRequestDto result = requestService.createItemRequest(itemRequestDto);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@PathVariable Long id) {
        log.info("{} -  Пришел запрос на получение потребности по id {}", TAG, id);
        return new ResponseEntity<>(requestService.getItemRequest(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequests() {
        log.info("{} - Пришел запрос на получение списка всех потребностей", TAG);
        return new ResponseEntity<>(requestService.getAllItemRequests(), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemRequestDto> updateItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                              @PathVariable Long id) {
        log.info("{} -  Пришел запрос на обновление потребности {}", TAG, itemRequestDto);
        return new ResponseEntity<>(requestService.updateItemRequest(itemRequestDto, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItemRequest(@PathVariable Long id) {
        log.info("{} -  Пришел запрос на удаление потребности по id {}", TAG, id);
        requestService.deleteItemRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
