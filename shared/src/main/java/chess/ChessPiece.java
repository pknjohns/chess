package chess;

import java.util.*;

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

        if (currentPiece.getPieceType() == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        } else if (currentPiece.getPieceType() == PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (currentPiece.getPieceType() == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else if (currentPiece.getPieceType() == PieceType.PAWN) {
            return pawnMoves(board,myPosition);
        } else if (currentPiece.getPieceType() == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        } else if (currentPiece.getPieceType() == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        } else {
            return moves;
        }
    }

    /**
     * Calculates all positions a bishop can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where bishop can move to
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {

        // create array of directions bishop can move in
        int[][] directions = {
                {1,1},  // top right diagonal
                {1,-1}, // top left diagonal
                {-1,1}, // bottom right diagonal
                {-1,-1} // bottom left diagonal
        };
        // call getMoves and return result
        return getMoves(board, myPosition, directions);
    }

    /**
     * Calculates all positions a king can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where king can move to
     */
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {


        // find ChessPiece (king) we're looking at
        ChessPiece currentPiece = board.getPiece(myPosition);

        // create object to store all valid moves for bishop
        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Define all possible king moves
        int[][] directions = {
                {-1, -1}, {-1, 1}, {-1, 0}, {1, -1}, {1, 1},
                {1, 0}, {0, -1}, {0, 1}
        };

        // Iterate through directions
        for (int[] direction : directions) {
            // store new row and col locations
            int newRow = startRow;
            int newCol = startCol;

            newRow += direction[0];
            newCol += direction[1];

            // check if we're still in-bounds or not
            if (newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                // make new ChessPosition and find piece there
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece newPiece = board.getPiece(newPosition);

                // if we find a piece at newPosition
                if (newPiece != null) {
                    // if the piece isn't on our team, add newPosition to moves
                    if (newPiece.getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                } else {
                    // empty space, add newPosition to moves
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return moves;
    }

    /**
     * Calculates all positions a knight can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where knight can move to
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {

        // find ChessPiece (knight) we're looking at
        ChessPiece currentPiece = board.getPiece(myPosition);

        // create object to store all valid moves for bishop
        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Define all possible knight moves
        int[][] directions = {
                {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
                {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
        };

        // Iterate through directions
        for (int[] direction : directions) {
            // store new row and col locations
            int newRow = startRow;
            int newCol = startCol;

            newRow += direction[0];
            newCol += direction[1];

            // check if we're still in-bounds or not
            if (newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                // make new ChessPosition and find piece there
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece newPiece = board.getPiece(newPosition);

                // if we find a piece at newPosition
                if (newPiece != null) {
                    // if the piece isn't on our team, add newPosition to moves
                    if (newPiece.getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    // we've hit a piece and can't continue in this direction
                } else {
                    // empty space, add newPosition to moves
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return moves;
    }

    /**
     * Calculates all positions a pawn can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where pawn can move to
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);
        HashSet<ChessMove> moves = new HashSet<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Determine movement direction and promotion row based on team color

        int moveDirection;
        int startRowForDoubleMove;
        int promotionRow;

        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            moveDirection = 1;
            startRowForDoubleMove = 2;
            promotionRow = 8;
        } else {
            moveDirection = -1;
            startRowForDoubleMove = 7;
            promotionRow = 1;
        }

        // Define all promotion options
        List<PieceType> promotions = Arrays.asList(PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN);

        // Single forward move
        int newRow = startRow + moveDirection;
        ChessPosition newPosition = new ChessPosition(newRow, startCol);

        if (board.getPiece(newPosition) == null) {
            if (newRow == promotionRow) {
                // Add promotion moves
                for (PieceType promotion : promotions) {
                    moves.add(new ChessMove(myPosition, newPosition, promotion));
                }
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }

            // Double forward move
            if (startRow == startRowForDoubleMove) {
                ChessPosition doubleMovePosition = new ChessPosition(startRow + 2 * moveDirection, startCol);
                if (board.getPiece(doubleMovePosition) == null) {
                    moves.add(new ChessMove(myPosition, doubleMovePosition, null));
                }
            }
        }

        // Capture moves (left and right diagonals)
        List<Integer> captureOffsets = Arrays.asList(-1, 1);
        for (int offset : captureOffsets) {
            int captureCol = startCol + offset;
            if (captureCol >= 1 && captureCol <= 8) { // Ensure within board bounds
                ChessPosition capturePosition = new ChessPosition(newRow, captureCol);
                ChessPiece targetPiece = board.getPiece(capturePosition);

                if (targetPiece != null && targetPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    if (newRow == promotionRow) {
                        // Add promotion moves during capture
                        for (PieceType promotion : promotions) {
                            moves.add(new ChessMove(myPosition, capturePosition, promotion));
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, capturePosition, null));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Calculates all positions a queen can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where queen can move to
     */
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> diagonalMoves = bishopMoves(board, myPosition);

        Collection<ChessMove> straightMoves = rookMoves(board, myPosition);

        HashSet<ChessMove> moves = new HashSet<>(diagonalMoves);

        moves.addAll(straightMoves);

        return moves;
    }

    /**
     * Calculates all positions a rook can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where rook can move to
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {

        // create array of directions rook can move in
        int[][] directions = {
                {0,1},  // right
                {0,-1}, // left
                {1,0}, // up
                {-1,0} // down
        };
        // getMoves and return result
        return getMoves(board, myPosition, directions);

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

    //------------------------------------------------------------------------------------------------
    // Custom Methods
    //------------------------------------------------------------------------------------------------

    /**
     *
     * finds all possible moves a piece can make based on the directions it can move in
     *
     * @param board board the piece is on
     * @param myPosition position the piece is at
     * @param directions the directions the piece can move in
     * @return collection of possible moves the piece can make
     */
    private Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        // find ChessPiece we're looking at
        ChessPiece currentPiece = board.getPiece(myPosition);

        // create object to store all possible moves for piece
        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Iterate through provided directions
        for (int[] direction : directions) {
            // store new row and col locations
            int newRow = startRow;
            int newCol = startCol;
            boolean blocked = false;

            // as long as new ChessPosition is within range:
            while (!blocked) {
                newRow += direction[0];
                newCol += direction[1];

                // check if we're still in-bounds or not
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) break;

                // make new ChessPosition and find piece there
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece newPiece = board.getPiece(newPosition);

                // if we find a piece at newPosition
                if (newPiece != null) {
                    // if the piece isn't on our team, add newPosition to moves, else break
                    if (newPiece.getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    // we've hit a piece and can't continue along this diagonal, so break
                    blocked = true;
                } else {
                    // empty space, add newPosition to moves
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return moves;
    }
}
