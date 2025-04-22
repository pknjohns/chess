package client.websocket;

import com.google.gson.Gson;
import facade.ResponseException;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    Session session;
    private final int port;
    private ServerMessageObserver observer;

    public WebsocketCommunicator(int port, ServerMessageObserver observer) throws ResponseException {
        this.port = port;
        this.observer = observer;
        try {
            String wsServerUrl = "ws://localhost:" + port + "/ws";
            URI wsURI = new URI(wsServerUrl);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, wsURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
                observer.notify(msg);
            });
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // need connect, make move, leave, and resign functions (and maybe get board function) for websocket communicator
    // need a "listener" that calls observer.notify if server sends any messages
}
