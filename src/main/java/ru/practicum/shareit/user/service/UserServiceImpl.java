package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String TAG = "USER SERVICE";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("{} - Обработка запроса на добавление пользователя {}", TAG, userDto);
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("{} - Обработка запроса на получение пользователя по id {}", TAG, id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("{} - Обработка запроса на получение всех пользователей", TAG);
        return userMapper.toUsersDto(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("{} - Обработка запроса на обновление пользователя {}", TAG, userDto);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        user.setId(id);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("{} - Обработка запроса на удаление пользователя по id {}", TAG, id);
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.deleteById(id);
    }

    @Override
    public int findUserByEmail(String email) {
        for (User exp : userRepository.findAll()) {
            if (exp.getEmail().equals(email)) {
                return 1;
            }
        }
        return 0;
    }
}