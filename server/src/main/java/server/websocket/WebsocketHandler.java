package server.websocket;

import chess.ChessGame;
import model.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import service.GameService;
import service.UserService;
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
                case MAKE_MOVE -> makeMove(session, username, cmd);
                case LEAVE -> leaveGame(session, username, cmd);
                case RESIGN -> resign(session, username, cmd);
            }
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
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

    private void makeMove(Session session, String username, UserGameCommand cmd) {
    }

    private void leaveGame(Session session, String username, UserGameCommand cmd) {
    }

    private void resign(Session session, String username, UserGameCommand cmd) {
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
