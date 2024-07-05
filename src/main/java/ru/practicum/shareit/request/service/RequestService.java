package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsUserSorted(Long userId, PageRequest pageRequest);

    List<ItemRequestDto> getAllItemRequestsSorted(Long userId, PageRequest pageRequest);

    ItemRequestDto getItemRequest(Long requestId, Long userId);
}
