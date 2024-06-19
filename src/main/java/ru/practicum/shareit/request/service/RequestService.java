package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto getItemRequest(Long id);

    List<ItemRequestDto> getAllItemRequests();

    ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto, Long id);

    void deleteItemRequest(Long id);
}
