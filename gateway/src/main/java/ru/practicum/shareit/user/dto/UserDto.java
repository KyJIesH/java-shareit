package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.ValidationCreate;
import ru.practicum.shareit.validation.ValidationUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {ValidationCreate.class}, message = "Имя не может быть пустым")
    @Size(min = 1, max = 30, message = "Длина имени должна быть от 1 до 30 символов")
    private String name;
    @NotBlank(groups = {ValidationCreate.class}, message = "Email не может быть пустым")
    @Email(groups = {ValidationCreate.class, ValidationUpdate.class}, message = "Некорректный email")
    private String email;
}