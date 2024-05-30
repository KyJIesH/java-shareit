package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.StatusBooking;

import java.util.Date;

@Data
@AllArgsConstructor
public class BookingDto {
    private Date start;
    private Date end;
    private Long itemId;
    private Long bookerId;
    private StatusBooking status;
}
