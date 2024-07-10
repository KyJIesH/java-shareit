package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.utils.ErrorMessage;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final String TAG = "ERROR HANDLER";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleExceptionValidation(ValidationException e) {
        log.error("400 {} {}", TAG, e.getMessage(), e);
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleExceptionNotFound(NotFoundException e) {
        log.error("404 {} {}", TAG, e.getMessage(), e);
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleExceptionConstraintViolation(ConstraintViolationException e) {
        log.error("409 {} {}", TAG, e.getMessage(), e);
        return new ErrorMessage(e.getMessage());
    }
}
