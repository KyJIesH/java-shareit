package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
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

    /*//Map<itemID, Map<userId, List<review>>>
    private Map<Long, Map<Long, List<String>>> reviews;*/

    public Item search(Item item) {
        /*Для поиска вещей должен быть организован поиск. Чтобы воспользоваться нужной вещью, её требуется забронировать.
        Бронирование, или Booking — ещё одна важная сущность приложения. Бронируется вещь всегда на определённые даты.
        Владелец вещи обязательно должен подтвердить бронирование.*/

        /*После того как вещь возвращена, у пользователя, который её арендовал, должна быть возможность оставить отзыв.
        В отзыве можно поблагодарить владельца вещи и подтвердить, что задача выполнена —
        дрель успешно справилась с бетоном, и картины повешены.*/
        return null;
    }
}
