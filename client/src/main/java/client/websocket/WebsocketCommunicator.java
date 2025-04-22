package client.websocket;

import com.google.gson.Gson;
import facade.ResponseException;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    Session session;

    public WebsocketCommunicator(int port, ServerMessageObserver observer) throws ResponseException {
        try {
            String wsServerUrl = "ws://localhost:" + port + "/ws";
            URI wsURI = new URI(wsServerUrl);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, wsURI);

            //onMessage
            this.session.addMessageHandler((MessageHandler.Whole<String>) observer::notify);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // need connect, make move, leave, and resign functions (and maybe get board function) for websocket communicator
    // need a "listener" that calls observer.notify if server sends any messages

    public void connect(String authToken, int gameID) {
        try {
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
