package service;

/**
 * Indicates the name (such as a username or game name) has already been used
 */
public class TakenException extends Exception {
    public TakenException(String message) {
        super(message);
    }
}
