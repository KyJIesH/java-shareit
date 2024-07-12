package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private RequestRepository requestRepository;

    private final Pageable pageable = PageRequest.of(0, 5);
    private Item item;
    private User user;
    private User requester;
    private Comment comment;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setEmail("mailUser@mail.com");
        userRepository.save(user);

        requester = new User();
        requester.setId(2L);
        requester.setName("requesterName");
        requester.setEmail("mailRequester@mail.com");
        userRepository.save(requester);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("requestDescription");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now().minusMinutes(5));
        itemRequest.setItems(new ArrayList<>());
        requestRepository.save(itemRequest);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("commentText");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreationDate(LocalDateTime.now());
        commentRepository.save(comment);

        commentDto = new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreationDate()
        );

        item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequest(itemRequest);
        itemRepository.save(item);

        itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner(),
                item.getAvailable(),
                item.getRequest().getId(),
                item.getRequest(),
                new BookingDto(),
                new BookingDto(),
                new ArrayList<>()
        );
    }

    @Test
    @DirtiesContext
    void createTest() {
        ItemDto result = itemService.create(itemDto, user.getId());
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void createCommentTest() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusMinutes(5));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(StatusBooking.WAITING);
        bookingRepository.save(booking);

        CommentDto result = itemService.createComment(commentDto, item.getId(), user.getId());
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getItem() {
        ItemDto result = itemService.getItem(user.getId(), item.getId());
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getAllItemsByUserIdTest() {
        List<ItemDto> result = itemService.getAllItemsByUserId(user.getId(), pageable);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void updateTest() {
        ItemDto result = itemService.update(itemDto, item.getId(), user.getId());
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void searchByTextTest() {
        List<ItemDto> result = itemService.searchByText("test", user.getId(), pageable);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void deleteTest() {
        itemService.delete(item.getId());
        assertEquals(Optional.empty(), itemRepository.findById(item.getId()));
    }
}