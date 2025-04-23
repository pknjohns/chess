package client;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class GameplayRepl {

    private final GameplayClient gameplayClient;
    private final PreLoginClient preLoginClient;

    public GameplayRepl(int port, PreLoginClient preLoginClient, GameplayClient gameplayClient) {
        this.preLoginClient = preLoginClient;
        this.gameplayClient = gameplayClient;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String result;
        while (preLoginClient.state == State.GAMEPLAY) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = gameplayClient.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + preLoginClient.state + "] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
