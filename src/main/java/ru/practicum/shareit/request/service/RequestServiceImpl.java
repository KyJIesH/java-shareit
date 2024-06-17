package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final String TAG = "REQUEST SERVICE";
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto) {
        log.info("{} - Обработка запроса на добавление потребности {}", TAG, itemRequestDto);
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequest(Long id) {
        log.info("{} - Обработка запроса на получение потребности по id {}", TAG, id);
        ItemRequest itemRequest = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Потребность не найдена"));
        return requestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        log.info("{} - Обработка запроса на получение всех потребностей", TAG);
        return requestMapper.toItemRequestsDto(requestRepository.findAll());
    }

    @Override
    public ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto, Long id) {
        log.info("{} - Обработка запроса на обновление потребности {}", TAG, itemRequestDto);
        ItemRequest itemRequest = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Потребность не найдена"));
        if (itemRequestDto.getDescription() != null && !itemRequestDto.getDescription().isBlank()) {
            itemRequest.setDescription(itemRequestDto.getDescription());
        }
        if (itemRequestDto.getRequestor() != null) {
            itemRequest.setRequestor(itemRequestDto.getRequestor());
        }
        itemRequest.setId(id);
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public void deleteItemRequest(Long id) {
        log.info("{} - Обработка запроса на удаление потребности по id {}", TAG, id);
        requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Потребность не найдена"));
        requestRepository.deleteById(id);
    }
}
