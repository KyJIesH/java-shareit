package ru.practicum.shareit.utils;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.SizeRequestException;

@Component
public class CheckPage {
    public void checkPage(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new SizeRequestException("Некорректные параметры пагинации");
        }
    }
}
