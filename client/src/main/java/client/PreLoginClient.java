package client;

import facade.ResponseException;
import model.*;

import java.util.Arrays;

public class PreLoginClient {

    private final ServerFacade facade;
    public State state = State.SIGNEDOUT;
    public String username;
    public String authToken;
    public int gameID;

    public PreLoginClient(int port) {
        facade = new ServerFacade(port, new GameplayClient(port, this));
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> preLogHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                RegisterRequest rReq = new RegisterRequest(params[0], params[1], params[2]);
                RegisterResult authData = facade.register(rReq);
                this.username = params[0];
                this.authToken = authData.authToken();
                state = State.SIGNEDIN;
                return String.format("You've registered and signed in as %s \n", authData.username());
            } catch (ResponseException ex) {
                return ex.getMessage();
            }
        } else {
            return "You are missing some information. Please provide all required information to register\n";
        }
    }

    private String login(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                LoginRequest lReq = new LoginRequest(params[0], params[1]);
                LoginResult authData = facade.login(lReq);
                this.username = params[0];
                this.authToken = authData.authToken();
                state = State.SIGNEDIN;
                return String.format("You signed in as %s \n", authData.username());
            } catch (ResponseException ex) {
                return ex.getMessage();
            }
        } else {
            return "Please provide both a correct username and password to login to an existing account \n";
        }
    }

    private String preLogHelp() {
        return """
            - register <USERNAME> <PASSWORD> <EMAIL>: create an account
            - login <USERNAME> <PASSWORD>: login to an existing account
            - quit: exit chess program
            - help: get help on what commands you can run
            """;
    }
}
