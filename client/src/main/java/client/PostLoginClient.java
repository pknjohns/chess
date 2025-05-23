package client;

import model.*;
import facade.*;

import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class PostLoginClient {

    private final ServerFacade facade;
    private final String authToken;
    private final PreLoginClient preLogClient;
    public final GameplayClient gameplayClient;
    private HashMap<Integer, ListGameData> gameMap;

    public PostLoginClient(int port, PreLoginClient preLogClient) {
        gameplayClient = new GameplayClient(port, preLogClient);
        facade = new ServerFacade(port, gameplayClient);
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
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout(params);
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

    private String listGames(String... params) throws ResponseException {
        if (params.length < 1) {
            try {
                ListGameResult gameList = facade.listGames(authToken);
                updateClientGameList(gameList); //updates client's gameMap based on gameList returned from server
                return makeGameListString(gameMap);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "Please provide a valid command\n" + postLogHelp();
        }
    }

    private void updateClientGameList(ListGameResult lgr) {
        gameMap = new HashMap<>();
        int i = 1;
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

    private String joinGame(String... params) throws BadRequestException {
        if (params.length != 2) {
            return "Please provide a valid game ID and team color to join a game\n";
        }

        int clientGameId;
        try {
            clientGameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Error: Invalid game ID\n");
        }

        if (!gameMap.containsKey(clientGameId)) {
            throw new BadRequestException("Error: Invalid game ID\n");
        }

        String teamColor = params[1].toUpperCase();
        String gameName = gameMap.get(clientGameId).gameName();
        int serverGameId = gameMap.get(clientGameId).gameID();
        JoinRequest jReq = new JoinRequest(teamColor, serverGameId);

        try {
            facade.joinGame(authToken, jReq); //HTTP API
            facade.connect(authToken, serverGameId); //open ws connection and send connect wsMsg to server
            preLogClient.gameID = clientGameId;
            preLogClient.state = State.GAMEPLAY; //transition to gameplayUI
            return String.format("You joined game '%s' as the %s player\n%s\n", gameName, teamColor, makeBoard(teamColor));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String observeGame(String... params) throws BadRequestException {
        if (params.length != 1) {
            return "Please provide a valid game ID and team color to join a game\n";
        }

        int clientGameId;
        try {
            clientGameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Error: Invalid game ID\n");
        }

        if (!gameMap.containsKey(clientGameId)) {
            throw new BadRequestException("Error: Invalid game ID\n");
        }

        try {
            String gameName = gameMap.get(clientGameId).gameName();
            preLogClient.gameID = clientGameId;
            preLogClient.state = State.GAMEPLAY;
            return String.format("You are now observing '%s' \n%s\n", gameName, makeBoard("WHITE"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String logout(String... params) {
        if (params.length < 1) {
            try {
                facade.logout(authToken);
                preLogClient.state = State.SIGNEDOUT;
                return "You successfully signed out";
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "Please provide a valid command\n" + postLogHelp();
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

    private static final String[][] STARTING_BOARD = {
            {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
            {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
    };

    private String makeBoard(String teamColor) {
        boolean isWhite = teamColor.equalsIgnoreCase("WHITE");
        return makeBoardView(isWhite);
    }

    private String makeBoardView(boolean isWhitePerspective) {
        StringBuilder sb = new StringBuilder();

        // Column labels
        String[] cols = {"a", " b", " c", "d", " e", " f", "g", " h"};
        if (!isWhitePerspective) {
            Collections.reverse(Arrays.asList(cols));
        }
        String colHeader = String.join("    ", cols);

        // Row labels
        String[] rowLabels = {"8", "7", "6", "5", "4", "3", "2", "1"};
        if (!isWhitePerspective) {
            Collections.reverse(Arrays.asList(rowLabels));
        }

        // Draw top column header
        sb.append(SET_BG_COLOR_BLUE)
                .append("   ")
                .append(SET_TEXT_COLOR_BLACK)
                .append("  ").append(colHeader).append("     ")
                .append(RESET_BG_COLOR).append("\n");

        for (int i = 0; i < 8; i++) {
            int rowIndex = isWhitePerspective ? i : 7 - i;
            String label = rowLabels[i];

            // Left label
            sb.append(SET_BG_COLOR_BLUE).append(" ").append(label).append(" ");

            for (int j = 0; j < 8; j++) {
                int colIndex = isWhitePerspective ? j : 7 - j;

                boolean isLightSquare = (rowIndex + colIndex) % 2 == 0;
                String bgColor = isLightSquare ? SET_BG_COLOR_BROWN : SET_BG_COLOR_DARK_BROWN;

                String piece = STARTING_BOARD[rowIndex][colIndex];
                String textColor = rowIndex > 2 ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;

                sb.append(bgColor).append(textColor).append(" ").append(piece).append(" ").append(RESET_TEXT_COLOR);
            }

            // Right label
            sb.append(SET_BG_COLOR_BLUE).append(" ").append(SET_TEXT_COLOR_BLACK).append(label).append(" ");
            sb.append(RESET_BG_COLOR).append(" \n");
        }

        // Bottom column header
        sb.append(SET_BG_COLOR_BLUE)
                .append("   ")
                .append(SET_TEXT_COLOR_BLACK)
                .append("  ").append(colHeader).append("     ")
                .append(RESET_BG_COLOR);

        return sb.toString();
    }
}
