package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private final Pageable pageable = PageRequest.of(0, 5);
    private User owner;
    private User owner2;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("ownerName");
        owner.setEmail("mailOwner@mail.com");
        userRepository.save(owner);

        owner2 = new User();
        owner2.setName("2ownerName");
        owner2.setEmail("2mailOwner@mail.com");
        userRepository.save(owner2);

        Item item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequest(null);
        itemRepository.save(item);

        Item item2 = new Item();
        item2.setName("2itemName");
        item2.setDescription("2itemDescription");
        item2.setOwner(owner);
        item2.setAvailable(true);
        item2.setRequest(null);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("3itemName");
        item3.setDescription("3itemDescription");
        item3.setOwner(owner2);
        item3.setAvailable(true);
        item3.setRequest(null);
        itemRepository.save(item3);
    }

    @Test
    void testSearch() {
        Page<Item> items = itemRepository.search("iTEm", pageable);
        assertEquals(3, items.getTotalElements());
    }

    @Test
    void testListFindAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId());
        assertEquals(2, items.size());
    }

    @Test
    void testPageFindAllByOwnerIdOrderByIdAsc() {
        Page<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId(), pageable);
        assertEquals(2, items.getTotalElements());
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}