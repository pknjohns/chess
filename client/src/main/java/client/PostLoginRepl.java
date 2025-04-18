package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginRepl {

    private final PreLoginClient preLogClient;
    private final PostLoginClient postLogClient;

    public PostLoginRepl(int port, PreLoginClient preLogClient) {
        this.preLogClient = preLogClient;
        this.postLogClient = new PostLoginClient(port, preLogClient);
    }

    public void run () {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (preLogClient.state != State.SIGNEDOUT) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = postLogClient.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + preLogClient.state + "] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
