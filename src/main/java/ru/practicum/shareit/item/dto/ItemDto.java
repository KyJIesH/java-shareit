package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.validation.ValidationItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название не может быть пустым", groups = {ValidationItem.Create.class})
    @Size(min = 1, max = 30, message = "Длина названия должна быть от 5 до 30 символов")
    private String name;
    @NotBlank(message = "Описание не может быть пустым", groups = {ValidationItem.Create.class})
    @Size(min = 5, max = 2000, message = "Длина описания должна быть от 15 до 2000 символов")
    private String description;
    private User owner;
    @NotNull(message = "Статус не может быть пустым", groups = {ValidationItem.Create.class})
    private Boolean available;
    private ItemRequest request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
