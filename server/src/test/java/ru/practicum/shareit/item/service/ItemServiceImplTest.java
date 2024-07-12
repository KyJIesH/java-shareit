package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;

    private final Pageable pageable = PageRequest.of(0, 5, Sort.by("start").descending());
    private Item item;
    private User user;
    private User booker;
    private User requester;
    private Comment comment;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Booking booking;
    private List<Booking> bookings;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                new ItemMapper(new BookingMapper()), new CommentMapper(), requestRepository);

        user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setEmail("mailUser@mail.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("bookerName");
        booker.setEmail("mailBooker@mail.com");

        requester = new User();
        requester.setId(2L);
        requester.setName("requesterName");
        requester.setEmail("mailRequester@mail.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("requestDescription");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now().minusHours(5));
        itemRequest.setItems(new ArrayList<>());

        comment = new Comment();
        comment.setId(1L);
        comment.setText("commentText");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreationDate(LocalDateTime.now());

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

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);

        bookings = new ArrayList<>();
        bookings.add(booking);
    }

    @Test
    void createTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.create(itemDto, user.getId())
        );
    }

    @Test
    void createTestIncorrectDescriptionOrRequestId() {
        item.setDescription(null);
        assertNull(item.getDescription());

        item.setDescription("");
        assertEquals("", item.getDescription());

        item.setDescription("itemDescription");

        itemDto.setRequestId(null);
        assertNull(itemDto.getRequestId());

        itemDto.setRequestId(1L);
    }

    @Test
    void createTestIncorrectRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> itemService.create(itemDto, user.getId())
        );
    }

    @Test
    void createTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemService.create(itemDto, user.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }



    @Test
    void createCommentTestIncorrectItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(commentDto, item.getId(), user.getId())
        );
    }

    @Test
    void createCommentTestIncorrectUser() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(commentDto, item.getId(), user.getId())
        );
    }

    @Test
    void createCommentTestIncorrectBookingsCount() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);

        assertThrows(
                ValidationException.class,
                () -> itemService.createComment(commentDto, item.getId(), user.getId())
        );
    }

    @Test
    void createCommentTestCorrect() {
        when(bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(1L);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        itemService.createComment(commentDto, item.getId(), user.getId());

        verify(bookingRepository, times(1)).countAllByItemIdAndBookerIdAndEndBefore(anyLong(),
                anyLong(), any(LocalDateTime.class));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getItemTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(user.getId(), item.getId())
        );
    }

    @Test
    void getItemTestIncorrectItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(user.getId(), item.getId())
        );
    }

    @Test
    void getItemTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        itemService.getItem(user.getId(), item.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllItemsByUserIdTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.getAllItemsByUserId(user.getId(), pageable)
        );

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllItemsByUserIdTestIncorrectLast() throws NoSuchMethodException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> exp = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(exp);

        Booking lastBooking = new Booking();
        lastBooking.setId(2L);
        lastBooking.setStart(LocalDateTime.now().plusMinutes(10));
        lastBooking.setEnd(LocalDateTime.now().plusHours(1).plusMinutes(30));
        lastBooking.setItem(item);
        lastBooking.setBooker(booker);
        lastBooking.setStatus(StatusBooking.APPROVED);
        bookings.add(lastBooking);

        when(bookingRepository.findByItemIdInAndStatusNot(any(), any())).thenReturn(bookings);

        assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(1L, bookings.get(1).getItem().getId());
        assertTrue(bookings.get(1).getStart().isBefore(bookings.get(0).getStart()));
        assertEquals(StatusBooking.APPROVED, lastBooking.getStatus());
        assertTrue(bookings.get(0).getEnd().isAfter(bookings.get(1).getEnd()));

        itemService.getAllItemsByUserId(user.getId(), pageable);

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong(), any());
        verify(bookingRepository, times(1)).findByItemIdInAndStatusNot(any(), any());
    }

    @Test
    void getAllItemsByUserIdTestIncorrectNext() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> exp = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(exp);

        Booking bookingTest = new Booking();
        bookingTest.setId(3L);
        bookingTest.setStart(LocalDateTime.now().minusHours(2));
        bookingTest.setEnd(LocalDateTime.now().minusHours(1));
        bookingTest.setItem(item);
        bookingTest.setBooker(booker);
        bookingTest.setStatus(StatusBooking.WAITING);

        List<Booking> bookingsTest = new ArrayList<>();
        bookingsTest.add(bookingTest);

        Booking nextBooking = new Booking();
        nextBooking.setId(4L);
        nextBooking.setStart(LocalDateTime.now().plusHours(1).plusMinutes(10));
        nextBooking.setEnd(LocalDateTime.now().plusHours(2).plusMinutes(30));
        nextBooking.setItem(item);
        nextBooking.setBooker(booker);
        nextBooking.setStatus(StatusBooking.APPROVED);
        bookingsTest.add(nextBooking);

        when(bookingRepository.findByItemIdInAndStatusNot(any(), any())).thenReturn(bookingsTest);

        assertEquals(1L, bookingsTest.get(0).getItem().getId());
        assertEquals(1L, bookingsTest.get(1).getItem().getId());
        assertTrue(bookingsTest.get(1).getStart().isAfter(bookingsTest.get(0).getStart()));
        assertNotEquals("APPROVED", bookingsTest.get(1).getStatus());
        assertTrue(bookingsTest.get(1).getEnd().isAfter(bookingsTest.get(0).getEnd()));

        itemService.getAllItemsByUserId(user.getId(), pageable);

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong(), any());
        verify(bookingRepository, times(1)).findByItemIdInAndStatusNot(any(), any());
    }

    @Test
    void getAllItemsByUserIdTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> exp = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(exp);

        List<Long> ids = new ArrayList<>();
        for (Item item : exp) {
            ids.add(item.getId());
        }

        when(bookingRepository.findByItemIdInAndStatusNot(any(), any())).thenReturn(bookings);

        itemService.getAllItemsByUserId(user.getId(), pageable);

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong(), any());
        verify(bookingRepository, times(1)).findByItemIdInAndStatusNot(any(), any());
    }

    @Test
    void updateTestIncorrectItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, item.getId(), user.getId())
        );
    }

    @Test
    void updateTestIncorrectUser() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, item.getId(), user.getId())
        );
    }

    @Test
    void updateTestCorrect() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertNotEquals(null, item.getId());
        assertEquals(1L, item.getId());

        assertNotEquals(null, item.getName());
        assertEquals("itemName", item.getName());

        assertNotEquals(null, item.getDescription());
        assertEquals("itemDescription", item.getDescription());

        assertNotEquals(null, item.getAvailable());
        assertEquals(true, item.getAvailable());

        assertEquals(item.getOwner().getId(), user.getId());

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemService.update(itemDto, item.getId(), user.getId());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void searchByTextTest() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> exp = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.search(any(), any())).thenReturn(exp);

        itemService.searchByText("test", user.getId(), pageable);

        verify(itemRepository, times(1)).search(any(), any());
    }

    @Test
    void deleteTestIncorrectItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.delete(item.getId())
        );
    }

    @Test
    void deleteTestCorrect() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        itemService.delete(item.getId());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void checkItemIdTest() {
        item.setId(null);
        assertThrows(
                ValidationException.class,
                () -> itemService.checkItemId(item.getId())
        );

        item.setId(-1L);
        assertThrows(
                ValidationException.class,
                () -> itemService.checkItemId(item.getId())
        );
    }
}