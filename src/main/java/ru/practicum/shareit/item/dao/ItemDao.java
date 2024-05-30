package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item create(Item item);

    Item getItem(Long id);

    List<Item> getAllItems();

    List<Item> getAllItemsByUserId(Long userId);

    Item update(Item item);

    List<Item> searchByName(String text, Long userId);

    void delete(Long id);

    Item findById(Long id);
}
