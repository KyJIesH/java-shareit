package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto item, Long userId);

    CommentDto createComment(CommentDto comment, Long itemId, Long userId);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getAllItemsByUserId(Long id, Pageable pageable);

    ItemDto update(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> searchByText(String text, Long userId, Pageable pageable);

    void delete(Long id);

    void checkItemId(Long id);
}
