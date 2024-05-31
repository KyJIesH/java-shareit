package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class ItemRequest {
    private Long id;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 15, max = 500, message = "Длина описания должна быть от 15 до 500 символов")
    private String description;
    @NotBlank(message = "Пользователь, который создаёт запрос должен существовать")
    private User requestor;
    @NotBlank
    @FutureOrPresent(message = "Значение должно быть настоящим временем либо будущим")
    private Date created;
}
