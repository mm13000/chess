package service;

/**
 * Indicates the name (such as a username or game name) has already been used
 */
public class NameTakenException extends Exception {
    public NameTakenException(String message) {
        super(message);
    }
}
