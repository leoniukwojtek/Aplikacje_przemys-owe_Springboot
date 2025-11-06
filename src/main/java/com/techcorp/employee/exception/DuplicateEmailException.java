package com.techcorp.employee.exception;

public class DuplicateEmailException extends RuntimeException {

    // Konstruktor z komunikatem
    public DuplicateEmailException(String message) {
        super(message);
    }

    // Konstruktor z komunikatem i przyczyną
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    // Konstruktor z przyczyną
    public DuplicateEmailException(Throwable cause) {
        super(cause);
    }

    // Opcjonalnie: konstruktor domyślny
    public DuplicateEmailException() {
        super("Email already exists.");
    }
}
