package service;

import dataaccess.*;

import model.*;
import org.mindrot.jbcrypt.BCrypt;
import server.BadRequestException;

import java.util.UUID;

public class UserService {

    private final AuthDAO authDB;
    private final UserDAO userDB;

    public UserService(AuthDAO authDB, UserDAO userDB) {
        this.authDB = authDB;
        this.userDB = userDB;
    }

    public RegisterResult registerUser(RegisterRequest request) throws AlreadyTakenException, BadRequestException, DataAccessException {
        String username = request.username();
        String password = request.password();
        String email = request.email();

        if (username == null) {
            throw new BadRequestException("Missing username");
        } else if (password == null) {
            throw new BadRequestException("Missing password");
        } else if (email == null) {
            throw new BadRequestException("Missing email");
        }

        if (userDB.getUser(username) == null) { //username is available
            String safePassword = encryptPassword(password);
            UserData user = new UserData(username, safePassword, email);
            String token = generateToken();
            AuthData auth = new AuthData(token, username);
            userDB.addUser(user);
            authDB.addAuth(auth);
            return new RegisterResult(token, username);
        } else {
            throw new AlreadyTakenException("Username is already in use");
        }
    }

    public LoginResult loginUser(LoginRequest request) throws UnauthorizedException, DataAccessException {
        String username = request.username();
        String password = request.password();

        UserData user = userDB.getUser(username);
        //String safePassword = encryptPassword(password);
        if (user == null) {
            throw new UnauthorizedException("Unregistered username");
        } else if (!BCrypt.checkpw(password, user.password())) {//Objects.equals(user.password(), safePassword)) {
            throw new UnauthorizedException("Incorrect password");
        } else {
            String authToken = generateToken();
            AuthData auth = new AuthData(authToken, username);
            authDB.addAuth(auth);
            return new LoginResult(authToken, username);
        }
    }

    public void logoutUser(String authToken) throws UnauthorizedException, DataAccessException{
        AuthData auth = authDB.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Bad token");
        } else {
            authDB.deleteAuth(authToken);
        }
    }

    public UserData findUsername(String username) throws DataAccessException {
        return userDB.getUser(username);
    }

    public void createUser(UserData user) throws DataAccessException {
        userDB.addUser(user);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(generateToken(), username);
        authDB.addAuth(auth);
    }

    String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

}
