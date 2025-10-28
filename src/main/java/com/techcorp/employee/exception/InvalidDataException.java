package com.techcorp.employee.exception;

/**
 * Wyjątek reprezentujący błąd walidacji danych wejściowych,
 * np. niepoprawne stanowisko, brak wymaganych pól, błędny format wynagrodzenia
 * lub inne niezgodności w danych importowanych z zewnętrznych źródeł (np. pliku CSV).
 *
 * Klasa rozszerza klasę Exception, co oznacza, że jest wyjątkiem kontrolowanym (checked exception),
 * i musi być jawnie obsłużona lub zadeklarowana w sygnaturze metody.
 */
public class InvalidDataException extends RuntimeException {

    /**
     * Konstruktor przyjmujący tylko komunikat błędu.
     *
     * @param message Komunikat opisujący przyczynę błędu walidacji,
     *                np. "Nieznane stanowisko: CEO"
     */
    public InvalidDataException(String message) {
        super(message); // Przekazanie komunikatu do klasy bazowej Exception
    }

    /**
     * Konstruktor przyjmujący komunikat błędu oraz przyczynę (cause).
     *
     * @param message Komunikat opisujący błąd walidacji
     * @param cause   Obiekt Throwable reprezentujący pierwotną przyczynę błędu,
     *                np. NumberFormatException przy parsowaniu wynagrodzenia
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause); // Przekazanie komunikatu i przyczyny do klasy bazowej Exception
    }
}
