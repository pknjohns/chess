package dataaccess;

/**
 * indicates the user has made a bad request
 * meaning they have not included a required input
 */

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
