package client;

import model.*;
import server.BadRequestException;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;

public class PostLoginClient {

    private final ServerFacade facade;
    private final String authToken;
    private final PreLoginClient preLogClient;
    private HashMap<Integer, ListGameData> gameMap;

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
                case "list" -> listGames();
                case "join" -> joinGame(params);
                //case "observe" -> observe(params);
                case "logout" -> logout();
                default -> postLogHelp();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String createGame(String... params) {
        if (params.length == 1) {
            try {
                CreateRequest cReq = new CreateRequest(params[0]);
                facade.createGame(authToken, cReq);
                return String.format("You created a new game: %s \n", params[0]);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "Please provide a game name to create a new game\n";
        }
    }

    private String listGames() throws ResponseException {
        try {
            ListGameResult gameList = facade.listGames(authToken);
            updateClientGameList(gameList);
            return makeGameListString(gameMap);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateClientGameList(ListGameResult lgr) {
        gameMap = new HashMap<>();
        int i = 0;
        for (ListGameData game : lgr.games()) {
            gameMap.put(i, game);
            i++;
        }
    }

    private String makeGameListString(HashMap<Integer, ListGameData> gameMap) {
        StringBuilder sbGameList = new StringBuilder("Current Games:");
        sbGameList.append('\n');

        for (int key : gameMap.keySet()) {
            ListGameData game = gameMap.get(key);
            sbGameList.append(key);
            sbGameList.append(" - ");
            sbGameList.append(game.gameName());
            sbGameList.append(" [WHITE|");
            if (game.whiteUsername() != null) {
                sbGameList.append(game.whiteUsername());
            } else {
                sbGameList.append(" - ");
            }

            sbGameList.append("] [BLACK|");

            if (game.blackUsername() != null) {
                sbGameList.append(game.blackUsername());
            } else {
                sbGameList.append(" - ");
            }

            sbGameList.append("]");
            sbGameList.append('\n');
        }
        return sbGameList.toString();
    }

    private String joinGame(String... params) throws ResponseException, BadRequestException {
        if (params.length == 2) {
            int clientGameId = Integer.parseInt(params[0]);
            if (gameMap.containsKey(clientGameId)) {
                try {
                    String teamColor = params[1].toUpperCase();
                    String gameName = gameMap.get(clientGameId).gameName();
                    int serverGameId = gameMap.get(clientGameId).gameID();
                    JoinRequest jReq = new JoinRequest(teamColor, serverGameId);
                    facade.joinGame(authToken, jReq);
                    preLogClient.state = State.GAMEPLAY;
                    return String.format("You joined game '%s' as the %s player\n", gameName, params[1]);
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new BadRequestException("Error: Invalid game ID\n");
            }
        } else {
            return "Please provide a valid game ID and team color to join a game\n";
        }
    }

    private String logout() {
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
