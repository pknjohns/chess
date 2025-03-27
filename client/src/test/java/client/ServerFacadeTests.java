package client;

import server.ResponseException;
import server.Server;
import server.ServerFacade;
import model.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearServer() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        facade.clear();
        server.stop();
    }

    @Test
    void doClear() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(req);
        assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    void doRegister() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult authData = facade.register(req);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void noRegisterAlreadyTaken() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(req);

        RegisterRequest req1 = new RegisterRequest("player1", "password", "p1@email.com");
        assertThrows(ResponseException.class, () -> facade.register(req1));
    }

    @Test
    void noRegisterNoUsername() {
        RegisterRequest req = new RegisterRequest(null, "password", "p1@email.com");
        assertThrows(ResponseException.class, () -> facade.register(req));
    }

    @Test
    void noRegisterNoPassword() {
        RegisterRequest req = new RegisterRequest("player1", null, "p1@email.com");
        assertThrows(ResponseException.class, () -> facade.register(req));
    }

    @Test
    void noRegisterNoEmail() {
        RegisterRequest req = new RegisterRequest("player1", "password", null);
        assertThrows(ResponseException.class, () -> facade.register(req));
    }

    @Test
    void doLogout() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult authData = facade.register(req);
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void noLogout() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(req);
        assertThrows(ResponseException.class, () -> facade.logout("1234"));
    }

    @Test
    void doLogin() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult authData = facade.register(req);
        facade.logout(authData.authToken());

        LoginRequest lReq = new LoginRequest("player1", "password");
        assertDoesNotThrow(() -> facade.login(lReq));
    }

    @Test
    void noLogoutBadUsername() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult authData = facade.register(req);
        facade.logout(authData.authToken());

        LoginRequest lReq = new LoginRequest("player2", "password");
        assertThrows(ResponseException.class, () -> facade.login(lReq));
    }

    @Test
    void noLogoutBadPassword() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult authData = facade.register(req);
        facade.logout(authData.authToken());

        LoginRequest lReq = new LoginRequest("player1", "passwor");
        assertThrows(ResponseException.class, () -> facade.login(lReq));
    }
}