package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final PageRequest pageRequest = PageRequest.of(0, 5,Sort.by("created").descending());
    private Item item;
    private User owner;
    private User requester;
    private User requester2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("ownerName");
        owner.setEmail("owner@email.com");
        userRepository.save(owner);

        requester = new User();
        requester.setName("requesterName");
        requester.setEmail("maiRequester@mail.com");
        userRepository.save(requester);

        requester2 = new User();
        requester2.setName("2requesterName");
        requester2.setEmail("2mailRequester@mail.com");
        userRepository.save(requester2);

        ItemRequest request = new ItemRequest();
        request.setRequester(requester);
        request.setDescription("requestDescription");
        request.setCreated(LocalDateTime.now().minusHours(3));
        requestRepository.save(request);

        ItemRequest request2 = new ItemRequest();
        request2.setRequester(requester);
        request2.setDescription("2requestDescription");
        request2.setCreated(LocalDateTime.now().minusHours(2));
        requestRepository.save(request2);

        ItemRequest request3 = new ItemRequest();
        request3.setRequester(requester2);
        request3.setDescription("3requestDescription");
        request3.setCreated(LocalDateTime.now().minusHours(1));
        requestRepository.save(request3);

        item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequest(request);
        itemRepository.save(item);
    }

    @Test
    void testFindAllByRequesterId() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(requester.getId(), pageRequest);
        assertEquals(2, requests.size());
    }

    @Test
    void testFindAll() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(requester.getId(), pageRequest);
        assertEquals(2, requests.size());
    }

    @AfterEach
    void tearDown() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}