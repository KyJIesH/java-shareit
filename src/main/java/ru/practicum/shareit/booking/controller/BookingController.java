package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.ErrorMessage;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@AllArgsConstructor
public class BookingController {

    private static final String TAG = "BOOKING CONTROLLER";

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> save(@RequestBody BookingDto bookingDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на добавление бронирования {}", TAG, bookingDto);
        return new ResponseEntity<>(bookingService.createBooking(bookingDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@PathVariable Long bookingId,
                                              @RequestParam Boolean approved,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на изменение статуса бронирования с id {}", TAG, bookingId);
        return new ResponseEntity<>(bookingService.approvedBooking(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable Long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос пользователея с id {} на бронирование {}", TAG, userId, bookingId);
        return new ResponseEntity<>(bookingService.getById(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение списка всех бронирований со статусом {} " +
                "пользователя с id {}", TAG, state, userId);
        return new ResponseEntity<>(bookingService.getAllByBooker(userId, state), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllForOwner(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                           @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("{} - Пришел запрос на получение списка бронирований со статусом {} " +
                "для вещей пользователя с id {}", TAG, state, owner);
        return new ResponseEntity<>(bookingService.getAllByOwner(owner, state), HttpStatus.OK);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleException(ValidationException e) {
        return new ErrorMessage(e.getMessage());
    }
}
