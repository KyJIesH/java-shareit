package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final String TAG = "REQUEST SERVICE";
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("{} - Обработка запроса на добавление потребности {} пользователем {}", TAG, itemRequestDto, userId);
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        User user = checkUser(userId);
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequestsUserSorted(Long userId, PageRequest pageRequest) {
        log.info("{} - Обработка запроса на получение всех потребностей пользователя {}", TAG, userId);
        checkUser(userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterId(userId, pageRequest);
        return requestMapper.toItemRequestsDto(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsSorted(Long userId, PageRequest pageRequest) {
        log.info("{} - Обработка запроса пользователя {} на получение всех потребностей остальных пользователей", TAG, userId);
        checkUser(userId);
        List<ItemRequest> itemRequests = requestRepository.findAll(userId, pageRequest);
        return requestMapper.toItemRequestsDto(itemRequests);
    }

    @Override
    public ItemRequestDto getItemRequest(Long requestId, Long userId) {
        log.info("{} - Обработка запроса на получение пользователем {} потребности по id {}", TAG, userId, requestId);
        checkUser(userId);
        ItemRequest itemRequest = checkItemRequest(requestId);
        return requestMapper.toItemRequestDto(itemRequest);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private ItemRequest checkItemRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Потребность не найдена"));
    }
}
