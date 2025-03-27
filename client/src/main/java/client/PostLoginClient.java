package client;

import model.CreateRequest;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class PostLoginClient {

    private final ServerFacade facade;
    private final String authToken;
    private final PreLoginClient preLogClient;

    public PostLoginClient(int port, PreLoginClient preLogClient) {
        facade = new ServerFacade(port);
        this.preLogClient = preLogClient;
        this.authToken = preLogClient.authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                //case "list" -> listGames(params);
                //case "join" -> joinGame(params);
                //case "observe" -> observe(params);
                case "logout" -> logout();
                default -> postLogHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                CreateRequest cReq = new CreateRequest(params[0]);
                facade.createGame(authToken, cReq);
                return String.format("You created a new game: %s", params[0]);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "Please provide a game name to create a new game";
        }
    }

    private String logout() throws ResponseException {
        try {
            facade.logout(authToken);
            preLogClient.state = State.SIGNEDOUT;
            return "You successfully signed out";
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private String postLogHelp() {
        return """
            - create <GAME NAME>: create a new chess game
            - list: list all current chess games
            - join <ID> [WHITE|BLACK]: play a chess game
            - observe <ID>: watch a chess game
            - logout: sign out of your account
            - help: get help on what commands you can run
            """;
    }
}
