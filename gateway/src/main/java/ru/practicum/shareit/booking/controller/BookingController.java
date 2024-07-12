package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.validation.ValidationCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String TAG = "GATEWAY BOOKING CONTROLLER";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> save(@Validated(ValidationCreate.class) @RequestBody BookingDto bookingDto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на добавление бронирования {}", TAG, bookingDto);
        checkTime(bookingDto);
        return bookingClient.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на изменение статуса бронирования с id {}", TAG, bookingId);
        return bookingClient.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@NotNull @PathVariable Long bookingId,
                                          @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос пользователея с id {} на бронирование {}", TAG, userId, bookingId);
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestParam(defaultValue = "ALL") String state,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "10") int size,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("{} - Пришел запрос на получение списка всех бронирований со статусом {} " +
                "пользователя с id {}", TAG, state, userId);
        StateBooking stateBooking = StateBooking.parse(state)
                .orElseThrow(() -> new ValidationException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getAllByUser(state, from, size, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size,
                                                 @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("{} - Пришел запрос на получение списка бронирований со статусом {} " +
                "для вещей пользователя с id {}", TAG, state, owner);
        StateBooking bookingState = StateBooking.parse(state)
                .orElseThrow(() -> new ValidationException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.getAllForOwner(state, from, size, owner);
    }

    private void checkTime(BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            String message = "Время бронирования не корректно";
            log.info(message);
            throw new ValidationException(message);
        }
    }
}
