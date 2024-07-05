package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("start").descending());
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, new BookingMapper());

        owner = new User();
        owner.setId(1L);
        owner.setName("ownerName");
        owner.setEmail("mailOwner@mail.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("bookerName");
        booker.setEmail("mailBooker@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setOwner(owner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);

        bookingDto = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getItem(),
                booking.getBooker().getId(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    @Test
    void createBookingTestIncorrectItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(bookingDto, owner.getId())
        );
    }

    @Test
    void createBookingTestIncorrectUser() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())
        );
    }

    @Test
    void createBookingTestIncorrectOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(bookingDto, owner.getId())
        );
    }

    @Test
    void createBookingTestIncorrectTime() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        //incorrectStart
        bookingDto.setStart(null);
        assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())
        );
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));

        //incorrectEnd
        bookingDto.setEnd(null);
        assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())
        );
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(2));

        //startIsAfterEnd
        bookingDto.setStart(LocalDateTime.now().plusMinutes(3));
        assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())
        );
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));

        //startIsBeforeNow
        bookingDto.setStart(LocalDateTime.now().minusMinutes(1));
        assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())
        );
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));

        //startEqualsEnd
        bookingDto.setStart(LocalDateTime.now().plusMinutes(2));
        assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())
        );
        bookingDto.setStart(LocalDateTime.now().plusMinutes(1));
    }

    @Test
    void createBookingTestIncorrectAvailable() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        item.setAvailable(false);

        assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId())
        );
    }

    @Test
    void createBookingTestCorrect() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        bookingService.createBooking(bookingDto, booker.getId());

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void approvedBookingTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.approvedBooking(booker.getId(), booking.getId(), true)
        );
    }

    @Test
    void approvedBookingTestIncorrectBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.approvedBooking(owner.getId(), booking.getId(), true)
        );
    }

    @Test
    void approvedBookingTestIncorrectOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(
                NotFoundException.class,
                () -> bookingService.approvedBooking(booker.getId(), booking.getId(), true)
        );
    }

    @Test
    void approvedBookingTestIncorrectStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        booking.setStatus(StatusBooking.APPROVED);

        assertThrows(
                ValidationException.class,
                () -> bookingService.approvedBooking(owner.getId(), booking.getId(), true)
        );
        booking.setStatus(StatusBooking.WAITING);
    }

    @Test
    void approvedBookingTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.approvedBooking(owner.getId(), booking.getId(), true);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void getByIdTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(owner.getId(), booking.getId())
        );
    }

    @Test
    void getByIdTestIncorrectBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(booker.getId(), booking.getId())
        );
    }

    @Test
    void getByIdTestIncorrectItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(booker.getId(), booking.getId())
        );
    }

    @Test
    void getByIdTestIncorrectBookerOrOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertNotEquals(booking.getBooker().getId(), bookingService.getById(booker.getId(), booking.getId()).getId());
        assertEquals(item.getOwner().getId(), bookingService.getById(booker.getId(), booking.getId()).getId());
    }

    @Test
    void getAllByBookerTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByBooker(booker.getId(), "ALL", pageable)
        );

    }

    @Test
    void getAllByBookerTestIncorrectState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByBooker(booker.getId(), "test", pageable)
        );
    }

    @Test
    void getAllByBookerTestStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBooker(booker.getId(), "ALL", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentBookerBookings(any(), any(), any());
    }

    @Test
    void getAllByBookerTestStateAllWaitingApprovedRejectedCancelled() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingsWaiting = bookingService.getAllByBooker(booker.getId(), "WAITING", pageable);
        assertEquals(1, bookingsWaiting.size());

        List<BookingDto> bookingsApproved = bookingService.getAllByBooker(booker.getId(), "APPROVED", pageable);
        assertEquals(1, bookingsApproved.size());

        List<BookingDto> bookingsRejected = bookingService.getAllByBooker(booker.getId(), "REJECTED", pageable);
        assertEquals(1, bookingsRejected.size());

        List<BookingDto> bookingsCancelled = bookingService.getAllByBooker(booker.getId(), "CANCELED", pageable);
        assertEquals(1, bookingsCancelled.size());

        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentBookerBookings(any(), any(), any());
    }

    @Test
    void getAllByBookerTestStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBooker(booker.getId(), "PAST", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentBookerBookings(any(), any(), any());
    }

    @Test
    void getAllByBookerTestStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBooker(booker.getId(), "FUTURE", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentBookerBookings(any(), any(), any());
    }

    @Test
    void getAllByBookerTestStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentBookerBookings(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByBooker(booker.getId(), "CURRENT", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByBookerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getAllByOwnerTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByOwner(owner.getId(), "ALL", pageable)
        );

    }

    @Test
    void getAllByOwnerTestIncorrectItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of());

        assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByOwner(owner.getId(), "ALL", pageable)
        );
    }

    @Test
    void getAllByOwnerTestIncorrectState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(
                ValidationException.class,
                () -> bookingService.getAllByBooker(owner.getId(), "test", pageable)
        );
    }

    @Test
    void getAllByOwnerTestStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), "ALL", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentOwnerBookings(any(), any(), any());
    }

    @Test
    void getAllByOwnerTestStateAllWaitingApprovedRejectedCancelled() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingsWaiting = bookingService.getAllByOwner(owner.getId(), "WAITING", pageable);
        assertEquals(1, bookingsWaiting.size());

        List<BookingDto> bookingsApproved = bookingService.getAllByOwner(owner.getId(), "APPROVED", pageable);
        assertEquals(1, bookingsApproved.size());

        List<BookingDto> bookingsRejected = bookingService.getAllByOwner(owner.getId(), "REJECTED", pageable);
        assertEquals(1, bookingsRejected.size());

        List<BookingDto> bookingsCancelled = bookingService.getAllByOwner(owner.getId(), "CANCELED", pageable);
        assertEquals(1, bookingsCancelled.size());

        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentOwnerBookings(any(), any(), any());
    }

    @Test
    void getAllByOwnerTestStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), "PAST", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentOwnerBookings(any(), any(), any());
    }

    @Test
    void getAllByOwnerTestStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));

        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), "FUTURE", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findCurrentOwnerBookings(any(), any(), any());
    }

    @Test
    void getAllByOwnerTestStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of(item));

        when(bookingRepository.findCurrentOwnerBookings(anyLong(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), "CURRENT", pageable);
        assertEquals(1, bookings.size());

        verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any(), any());
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any());
    }
}