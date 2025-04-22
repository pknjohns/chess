package server.websocket;

import chess.*;
import dataaccess.DataAccessException;
import model.*;
import com.google.gson.Gson;
import dataaccess.UnauthorizedException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Objects;

@WebSocket
public class WebsocketHandler {

    private final UserService userService;
    private final GameService gameService;
    private final ConnectionManager connections = new ConnectionManager();

    public WebsocketHandler(UserService uService, GameService gService) {
        this.userService = uService;
        this.gameService = gService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand cmd = new Gson().fromJson(msg, UserGameCommand.class);

            // validate authToken
            String username = userService.getAuth(cmd.getAuthToken()).username();

            // make sure session is tracked in ConnectionManager
            connections.add(username, cmd.getGameID(), session);

            switch (cmd.getCommandType()) {
                case CONNECT -> connect(username, cmd);
                case MAKE_MOVE -> makeMove(session, username, msg);
                case LEAVE -> leaveGame(username, cmd);
                case RESIGN -> resign(session, username, cmd);
            }
        }  catch (UnauthorizedException ex) {
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }

    }

    private void connect(String username, UserGameCommand cmd) {

        int gameID = cmd.getGameID();
        try {
            GameData gData= gameService.getGame(gameID);

            ChessGame game = gData.game();
            LoadGameMessage lgMsg = new LoadGameMessage(game);
            connections.sendTo(username, lgMsg);

            String teamColor = getTeamColor(username, gData);
            String nMsg;
            if (teamColor == null) {
                nMsg = username + "joined as an observer";
            } else {
                nMsg = username + "joined as " + teamColor;
            }
            NotificationMessage notification = new NotificationMessage(nMsg);
            connections.broadcastExcept(gameID, username, notification);

        } catch (Exception e) {
            ErrorMessage er = new ErrorMessage("Error: " + e.getMessage());
            connections.sendTo(username, er);
        }
    }

    private void makeMove(Session session, String username, String rawMsg) {
        try {
            MakeMoveCommand cmd = new Gson().fromJson(rawMsg, MakeMoveCommand.class);

            ChessMove move = cmd.getMove();
            int gameID = cmd.getGameID();
            GameData gData= gameService.getGame(gameID);
            ChessGame game = gData.game();

            // check if move is valid
            ChessGame.TeamColor teamClr = ChessGame.TeamColor.valueOf(Objects.requireNonNull(getTeamColor(username, gData)).toUpperCase());
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece movedPiece = game.getBoard().getPiece(start);
            if (movedPiece.getTeamColor() == teamClr) {
                game.makeMove(move);
                gameService.updateGame(gameID, game);
                LoadGameMessage updateMsg = new LoadGameMessage(game);
                connections.broadcastAll(gameID, updateMsg);

                // send notification to all OTHER clients about move made
                String moveMade = "%sfrom %sto %s".formatted(movedPiece.toString(), start.toString(), end.toString());
                NotificationMessage nMsg = new NotificationMessage(username + "moved " + moveMade);
                connections.broadcastExcept(gameID, username, nMsg);

                // send notification about check, checkmate, or stalemate to ALL clients
                ChessGame.TeamColor opponent = game.getOpponentsColor(teamClr);
                if (game.isInCheck(opponent)) {
                    NotificationMessage notifMsg = new NotificationMessage(opponent + " is in check");
                    connections.broadcastAll(gameID, notifMsg);
                } else if (game.isInCheckmate(opponent)) {
                    NotificationMessage notifMsg = new NotificationMessage(opponent + " is in checkmate - GAME OVER");
                    connections.broadcastAll(gameID, notifMsg);
                } else if (game.isInStalemate(opponent)) {
                    NotificationMessage notifMsg = new NotificationMessage(opponent + " is in stalemate - GAME OVER");
                    connections.broadcastAll(gameID, notifMsg);
                }
            } else {
                //sendMessage(session.getRemote(), new ErrorMessage("Error: invalid move - can only move your own pieces"));
                connections.sendTo(username, new ErrorMessage("Error: invalid move - can only move your own pieces"));
            }

        } catch (InvalidMoveException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: invalid move - " + e.getMessage()));
        } catch (UnauthorizedException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private void leaveGame(String username, UserGameCommand cmd) {
        try {
            int gameID = cmd.getGameID();
            GameData gData = gameService.getGame(gameID);
            String white = gData.whiteUsername();
            String black = gData.blackUsername();
            if (username.equals(white)) {
                white = null;
                gameService.updateWhite(gameID, white);
            } else if (username.equals(black)) {
                black = null;
                gameService.updateBlack(gameID, black);
            }
            connections.remove(username); // unsubscrine
            NotificationMessage notification = new NotificationMessage(username + " has left the game");
            connections.broadcastExcept(gameID, username, notification);
        } catch (DataAccessException e) {
            connections.sendTo(username, new ErrorMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            connections.sendTo(username, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private void resign(Session session, String username, UserGameCommand cmd) {
    }

    private void sendMessage(org.eclipse.jetty.websocket.api.RemoteEndpoint remote, ErrorMessage msg) {
        try {
            remote.sendString(msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTeamColor(String username, GameData gData) {
        if (Objects.equals(username, gData.blackUsername())) {
            return "BLACK";
        } else if (Objects.equals(username, gData.whiteUsername())){
            return "WHITE";
        } else {
            return null;
        }
    }
}
