package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    private final PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("created").descending());
    private Item item;
    private ItemDto itemDto;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(requestRepository,
                new RequestMapper(new ItemMapper(new BookingMapper())), userRepository);

        user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setEmail("mailUser@mail.com");
        userRepository.save(user);

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
                1L,
                item.getRequest(),
                new BookingDto(),
                new BookingDto(),
                new ArrayList<>()
        );

        List<Item> items = new ArrayList<>();
        items.add(item);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("requestDescription");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(items);

        List<ItemDto> itemsDto = new ArrayList<>();
        itemsDto.add(itemDto);

        requestDto = new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated(),
                itemsDto
        );
    }

    @Test
    void createItemRequestTestIncorrectDescription() {
        requestDto.setDescription("");
        assertThrows(
                ValidationException.class,
                () -> requestService.createItemRequest(user.getId(), requestDto)
        );

        requestDto.setDescription(null);
        assertThrows(
                ValidationException.class,
                () -> requestService.createItemRequest(user.getId(), requestDto)
        );
    }

    @Test
    void createItemRequestTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> requestService.createItemRequest(user.getId(), requestDto)
        );

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void createItemRequestTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        requestService.createItemRequest(user.getId(), requestDto);

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestsUserSortedTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> requestService.getItemRequestsUserSorted(user.getId(), pageRequest)
        );

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemRequestsUserSortedTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);

        when(requestRepository.findAllByRequesterId(anyLong(), any())).thenReturn(itemRequests);

        requestService.getItemRequestsUserSorted(user.getId(), pageRequest);

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByRequesterId(anyLong(), any());
    }

    @Test
    void getAllItemRequestsSortedTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> requestService.getAllItemRequestsSorted(user.getId(), pageRequest)
        );

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllItemRequestsSortedTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);

        when(requestRepository.findAll(anyLong(), any())).thenReturn(itemRequests);

        requestService.getAllItemRequestsSorted(user.getId(), pageRequest);

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAll(anyLong(), any());

    }

    @Test
    void getItemRequestTestIncorrectUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> requestService.getItemRequest(itemRequest.getId(), user.getId())
        );

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemRequestTestIncorrectRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> requestService.getItemRequest(itemRequest.getId(), user.getId())
        );

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemRequestTestCorrect() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        requestService.getItemRequest(itemRequest.getId(), user.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }
}