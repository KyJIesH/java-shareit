package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utils.ErrorMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleExceptionValidation() {
        ValidationException exception = new ValidationException("validationException");
        ErrorMessage message = errorHandler.handleExceptionValidation(exception);
        assertEquals(exception.getMessage(), message.getError());
    }

    @Test
    void handleExceptionNotFound() {
        NotFoundException exception = new NotFoundException("notFoundException");
        ErrorMessage message = errorHandler.handleExceptionNotFound(exception);
        assertEquals(exception.getMessage(), message.getError());
    }
}