package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("start").descending());
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("ownerName");
        owner.setEmail("mailOwner@mail.com");
        userRepository.save(owner);

        booker = new User();
        booker.setId(2L);
        booker.setName("bookerName");
        booker.setEmail("mailBooker@mail.com");
        userRepository.save(booker);

        item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setOwner(booker);
        item.setAvailable(true);
        itemRepository.save(item);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking);

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
    @DirtiesContext
    void createBookingTest() {
        BookingDto result = bookingService.createBooking(bookingDto, owner.getId());
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void approvedBookingTest() {
        BookingDto result = bookingService.approvedBooking(booker.getId(), booking.getId(), true);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getByIdTest() {
        BookingDto result = bookingService.getById(booker.getId(), booking.getId());
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getAllByBookerTest() {
        List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), "ALL", pageable);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getAllByOwnerTest() {
        List<BookingDto> result = bookingService.getAllByOwner(booker.getId(), "ALL", pageable);
        assertNotNull(result);
    }
}