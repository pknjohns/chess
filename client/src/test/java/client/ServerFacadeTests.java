package client;

import dataaccess.DataAccessException;
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
    void clearServer() {
        try {
            server.clearService.clearDB();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the server before test", e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void doRegister() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult authData = facade.register(req);
        assertTrue(authData.authToken().length() > 10);
    }

}