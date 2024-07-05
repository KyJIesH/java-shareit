package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("start").descending());
    private User booker;
    private User owner;
    private Item item;
    private List<Long> itemsIds;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setName("ownerName");
        owner.setEmail("mailOwner@mail.com");
        userRepository.save(owner);

        item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequest(null);
        itemRepository.save(item);

        itemsIds = new ArrayList<>();
        itemsIds.add(item.getId());

        booker = new User();
        booker.setName("bookerName");
        booker.setEmail("mailBooker@mail.com");
        userRepository.save(booker);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusHours(1));
        booking2.setEnd(LocalDateTime.now().plusHours(2));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(LocalDateTime.now().minusHours(1));
        booking3.setEnd(LocalDateTime.now().plusHours(1));
        booking3.setItem(item);
        booking3.setBooker(booker);
        booking3.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking3);

        Booking booking4 = new Booking();
        booking4.setStart(LocalDateTime.now());
        booking4.setEnd(LocalDateTime.now());
        booking4.setItem(item);
        booking4.setBooker(booker);
        booking4.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking4);

        Booking booking5 = new Booking();
        booking5.setStart(LocalDateTime.now());
        booking5.setEnd(LocalDateTime.now());
        booking5.setItem(item);
        booking5.setBooker(booker);
        booking5.setStatus(StatusBooking.REJECTED);
        bookingRepository.save(booking5);
    }

    @Test
    void testFindAllByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageable);
        assertEquals(5, bookings.size());
    }

    @Test
    void testFindAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(),
                StatusBooking.WAITING, pageable);
        assertEquals(4, bookings.size());
    }

    @Test
    void testFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(),
                LocalDateTime.now(), pageable);
        assertEquals(3, bookings.size());
    }

    @Test
    void testFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(),
                LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
    }

    @Test
    void testFindCurrentBookerBookings() {
        List<Booking> bookings = bookingRepository.findCurrentBookerBookings(booker.getId(), LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
    }

    @Test
    void testFindAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), pageable);
        assertEquals(5, bookings.size());
    }

    @Test
    void testFindAllByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(),
                StatusBooking.WAITING, pageable);
        assertEquals(4, bookings.size());
    }

    @Test
    void testFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(owner.getId(),
                LocalDateTime.now(), pageable);
        assertEquals(3, bookings.size());
    }

    @Test
    void testFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(owner.getId(),
                LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
    }

    @Test
    void testFindCurrentOwnerBookings() {
        List<Booking> bookings = bookingRepository.findCurrentOwnerBookings(owner.getId(),
                LocalDateTime.now(), pageable);
        assertEquals(1, bookings.size());
    }

    @Test
    void testCountAllByItemIdAndBookerIdAndEndBefore() {
        Long count = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(item.getId(), booker.getId(),
                LocalDateTime.now());
        assertEquals(3, count);
    }

    @Test
    void testFindPastOwnerBookings() {
        List<Booking> bookings = bookingRepository.findPastOwnerBookings(item.getId(), owner.getId(), LocalDateTime.now());
        assertEquals(3, bookings.size());
    }

    @Test
    void testFindFutureOwnerBookings() {
        List<Booking> bookings = bookingRepository.findFutureOwnerBookings(item.getId(), owner.getId(), LocalDateTime.now());
        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemIdInAndStatusNot() {
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusNot(itemsIds, StatusBooking.REJECTED);
        assertEquals(4, bookings.size());
    }

    @AfterEach
    public void tearDown() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}