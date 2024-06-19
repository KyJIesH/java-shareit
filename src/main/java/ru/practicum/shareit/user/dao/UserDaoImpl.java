package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Slf4j
@Component
public class UserDaoImpl implements UserDao {

    private static final String TAG = "USER DAO";

    private Map<Long, User> users = new HashMap<>();

    private long idGenerator = 1;

    @Override
    public User create(User user) {
        log.info("{} - Попытка создания пользователя {}", TAG, user);
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        log.info("{} - Попытка получения пользователя по id {}", TAG, id);
        if (!users.containsKey(id)) {
            log.error("{} - Пользоваетель с id {} не найден", TAG, id);
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("{} - Попытка получения списка всех пользователей", TAG);
        List<User> userList = new ArrayList<>(users.values());
        log.info("{} - Попытка получения всех пользователей завершена {}", TAG, userList);
        return userList;
    }

    @Override
    public User update(User user, Long id) {
        log.info("{} - Попытка обновления пользователя {}", TAG, user);
        getUser(id);
        users.put(id, user);
        return user;
    }

    @Override
    public void delete(Long id) {
        log.info("{} - Попытка удаления пользователя по id {}", TAG, id);
        if (!users.containsKey(id)) {
            log.error("{} - По данному id {} нет пользователя", TAG, id);
            throw new NotFoundException("Удаляемый пользователь не найден");
        }
        users.remove(id);
    }
}
