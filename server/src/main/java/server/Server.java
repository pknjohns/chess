package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.RegisterRequest;
import model.RegisterResult;
import spark.*;
import service.*;

public class Server {

    private final AuthDAO authDB = new MemoryAuthDAO();
    private final GameDAO gameDB = new MemoryGameDAO();
    private final UserDAO userDB = new MemoryUserDAO();
    ClearService clearService = new ClearService(authDB, gameDB, userDB);
    UserService userService = new UserService(authDB, userDB);

    public int run(int desiredPort) { //exceptions should be caught before the Server (like in the handler)
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // register clear endpoint
        Spark.delete("/db", this::clearHandler);

        Spark.post("/user", this::registerHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object clearHandler(Request req, Response res) throws DataAccessException {
        clearService.clearDB();
        res.status(200);
        return "{}";
    }

    public Object registerHandler(Request req, Response res) {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResult result = userService.registerUser(request);
            res.status(200);
            return new Gson().toJson(result);
        } catch(BadRequestException e) {
            res.status(400);
            return new Gson().toJson(exceptionMessageGenerator(e)); //make full error message and return json object
        } catch(AlreadyTakenException e) {
            res.status(403);
            return new Gson().toJson(exceptionMessageGenerator(e)); //make full error message and return json object
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(exceptionMessageGenerator(e)); //make full error message and return json object
        }
    }

    public String exceptionMessageGenerator(Exception e) {
        return "Error: " + e.getMessage();
    }
}