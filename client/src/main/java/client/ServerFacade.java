package client;

import client.websocket.ServerMessageObserver;
import client.websocket.WebsocketCommunicator;
import facade.ResponseException;
import model.*;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class ServerFacade {

    private final int port;
    private final WebsocketCommunicator communicator;

    public ServerFacade(int desiredPort, ServerMessageObserver serverMsgObserver) {
        this.port = desiredPort;
        try {
            this.communicator = new WebsocketCommunicator(port, serverMsgObserver);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    //wsCommunicator
    public void connect(String authToken, int gameID) {
        communicator.connect(authToken, gameID);
    }

    // httpCommunicator
    public Object clear() throws ResponseException {
        HttpURLConnection http = makeRequest("DELETE", "/db", "", null);
        return getResponse(http, Map.class);
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        HttpURLConnection http = makeRequest("POST", "/user", "", req);
        return getResponse(http, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        HttpURLConnection http = makeRequest("POST", "/session", "", req);
        return getResponse(http, LoginResult.class);
    }

    public Object logout(String authToken) throws ResponseException {
        HttpURLConnection http = makeRequest("DELETE", "/session", authToken, null);
        return getResponse(http, Map.class);
    }

    public Object createGame(String authToken, CreateRequest cReq) throws ResponseException {
        HttpURLConnection http = makeRequest("POST", "/game", authToken, cReq);
        return getResponse(http, Map.class);
    }

    public ListGameResult listGames(String authToken) throws ResponseException {
        HttpURLConnection http = makeRequest("GET", "/game", authToken, null);
        return getResponse(http, ListGameResult.class);
    }

    public Object joinGame(String authToken, JoinRequest jReq) throws ResponseException {
        HttpURLConnection http = makeRequest("PUT", "/game", authToken, jReq);
        return getResponse(http, Map.class);
    }

    private HttpURLConnection makeRequest(String method, String path, String header, Object request) throws ResponseException {
        String serverUrl = "http://localhost:" + port + path;
        try {
            URL url = (new URI(serverUrl)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            writeRequestBody(header, request, http);
            http.connect(); // actually makes the request
            return http;
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private void writeRequestBody(String header, Object request, HttpURLConnection http) throws IOException {
        // check if header is given -> means need to authorize
        if (!header.isEmpty()) {
            http.addRequestProperty("Authorization", header);
        }

        // check if request body is provided
        if (request != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (OutputStream reqBody = http.getOutputStream()) {
                String jsonBody = new Gson().toJson(request);
                reqBody.write(jsonBody.getBytes());
            }
        }
    }

    private <T> T getResponse(HttpURLConnection http, Class<T> responseClass) throws ResponseException {
        try {
            throwIfNotSuccessful(http);
            return readResponseBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private <T> T readResponseBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T responseBody;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, responseClass);
        }
        return responseBody;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException("other failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
