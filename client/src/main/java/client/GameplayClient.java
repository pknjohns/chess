package client;

import client.websocket.ServerMessageObserver;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class GameplayClient implements ServerMessageObserver {

    private final ServerFacade facade;
    private final String authToken;
    private final PreLoginClient preLogClient;

    public GameplayClient(int port, PreLoginClient preLogClient) {
        facade = new ServerFacade(port, this);
        this.preLogClient = preLogClient;
        this.authToken = preLogClient.authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                //case "redraw" -> redrawBoard();
                //case "leave" -> leaveGame(params);
                //case "make move" -> makeChessMove(params);
                //case "highlight" -> highlightValidMoves(params);
                //case "resign" -> resignGame(params);
                default -> gameplayHelp();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

//    public String redrawBoard() {
//
//    }

    private String gameplayHelp() {
        return """
            - redraw: redraw the chessboard
            - leave: stop playing or observing a game
            - make move <PIECE> <START> <END> (PLAYERS ONLY): input the piece you want to move, its starting position, and its desired end position
            - highlight <PIECE>: input piece to highlight its valid moves on the board
            - resign (PLAYERS ONLY): forfeit and end the game
            - help: get help on what commands you can run
            """;
    }

    @Override
    public void notify(ServerMessage msg) {

    }
}
