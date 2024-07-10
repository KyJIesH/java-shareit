package ru.practicum.shareit.booking.model;

public enum StatusBooking {
    //в ожидании
    WAITING,
    //бронирование отменено юронирующим
    CANCELED,
    //бронирование отменено владельцем
    REJECTED,
    //подтверждено
    APPROVED
}
