package com.techcorp.employee.exception;

public class EmployeeNotFoundException extends RuntimeException {

    // Konstruktor z komunikatem
    public EmployeeNotFoundException(String message) {
        super(message);
    }

    // Konstruktor z komunikatem i przyczyną
    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Konstruktor z przyczyną
    public EmployeeNotFoundException(Throwable cause) {
        super(cause);
    }

    // Konstruktor domyślny z komunikatem
    public EmployeeNotFoundException() {
        super("Employee not found.");
    }
}
