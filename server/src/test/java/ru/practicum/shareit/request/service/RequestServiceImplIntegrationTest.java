package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    private final PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("created").descending());
    private Item item;
    private ItemDto itemDto;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
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
    @DirtiesContext
    void createItemRequestTest() {
        ItemRequestDto result = requestService.createItemRequest(user.getId(), requestDto);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getItemRequestsUserSortedTest() {
        List<ItemRequestDto> result = requestService.getItemRequestsUserSorted(user.getId(), pageRequest);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getAllItemRequestsSortedTest() {
        List<ItemRequestDto> result = requestService.getAllItemRequestsSorted(user.getId(), pageRequest);
        assertNotNull(result);
    }
}