package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    User create(User user);

    User getUser(Long id);

    List<User> getAllUsers();

    User update(User user, Long id);

    void delete(Long id);
}
