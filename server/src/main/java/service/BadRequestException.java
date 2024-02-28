package service;

/**
 * Indicates there was an error in the nature of the request (a required field missing or incorrectly formatted)
 */
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
