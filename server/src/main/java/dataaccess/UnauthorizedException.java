package dataaccess;

/**
 * indicates username, password, or authToken are incorrect
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
