package client;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreLoginRepl {

    private final int port;
    private final PreLoginClient preLogClient;

    public PreLoginRepl(int port) {
        this.port = port;
        preLogClient = new PreLoginClient(port);
    }

    public void run () {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = preLogClient.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
                if (preLogClient.state == State.SIGNEDIN) {
                    new PostLoginRepl(port, preLogClient).run();
                }
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
