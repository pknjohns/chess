import chess.*;
import client.PreLoginRepl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        new PreLoginRepl(port).run();
    }
}