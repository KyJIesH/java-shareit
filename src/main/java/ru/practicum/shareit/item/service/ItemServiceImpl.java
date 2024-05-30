package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String TAG = "ITEM SERVICE";

    private final ItemDao itemDao;
    private final UserDao userDao;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("{} - Обработка запроса на добавление вещи", TAG);
        Item item = itemMapper.toItem(itemDto);
        User owner = userDao.getUser(userId);
        item.setOwner(owner);
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            return null;
        }
        return itemMapper.toItemDto(itemDao.create(item));
    }

    @Override
    public ItemDto getItem(Long id) {
        log.info("{} - Обработка запроса на получение вещи по id {}", TAG, id);
        return itemMapper.toItemDto(itemDao.getItem(id));
    }

    @Override
    public List<ItemDto> getAllItems() {
        log.info("{} - Обработка запроса на получение всех вещей", TAG);
        return itemMapper.toItemsDto(itemDao.getAllItems());
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long id) {
        log.info("{} - Обработка запроса вещи по id пользователя {}", TAG, id);
        return itemMapper.toItemsDto(itemDao.getAllItemsByUserId(id));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        log.info("{} - Обработка запроса на обновление вещи {}", TAG, itemDto);
        Item item = itemMapper.toItem(itemDto);
        Item temp = itemDao.getItem(id);
        User owner = userDao.getUser(userId);
        if (item.getId() == null) {
            item.setId(temp.getId());
        }
        if (item.getName() == null || item.getName().isBlank()) {
            item.setName(temp.getName());
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            item.setDescription(temp.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(temp.getAvailable());
        }
        if (temp.getOwner().getId().equals(owner.getId())) {
            item.setOwner(owner);
            return itemMapper.toItemDto(itemDao.update(item));
        }
        throw new NotFoundException("Пользователь вещи не найден");
    }

    @Override
    public List<ItemDto> searchByName(String text, Long userId) {
        log.info("{} - Обработка запроса на поиск вещи по названию {}", TAG, text);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemMapper.toItemsDto(itemDao.searchByName(text.toLowerCase(), userId));
    }

    @Override
    public void delete(Long id) {
        log.info("{} - Обработка запроса на удаление вещи по id {}", TAG, id);
        itemDao.delete(id);
    }

    @Override
    public void checkItemId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Некорректный формат id фильма");
        }
    }
}
