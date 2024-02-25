package service;

/**
 * Indicates there was an error connecting to the database
 */
public class NameTakenException extends Exception {
    public NameTakenException(String message) {
        super(message);
    }
}
