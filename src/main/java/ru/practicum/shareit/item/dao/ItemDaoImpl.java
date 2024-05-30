package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ItemDaoImpl implements ItemDao {

    private static final String TAG = "ITEM DAO";

    private Map<Long, Item> items = new HashMap<>();

    private long idGenerator = 1;

    @Override
    public Item create(Item item) {
        log.info("{} - Попытка добавления вещи {}", TAG, item);
        item.setId(idGenerator++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(Long id) {
        log.info("{} - Попытка получения вещи по id {}", TAG, id);
        if (id == null || id <= 0 || !items.containsKey(id)) {
            log.error("{} - некорректный id {}", TAG, id);
            throw new NotFoundException("Вещь не найдена");
        }
        return items.get(id);
    }

    @Override
    public List<Item> getAllItems() {
        log.info("{} - Попытка получения всех вещей", TAG);
        List<Item> itemList = new ArrayList<>(items.values());
        log.info("{} - Попытка получения всех вещей завершена {}", TAG, itemList);
        return itemList;
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        log.info("{} - Попытка получения всех вещей пользователя с id {}", TAG, userId);
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public Item update(Item item) {
        log.info("{} - Попытка обновления вещи {}", TAG, item);
        getItem(item.getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchByName(String text, Long userId) {
        log.info("{} - Попытка поиска вещи по названию {}", TAG, text);
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text)
                    || item.getDescription().toLowerCase().contains(text))) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void delete(Long id) {
        log.info("{} - Попытка удаления вещи по id {}", TAG, id);
        if (!items.containsKey(id)) {
            log.error("{} - по данному id {} нет вещи", TAG, id);
            throw new NotFoundException("Удаляемая вещь не найдена");
        }
        items.remove(id);
    }

    @Override
    public Item findById(Long id) {
        return null;
    }
}
