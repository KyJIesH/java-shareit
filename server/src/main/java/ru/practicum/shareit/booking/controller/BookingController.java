package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.CheckPage;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@AllArgsConstructor
public class BookingController {

    private static final String TAG = "BOOKING CONTROLLER";

    private final BookingService bookingService;
    private final CheckPage checkPage;

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
    public ResponseEntity<List<BookingDto>> getAllByUser(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение списка всех бронирований со статусом {} " +
                "пользователя с id {}", TAG, state, userId);
        checkPage.checkPage(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return new ResponseEntity<>(bookingService.getAllByBooker(userId, state, pageable), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("{} - Пришел запрос на получение списка бронирований со статусом {} " +
                "для вещей пользователя с id {}", TAG, state, owner);
        checkPage.checkPage(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return new ResponseEntity<>(bookingService.getAllByOwner(owner, state, pageable), HttpStatus.OK);
    }
}
