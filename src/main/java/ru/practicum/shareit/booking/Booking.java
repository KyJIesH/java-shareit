package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Data
public class Booking {
    private Long id;
    @NotNull
    @FutureOrPresent(message = "Значение должно быть настоящим временем либо будущим")
    private Date start;
    @NotNull
    @PastOrPresent(message = "Значение должно быть настоящим временем либо прошедшим")
    private Date end;
    @NotBlank(message = "Вещь, которую пользователь бронирует должна быть")
    private Item item;
    @NotBlank(message = "Пользователь, который бронирует вещь должен существовать")
    private User booker;
    @NotBlank(message = "Статус не может быть не заполнен")
    private StatusBooking status;
}
