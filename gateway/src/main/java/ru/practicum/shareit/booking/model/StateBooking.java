package ru.practicum.shareit.booking.model;

import java.util.Optional;

public enum StateBooking {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNKNOWN;

    public static Optional<StateBooking> parse(String stringState) {
        for (StateBooking state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
