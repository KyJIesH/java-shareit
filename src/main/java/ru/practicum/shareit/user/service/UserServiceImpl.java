package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String TAG = "USER SERVICE";
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("{} - Обработка запроса на добавление пользователя", TAG);
        User user = userMapper.toUser(userDto);
        if (user.getName() == null || user.getName().isEmpty()) {
            return null;
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || findUserByEmail(user.getEmail()) == 1) {
            return null;
        }
        return userMapper.toUserDto(userDao.create(user));
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("{} - Обработка запроса на получение пользователя по id {}", TAG, id);
        return userMapper.toUserDto(userDao.getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("{} - Обработка запроса на получение всех пользователей", TAG);
        return userMapper.toUsersDto(userDao.getAllUsers());
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("{} - Обработка запроса на обновление пользователя {}", TAG, userDto);
        User user = userMapper.toUser(userDto);
        User updatedUser = userDao.getUser(id);
        if (user.getName() != null && !user.getName().isBlank() && !user.getName().equals(updatedUser.getName())) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().equals(updatedUser.getEmail())) {
            updatedUser.setEmail(user.getEmail());
        }
        return userMapper.toUserDto(userDao.update(updatedUser, id));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("{} - Обработка запроса на удаление пользователя по id {}", TAG, id);
        userDao.delete(id);
    }

    @Override
    public int findUserByEmail(String email) {
        for (User exp : userDao.getAllUsers()) {
            if (exp.getEmail().equals(email)) {
                return 1;
            }
        }
        return 0;
    }
}
