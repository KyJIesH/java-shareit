package ru.practicum.shareit.item.comment.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("userName");
        user.setEmail("mailUser@mail.com");
        userRepository.save(user);

        item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequest(null);
        itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("comment");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreationDate(LocalDateTime.now().minusHours(2));
        commentRepository.save(comment);

        Comment comment2 = new Comment();
        comment2.setText("2comment");
        comment2.setItem(item);
        comment2.setAuthor(user);
        comment2.setCreationDate(LocalDateTime.now().minusHours(1));
        commentRepository.save(comment2);
    }

    @Test
    void testGetAllByItemId() {
        List<Comment> comments = commentRepository.getAllByItemId(item.getId());
        assertEquals(2, comments.size());
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}