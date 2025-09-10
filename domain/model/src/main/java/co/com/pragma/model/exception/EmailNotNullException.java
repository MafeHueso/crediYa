package co.com.pragma.model.exception;

public class EmailNotNullException extends RuntimeException {
    public EmailNotNullException(String message) {
        super(message);
    }
}
