package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.ValidationCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {ValidationCreate.class}, message = "Название не может быть пустым")
    @Size(min = 1, max = 30, message = "Длина названия должна быть от 1 до 30 символов")
    private String name;
    @NotBlank(groups = {ValidationCreate.class}, message = "Описание не может быть пустым")
    @Size(min = 5, max = 2000, message = "Длина описания должна быть от 5 до 2000 символов")
    private String description;
    @NotNull(groups = {ValidationCreate.class}, message = "Статус не может быть пустым")
    private Boolean available;
    private Long requestId;
}
