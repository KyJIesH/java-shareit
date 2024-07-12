package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final String TAG = "GATEWAY ERROR HANDLER";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleExceptionValidation(ValidationException e) {
        log.error("400 {} {}", TAG, e.getMessage(), e);
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleExceptionNotFound(NotFoundException e) {
        log.error("400 {} {}", TAG, e.getMessage(), e);
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleExceptionConstraintViolation(ConstraintViolationException e) {
        log.error("400 {} {}", TAG, e.getMessage(), e);
        return new ErrorMessage(e.getMessage());
    }
}
