package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
}