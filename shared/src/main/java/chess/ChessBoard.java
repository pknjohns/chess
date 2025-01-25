package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[(position.getRow() - 1)][(position.getColumn() - 1)] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[(position.getRow()-1)][(position.getColumn()-1)];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        // white team pieces
        // rooks
        ChessPosition position1 = new ChessPosition(1,1);
        ChessPosition position2 = new ChessPosition(1,8);
        ChessPiece wRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(position1,wRook);
        addPiece(position2,wRook);

        // knights
        ChessPosition position3 = new ChessPosition(1,2);
        ChessPosition position4 = new ChessPosition(1,7);
        ChessPiece wKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(position3,wKnight);
        addPiece(position4, wKnight);

        // bishops
        ChessPosition position5 = new ChessPosition(1,3);
        ChessPosition position6 = new ChessPosition(1,6);
        ChessPiece wBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(position5,wBishop);
        addPiece(position6, wBishop);

        // queen
        ChessPosition position7 = new ChessPosition(1,4);
        ChessPiece wQun = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        addPiece(position7,wQun);

        // king
        ChessPosition position8 = new ChessPosition(1,5);
        ChessPiece wKng = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        addPiece(position8,wKng);

        // pawns
        ChessPiece wPawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        for(int i = 1; i <= 8; i++) {
            ChessPosition pawnPos = new ChessPosition(2,i);
            addPiece(pawnPos,wPawn);
        }

        // black team pieces
        // rooks
        ChessPosition posB1 = new ChessPosition(8,1);
        ChessPosition posB2 = new ChessPosition(8,8);
        ChessPiece bRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(posB1,bRook);
        addPiece(posB2,bRook);

        // knights
        ChessPosition posB3 = new ChessPosition(8,2);
        ChessPosition posB4 = new ChessPosition(8,7);
        ChessPiece bKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(posB3,bKnight);
        addPiece(posB4, bKnight);

        // bishops
        ChessPosition posB5 = new ChessPosition(8,3);
        ChessPosition posB6 = new ChessPosition(8,6);
        ChessPiece bBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(posB5,bBishop);
        addPiece(posB6, bBishop);

        // queen
        ChessPosition posB7 = new ChessPosition(8,4);
        ChessPiece bQun = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        addPiece(posB7,bQun);

        // king
        ChessPosition posB8 = new ChessPosition(8,5);
        ChessPiece bKng = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        addPiece(posB8,bKng);

        // pawns
        ChessPiece bPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        for(int i = 1; i <= 8; i++) {
            ChessPosition pawnPos = new ChessPosition(8,i);
            addPiece(pawnPos,bPawn);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
