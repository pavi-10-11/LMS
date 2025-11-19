package com.example.LMS.advice;

import com.example.LMS.exception.LeavesException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestControllerAdvice
public class GlobalException {

    private String timestamp() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }


    @ExceptionHandler(LeavesException.class)
    public ResponseEntity<Map<String, Object>> handlerLeave(LeavesException ex, HttpServletRequest req) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", timestamp());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("path", req.getRequestURI());
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", timestamp());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("path", req.getRequestURI());
        body.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {

        List<String> messages = new ArrayList<>();

        for (FieldError f : ex.getBindingResult().getFieldErrors()) {
            messages.add(f.getField() + ": " + f.getDefaultMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", timestamp());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("path", req.getRequestURI());
        body.put("messages", messages);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex,
                                                                 HttpServletRequest req) {

        String msg = Optional.of(ex.getMostSpecificCause())
                .map(Throwable::getMessage)
                .orElse("Malformed or missing request body");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", timestamp());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("path", req.getRequestURI());
        body.put("message", msg);

        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, HttpServletRequest req) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", timestamp());
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("path", req.getRequestURI());
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
