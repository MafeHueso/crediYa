package co.com.pragma.api.error;

public class InvalidRolRequestException extends RuntimeException {
    public InvalidRolRequestException(String message) {
        super(message);
    }
}
