package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.validation.ValidationCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotNull(groups = {ValidationCreate.class}, message = "Описание не может быть null")
    @NotBlank(groups = {ValidationCreate.class}, message = "Описание не может быть пустым")
    private String description;
    private UserResponseDto requester;
    private LocalDateTime created;
}
