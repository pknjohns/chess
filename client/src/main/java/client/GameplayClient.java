package client;

import chess.*;
import client.websocket.ServerMessageObserver;
import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameplayClient implements ServerMessageObserver {

    public GameplayClient(int port, PreLoginClient preLogClient) {
        ServerFacade facade = new ServerFacade(port, this);
        String authToken = preLogClient.authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame(params);
                case "make move" -> makeChessMove(params);
                case "highlight" -> highlightValidMoves(params);
                case "resign" -> resignGame(params);
                default -> gameplayHelp();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard() {
        return "Not implemented";
    }

    private String leaveGame(String[] params) {
        return "Not implemented";
    }

    private String makeChessMove(String[] params) {
        return "Not implemented";
    }

    private String highlightValidMoves(String[] params) {
        return "Not implemented";
    }

    private String resignGame(String[] params) {
        return "Not implemented";
    }

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
    public void notify(String message) {
        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
        switch (msg.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(message);
            case ERROR -> displayError(message);
            case LOAD_GAME -> loadGame(message);
        }
    }

    public void displayNotification(String message) {
        NotificationMessage msg = new Gson().fromJson(message, NotificationMessage.class);
        System.out.println(SET_TEXT_BOLD + SET_BG_COLOR_MAGENTA + msg.getMessage());
    }

    public void displayError(String message) {
        ErrorMessage msg = new Gson().fromJson(message, ErrorMessage.class);
        System.out.println(SET_TEXT_BOLD + SET_BG_COLOR_MAGENTA + msg.getErrorMessage());
    }

    public void loadGame(String message) {
        LoadGameMessage msg = new Gson().fromJson(message, LoadGameMessage.class);
        ChessGame game = msg.getGame();
        ChessBoard board = game.getBoard();
        System.out.println(makeWhiteBoard(board));
    }

    private String makeWhiteBoard(ChessBoard board) {
        StringBuilder sb = new StringBuilder();
        String columns = "  a     b     c    d     e    f     g     h  ";
        sb.append(SET_BG_COLOR_BLUE)
                .append("   ").append(SET_TEXT_COLOR_BLACK).append(columns).append("   ")
                .append(RESET_BG_COLOR).append("\n");

        for (int row = 0; row < 8; row++) {
            sb.append(SET_BG_COLOR_BLUE).append(" ").append(8 - row).append(" ");
            for (int col = 0; col < 8; col++) {
                boolean isLightSquare = (row + col) % 2 == 0;
                String bgColor = isLightSquare ? SET_BG_COLOR_BROWN : SET_BG_COLOR_DARK_BROWN;

                ChessPosition pstn = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pstn);
                String pieceStr = convertPieceToSymbol(piece);
                String textColor = (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                        ? SET_TEXT_COLOR_WHITE
                        : SET_TEXT_COLOR_BLACK;

                sb.append(bgColor).append(textColor).append(" ").append(pieceStr).append(" ").append(RESET_TEXT_COLOR);
            }
            sb.append(SET_BG_COLOR_BLUE).append(" ").append(SET_TEXT_COLOR_BLACK).append(8 - row).append(" ");
            sb.append(RESET_BG_COLOR).append(" \n");
        }

        sb.append(SET_BG_COLOR_BLUE)
                .append("   ").append(SET_TEXT_COLOR_BLACK).append(columns).append("   ")
                .append(RESET_BG_COLOR);

        return sb.toString();
    }

    private String makeBlackBoard(ChessBoard board) {
        StringBuilder sb = new StringBuilder();
        String columns = "  h     g     f    e     d    c     b     a  ";
        sb.append(SET_BG_COLOR_BLUE)
                .append("   ").append(SET_TEXT_COLOR_BLACK).append(columns).append("   ")
                .append(RESET_BG_COLOR).append("\n");

        for (int row = 7; row >= 0; row--) {
            sb.append(SET_BG_COLOR_BLUE).append(" ").append(8 - row).append(" ");
            for (int col = 7; col >= 0; col--) {
                boolean isLightSquare = (row + col) % 2 == 0;
                String bgColor = isLightSquare ? SET_BG_COLOR_BROWN : SET_BG_COLOR_DARK_BROWN;

                ChessPosition pstn = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pstn);
                String pieceStr = convertPieceToSymbol(piece);
                String textColor = (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                        ? SET_TEXT_COLOR_WHITE
                        : SET_TEXT_COLOR_BLACK;

                sb.append(bgColor).append(textColor).append(" ").append(pieceStr).append(" ").append(RESET_TEXT_COLOR);
            }
            sb.append(SET_BG_COLOR_BLUE).append(" ").append(SET_TEXT_COLOR_BLACK).append(8 - row).append(" ");
            sb.append(RESET_BG_COLOR).append(" \n");
        }

        sb.append(SET_BG_COLOR_BLUE)
                .append("   ").append(SET_TEXT_COLOR_BLACK).append(columns).append("   ")
                .append(RESET_BG_COLOR);

        return sb.toString();
    }

    private String convertPieceToSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        } else {
            return switch (piece.getPieceType()) {
                case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
                case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
                case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
                case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
                case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
                case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
            };
        }
    }
}
