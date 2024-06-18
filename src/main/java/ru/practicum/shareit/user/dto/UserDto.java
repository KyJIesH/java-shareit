package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Size(min = 1, max = 30, message = "Длина имени должна быть от 5 до 30 символов")
    private String name;
    @Email(message = "Некорректный email")
    private String email;
}
