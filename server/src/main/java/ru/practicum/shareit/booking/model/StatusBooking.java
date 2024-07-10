package ru.practicum.shareit.booking.model;

public enum StatusBooking {
    //Неизвестное значение
    UNDEFINED,
    //Новое бронирование, ожидает одобрения
    WAITING,
    //Бронирование подтверждено владельцем
    APPROVED,
    //Бронирование отклонено владельцем
    REJECTED,
    //Бронирование отменено создателем
    CANCELED
}
