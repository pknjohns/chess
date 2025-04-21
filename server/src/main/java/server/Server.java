package server;

import com.google.gson.Gson;
import dataaccess.*;
import facade.BadRequestException;
import model.*;
import server.websocket.WebsocketHandler;
import spark.*;
import service.*;

import java.util.Collection;
import java.util.HashMap;

public class Server {

    private ClearService clearService;
    private GameService gameService;
    private UserService userService;
    private WebsocketHandler websocketHandler;

    public Server() {
        try {
            AuthDAO authDB = new MySqlAuthDAO();
            GameDAO gameDB = new MySqlGameDAO();
            UserDAO userDB = new MySqlUserDAO();

            clearService = new ClearService(authDB, gameDB, userDB);
            gameService = new GameService(authDB, gameDB);
            userService = new UserService(authDB, userDB);
            websocketHandler = new WebsocketHandler(userService, gameService);
        } catch(DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    public int run(int desiredPort){ //exceptions should be caught before the Server (like in the handler)

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", websocketHandler);

        Spark.delete("/db", this::clearHandler);
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);

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

    public Object loginHandler(Request req, Response res) {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        try {
            LoginResult result = userService.loginUser(request);
            res.status(200);
            return new Gson().toJson(result);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(exceptionMessageGenerator(e));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(exceptionMessageGenerator(e));
        }
    }

    public Object logoutHandler(Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            userService.logoutUser(authToken);
            res.status(200);
            return "{}";
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(exceptionMessageGenerator(e));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(exceptionMessageGenerator(e));
        }
    }

    public Object listGamesHandler(Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            Collection<ListGameData> games = gameService.listGames(authToken);
            HashMap<String, Collection<ListGameData>> resMap = new HashMap<>();
            resMap.put("games",games);
            res.status(200);
            return new Gson().toJson(resMap);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(exceptionMessageGenerator(e));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(exceptionMessageGenerator(e));
        }
    }

    public Object createGameHandler(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            CreateRequest createReq = new Gson().fromJson(req.body(), CreateRequest.class);
            int gameID = gameService.createGame(authToken, createReq.gameName());
            HashMap<String, Integer> resMap = new HashMap<>();
            resMap.put("gameID",gameID);
            res.status(200);
            return new Gson().toJson(resMap);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(exceptionMessageGenerator(e));
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(exceptionMessageGenerator(e));
        }
    }

    public Object joinGameHandler(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            JoinRequest joinReq = new Gson().fromJson(req.body(), JoinRequest.class);
            gameService.joinGame(authToken, joinReq.playerColor(), joinReq.gameID());
            res.status(200);
            return "{}";
        } catch(BadRequestException e) {
            res.status(400);
            return new Gson().toJson(exceptionMessageGenerator(e)); //make full error message and return json object
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(exceptionMessageGenerator(e));
        } catch(AlreadyTakenException e) {
            res.status(403);
            return new Gson().toJson(exceptionMessageGenerator(e)); //make full error message and return json object
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(exceptionMessageGenerator(e)); //make full error message and return json object
        }
    }

    public HashMap<String, String> exceptionMessageGenerator(Exception e) {
        HashMap<String, String> resMap = new HashMap<>();
        resMap.put("message", "Error: " + e.getMessage());
        return resMap;
    }
}