package com.techcorp.employee.exception;

import com.techcorp.employee.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==============================
    // 👥 Wyjątki dotyczące pracowników
    // ==============================

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidDataException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ==============================
    // 🗂️ Wyjątki dotyczące obsługi plików
    // ==============================

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorage(FileStorageException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFile(InvalidFileException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(FileNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "Przesłany plik przekracza maksymalny rozmiar 10MB.",
                LocalDateTime.now(),
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    // ==============================
    // ⚠️ Ogólna obsługa wyjątków
    // ==============================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
