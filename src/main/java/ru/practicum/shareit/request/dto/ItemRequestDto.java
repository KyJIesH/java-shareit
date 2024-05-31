package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private Long requestor;
    private Date created;
}
