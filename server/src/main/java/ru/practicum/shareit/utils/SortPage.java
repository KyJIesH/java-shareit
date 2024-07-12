package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortPage {

    public PageRequest sortPageRequest(int from, int size) {
        return PageRequest.of(from, size, Sort.by("created").descending());
    }
}
