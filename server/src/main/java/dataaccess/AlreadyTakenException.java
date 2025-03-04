package dataaccess;

/**
 * indicates the info they are trying to claim is already being used by another user
 */

public class AlreadyTakenException extends Exception {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
