package ru.practicum.shareit.booking;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum StatusBooking {
    //Неизвестное значение
    UNDEFINED(0),
    //Новое бронирование, ожидает одобрения
    WAITING(1),
    //Бронирование подтверждено владельцем
    APPROVED(2),
    //Бронирование отклонено владельцем
    REJECTED(3),
    //Бронирование отменено создателем
    CANCELED(4);

    private int value = 0;

    StatusBooking(int value) {
        this.value = value;
    }

    public static StatusBooking valueOf(int code) {
        return Stream.of(StatusBooking.values()).filter((st) -> st.getValue() == code).findAny().orElse(UNDEFINED);
    }
}
