package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);

        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        //find out how far we can move left, right, up down
//        int left = startCol - 1;
//        int right = 8 - startCol;
//        int down = startRow - 1;
//        int up = 8 - startRow;

        for (PieceType piece : PieceType.values()) {
            if (currentPiece.getPieceType() == piece && piece == PieceType.BISHOP) {

                //create ChessMove objects we can make (independent of check, stalemate, or checkmate)
                int i = 1;
                while (i < 8) {

                    int newRowUp = startRow + i;
                    int newRowDown = startRow - i;
                    int newColRight = startCol + i;
                    int newColLeft = startCol - i;

                    if (newRowUp <= 8 && newColRight <= 8) {
                        ChessPosition newPosition = new ChessPosition((startRow + i), startCol + i);
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    if (newRowUp <= 8 && newColLeft >= 1) {
                        ChessPosition newPosition = new ChessPosition((startRow + i), startCol - i);
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    if (newRowDown >= 1  && newColRight <= 8) {
                        ChessPosition newPosition = new ChessPosition(startRow - i, startCol + i);
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    if (newRowDown >= 1  && newColLeft >= 1) {
                        ChessPosition newPosition = new ChessPosition(startRow - i, startCol - i);
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    i++;
                }
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
