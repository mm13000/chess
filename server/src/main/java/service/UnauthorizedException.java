package service;

/**
 * Indicates there was an error in a user getting authorized (invalid Auth token, incorrect password, nonexistent user)
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}
