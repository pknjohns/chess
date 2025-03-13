package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class GameServiceTestUtil {

    final AuthDAO authDB;
    final GameDAO gameDB;
    final UserDAO userDB;
    final GameService gameService;
    final UserService userService;

    public GameServiceTestUtil(AuthDAO authDB, GameDAO gameDB, UserDAO userDB) {
        this.authDB = authDB;
        this.gameDB = gameDB;
        this.userDB = userDB;
        this.gameService = new GameService(authDB, gameDB);
        this.userService = new UserService(authDB, userDB);
    }
}
