package server.websocket;

//import spark.Session;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, int gameID, Session session) {
        Connection connection = new Connection(username, gameID, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    // Send a message to a specific user
    public void sendTo(String username, ServerMessage msg) {
        Connection c = connections.get(username);
        if (c != null && c.session.isOpen()) {
            try {
                c.send(msg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastExcept(int currentGameID, String excludeUsername, ServerMessage msg) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<>();
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID.equals(currentGameID) && !c.username.equals(excludeUsername)) {
                    c.send(msg.toString());
                }
            } else {
                 removeList.add(c);
            }
        }

        for (Connection c : removeList) {
            connections.remove(c.username);
        }
    }

    // Broadcast to all users in a given game, including the sender
    public void broadcastAll(int gameID, ServerMessage msg) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<>();

        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID == gameID) {
                    c.send(msg.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        for (Connection c : removeList) {
            connections.remove(c.username);
        }
    }

}
