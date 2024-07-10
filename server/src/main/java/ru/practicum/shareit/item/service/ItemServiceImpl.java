package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String TAG = "ITEM SERVICE";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("{} - Обработка запроса на добавление вещи", TAG);
        Item item = itemMapper.toItem(itemDto);
        User owner = checkUser(userId);
        item.setOwner(owner);
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            return null;
        }
        if (itemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Потребность отсутствует")));
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        log.info("{} - Обработка запроса на создание отзыва {} о вещи", TAG, commentDto);
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Текст не может быть пустым");
        }

        Item item = checkItem(itemId);
        User user = checkUser(userId);

        Long bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (bookingsCount == null || bookingsCount == 0) {
            throw new ValidationException("Вещь не была взята в аренду");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreationDate(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        log.info("{} - Обработка запроса на получение вещи по id {}", TAG, itemId);
        checkUser(userId);
        Item item = checkItem(itemId);
        Booking last = getLast(item.getId(), userId);
        Booking next = getNext(item.getId(), userId);
        List<Comment> comments = commentRepository.getAllByItemId(item.getId());
        List<CommentDto> commentsDto = commentMapper.toCommentsDto(comments);
        return itemMapper.toItemDto(item, last, next, commentsDto);
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long id, Pageable pageable) {
        log.info("{} - Обработка получения всех вещей пользователя с id {}", TAG, id);
        checkUser(id);
        Page<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(id, pageable);
        List<ItemDto> result = new ArrayList<>();

        List<Item> itemList = items.getContent();

        List<Long> ids = new ArrayList<>();
        for (Item item : itemList) {
            ids.add(item.getId());
        }

        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusNot(ids, StatusBooking.REJECTED);

        for (Item item : itemList) {
            Booking last = getLastBookingForGetItems(bookings, LocalDateTime.now(), item.getId());
            Booking next = getNextBookingForGetItems(bookings, LocalDateTime.now(), item.getId());
            List<Comment> comments = commentRepository.getAllByItemId(item.getId());
            List<CommentDto> commentsDto = commentMapper.toCommentsDto(comments);
            result.add(itemMapper.toItemDto(item, last, next, commentsDto));
        }
        return result;
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        log.info("{} - Обработка запроса на обновление вещи {}", TAG, itemDto);
        Item item = itemMapper.toItem(itemDto);
        Item temp = checkItem(id);
        User owner = checkUser(userId);
        if (item.getId() == null) {
            item.setId(temp.getId());
        }
        if (item.getName() == null || item.getName().isBlank()) {
            item.setName(temp.getName());
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            item.setDescription(temp.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(temp.getAvailable());
        }
        if (temp.getOwner().getId().equals(owner.getId())) {
            item.setOwner(owner);
            return itemMapper.toItemDto(itemRepository.save(item));
        }
        throw new NotFoundException("Пользователь вещи не найден");
    }

    @Override
    public List<ItemDto> searchByText(String text, Long userId, Pageable pageable) {
        log.info("{} - Обработка запроса на поиск вещи по переданному тексту {}", TAG, text);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        Page<Item> result = itemRepository.search(text, pageable);
        return itemMapper.toItemsDto(result.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("{} - Обработка запроса на удаление вещи по id {}", TAG, id);
        checkItem(id);
        itemRepository.deleteById(id);
    }

    @Override
    public void checkItemId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректный формат id вещи");
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

    private Booking getLast(Long itemId, Long userId) {
        List<Booking> temp = bookingRepository.findPastOwnerBookings(itemId, userId, LocalDateTime.now());
        if (temp.isEmpty()) {
            return null;
        }
        return temp.get(0);
    }

    private Booking getNext(Long itemId, Long userId) {
        List<Booking> temp = bookingRepository.findFutureOwnerBookings(itemId, userId, LocalDateTime.now());
        if (temp.isEmpty()) {
            return null;
        }
        return temp.get(0);
    }

    private Booking getLastBookingForGetItems(List<Booking> bookings, LocalDateTime now, Long itemId) {
        if (bookings.isEmpty()) {
            return null;
        }
        Booking result = null;
        for (Booking booking : bookings) {
            if (!booking.getItem().getId().equals(itemId)) {
                continue;
            }
            if (booking.getStart().isBefore(now)) {
                if (result == null &&
                        ((booking.getStatus().equals(StatusBooking.APPROVED)))) {
                    result = booking;
                } else if (result == null) {
                    result = booking;
                } else if (booking.getEnd().isAfter(result.getEnd())) {
                    result = booking;
                }
            }
        }
        return result;
    }

    private Booking getNextBookingForGetItems(List<Booking> bookings, LocalDateTime now, Long itemId) {
        if (bookings.isEmpty()) {
            return null;
        }
        Booking result = null;
        for (Booking booking : bookings) {
            if (!booking.getItem().getId().equals(itemId)) {
                continue;
            }
            if (booking.getStart().isAfter(now)) {
                if (result == null &&
                        ((booking.getStatus().equals(StatusBooking.APPROVED)) ||
                                (booking.getStatus().equals(StatusBooking.WAITING)))) {
                    result = booking;
                } else if (result == null) {
                    result = booking;
                } else if (booking.getStart().isBefore(result.getStart())) {
                    result = booking;
                }
            }
        }
        return result;
    }
}
