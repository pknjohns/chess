package server;

import dataaccess.*;
import spark.*;
import service.*;

public class Server {

    private final AuthDAO authDB = new MemoryAuthDAO();
    private final GameDAO gameDB = new MemoryGameDAO();
    private final UserDAO userDB = new MemoryUserDAO();
    ClearService clearService = new ClearService(authDB, gameDB, userDB);

    public int run(int desiredPort) { //exceptions should be caught before the Server (like in the handler)
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // register clear endpoint
        Spark.delete("/db", this::clearHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object clearHandler(Request req, Response res) throws DataAccessException {
        try {
            clearService.clearDB();
            res.status(200);
            return "";
        } catch (DataAccessException e) {
            res.status(500);
            System.err.println("Error: Database connection unsuccessful");
            return "";
        }
    }
}