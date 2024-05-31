package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto item, Long userId);

    ItemDto getItem(Long id);

    List<ItemDto> getAllItems();

    List<ItemDto> getAllItemsByUserId(Long id);

    ItemDto update(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> searchByName(String text, Long userId);

    void delete(Long id);

    void checkItemId(Long id);
}
