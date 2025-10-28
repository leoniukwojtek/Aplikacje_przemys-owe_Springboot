package com.techcorp.employee.exception;

/**
 * Wyjątek reprezentujący błąd komunikacji z zewnętrznym API,
 * np. błąd HTTP, brak odpowiedzi, niepoprawny format danych (np. JSON),
 * lub inne problemy związane z integracją z usługą zewnętrzną.
 *
 * Klasa rozszerza klasę Exception, dzięki czemu może być używana
 * w mechanizmie obsługi wyjątków (try-catch) jako wyjątek kontrolowany.
 */
public class ApiException extends Exception {

    /**
     * Konstruktor przyjmujący tylko komunikat błędu.
     *
     * @param message Komunikat opisujący błąd, np. "Nie udało się połączyć z API"
     */
    public ApiException(String message) {
        super(message); // Przekazanie komunikatu do klasy bazowej Exception
    }

    /**
     * Konstruktor przyjmujący komunikat błędu oraz przyczynę (cause).
     *
     * @param message Komunikat opisujący błąd
     * @param cause   Obiekt Throwable reprezentujący pierwotną przyczynę błędu,
     *                np. IOException, JsonParseException itp.
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause); // Przekazanie komunikatu i przyczyny do klasy bazowej Exception
    }
}
