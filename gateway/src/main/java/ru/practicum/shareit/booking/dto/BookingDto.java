package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.validation.ValidationCreate;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Valid
public class BookingDto {
    private Long id;
    @NotNull(groups = {ValidationCreate.class}, message = "Не может быть пустым")
    private Long itemId;
    private UserResponseDto booker;
    @FutureOrPresent(groups = {ValidationCreate.class}, message = "Начало бронирования не может быть в прошлом")
    private LocalDateTime start;
    @FutureOrPresent(groups = {ValidationCreate.class}, message = "Окончание бронирования должно быть в будущем")
    private LocalDateTime end;
    private StatusBooking status;
}
