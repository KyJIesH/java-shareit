package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.model.StatusBooking.*;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final String TAG = "BOOKING SERVICE";
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        log.info("{} - Обработка запроса на добавление бронирования", TAG);
        Item item = checkItem(bookingDto.getItemId());
        User booker = checkUser(userId);
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь принадлежит вам");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Ошибочное время бронирования");
        }
        bookingDto.setStatus(WAITING);
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("{} - Обработка запроса на изменение статуса бронирования", TAG);
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вы не являетесь владельцем вещи");
        }
        if (booking.getStatus() == APPROVED) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        booking.setStatus(approved ? APPROVED : REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        log.info("{} - Обработка запроса пользователея с id {} на бронирование {}", TAG, userId, bookingId);
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        Item item = checkItem(booking.getItem().getId());
        if (!booking.getBooker().getId().equals(userId)) {
            if (!item.getOwner().getId().equals(userId)) {
                throw new NotFoundException("Вы не бронировали эту вещь");
            }
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state, Pageable pageable) {
        log.info("{} - Обработка запроса на получение списка всех бронирований со статусом {} " +
                "пользователя с id {}", TAG, state, userId);
        checkUser(userId);
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("start").descending());
        List<Booking> bookings = new ArrayList<>();
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
                return bookingMapper.toBookingDto(bookings);
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                StatusBooking status = checkStatus(state);
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, status, page);
                return bookingMapper.toBookingDto(bookings);
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                return bookingMapper.toBookingDto(bookings);
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                return bookingMapper.toBookingDto(bookings);
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookerBookings(userId, LocalDateTime.now(), page);
                return bookingMapper.toBookingDto(bookings);
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state, Pageable pageable) {
        log.info("{} - Обработка запроса на получение списка бронирований со статусом {} " +
                "для вещей пользователя с id {}", TAG, state, userId);
        checkUser(userId);
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("start").descending());
        if (itemRepository.findAllByOwnerIdOrderByIdAsc(userId).isEmpty()) {
            throw new ValidationException("У пользователя с id " + userId + " нет вещей");
        }
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, page);
                return bookingMapper.toBookingDto(bookings);
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                StatusBooking status = checkStatus(state);
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, status, page);
                return bookingMapper.toBookingDto(bookings);
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
                return bookingMapper.toBookingDto(bookings);
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
                return bookingMapper.toBookingDto(bookings);
            case "CURRENT":
                bookings = bookingRepository.findCurrentOwnerBookings(userId, LocalDateTime.now(), page);
                return bookingMapper.toBookingDto(bookings);
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    private StatusBooking checkStatus(String state) {
        StatusBooking status = null;
        for (StatusBooking value : StatusBooking.values()) {
            if (value.name().equals(state)) {
                status = value;
            }
        }
        return status;
    }
}
