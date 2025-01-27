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

        // find ChessPiece (bishop) we're looking at
        ChessPiece currentPiece = board.getPiece(myPosition);

        // create object to store all valid moves for bishop
        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // create array of directions/ diagonals
        int[][] directions = {
                {1,1},  // top right diagonal
                {1,-1}, // top left diagonal
                {-1,1}, // bottom right diagonal
                {-1,-1} // bottom left diagonal
        };

        // Iterate through diagonals
        for (int[] direction : directions) {
            // store new row and col locations
            int newRow = startRow;
            int newCol = startCol;
            boolean blocked = false;

            // as long as new ChessPosition is within range:
            // limits are 1 and 8 instead of 0 and 9 because we update newRow and newCol AFTER we check if they're in range
            while (!blocked) { //FIX THIS LOGIC SO PIECES AT EDGES OF BOARD WILL WORK
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

    /**
     * Calculates all positions a king can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where king can move to
     */
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);

        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        List<Integer> steps = Arrays.asList(-1,0,1);
        for (int step1 : steps) {
            int newRow = startRow + step1;
            if (newRow < 9 && newRow > 0) {
                for (int step2 : steps) {
                    int newCol = startCol + step2;
                    if (newCol < 9 && newCol > 0) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        if (!newPosition.equals(myPosition)) {
                            if (board.getPiece(newPosition) != null) {
                                if (board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                                    moves.add(newMove);
                                }
                            } else {
                                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                                moves.add(newMove);
                            }
                        }
                    }
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
        ChessPiece currentPiece = board.getPiece(myPosition);

        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        List<Integer> lilSteps = Arrays.asList(-1,1);
        List<Integer> bigSteps = Arrays.asList(-2,2);

        for (int big : bigSteps) {
            int newRow = startRow + big;
            if (newRow < 9 && newRow > 0) {
                for (int lil : lilSteps) {
                    int newCol = startCol + lil;
                    if (newCol < 9 && newCol > 0) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        if (board.getPiece(newPosition) != null) {
                            if (board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                                moves.add(newMove);
                            }
                        } else {
                            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                            moves.add(newMove);
                        }
                    }
                }
            }
        }

        for (int lil : lilSteps) {
            int newRow = startRow + lil;
            if (newRow < 9 && newRow > 0) {
                for (int big : bigSteps) {
                    int newCol = startCol + big;
                    if (newCol < 9 && newCol > 0) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        if (board.getPiece(newPosition) != null) {
                            if (board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                                moves.add(newMove);
                            }
                        } else {
                            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                            moves.add(newMove);
                        }
                    }
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

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // make list with all promotion options for player
        List<PieceType> promotions = Arrays.asList(PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN);

        // white team logic
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            // find next position pawn can move to
            int newRow = startRow + 1;
            ChessPosition newPosition = new ChessPosition(newRow, startCol);

            // check that space in front of pawn is empty so it can move
            if (board.getPiece(newPosition) == null) {
                // check if pawn is on white starting line
                if (startRow == 2) {
                    ChessPosition iNewPosition = new ChessPosition(startRow + 2, startCol);

                    // check that space 2 ahead of pawn is empty
                    if (board.getPiece(iNewPosition) == null) {
                        ChessMove startMove = new ChessMove(myPosition, iNewPosition, null);
                        moves.add(startMove);
                    }
                }

                // check if pawn has reached end of board and earned a promotion
                if (newRow == 8) {
                    for (PieceType promotion : promotions) {
                        ChessMove newMove = new ChessMove(myPosition, newPosition, promotion);
                        moves.add(newMove);
                    }
                } else {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
                }
            }

            // check if pawn can capture a piece
            List<Integer> captSpots = Arrays.asList(-1,1);
            for (int captSpot : captSpots) {
                int captCol = startCol + captSpot;
                if (captCol < 9 && captCol > 0) {
                    ChessPosition captPosition = new ChessPosition(newRow, captCol);
                    if (board.getPiece(captPosition) != null && board.getPiece(captPosition).getTeamColor() != currentPiece.getTeamColor()) {
                        // check if pawn also gets promoted during capture
                        if (newRow == 8) {
                            for (PieceType promotion : promotions) {
                                ChessMove captMove = new ChessMove(myPosition, captPosition, promotion);
                                moves.add(captMove);
                            }
                        } else {
                            ChessMove captMove = new ChessMove(myPosition, captPosition, null);
                            moves.add(captMove);
                        }
                    }
                }
            }

            // BLACK team logic
        } else {
            // find next position pawn can move to
            int newRow = startRow - 1;
            ChessPosition newPosition = new ChessPosition(newRow, startCol);

            // check that space in front of pawn is empty so it can move
            if (board.getPiece(newPosition) == null) {
                // check if pawn is on starting line for black
                if (startRow == 7) {
                    ChessPosition iNewPosition = new ChessPosition(startRow - 2, startCol);

                    // check that space 2 ahead of pawn is empty
                    if (board.getPiece(iNewPosition) == null) {
                        ChessMove startMove = new ChessMove(myPosition, iNewPosition, null);
                        moves.add(startMove);
                    }
                }

                // check if pawn reached the end of board and earns a promotion
                if (newRow == 1) {
                    for (PieceType promotion : promotions) {
                        ChessMove newMove = new ChessMove(myPosition, newPosition, promotion);
                        moves.add(newMove);
                    }
                } else {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
                }
            }

            // check if pawn can capture a piece
            List<Integer> captSpots = Arrays.asList(-1,1);
            // look left and right
            for (int captSpot : captSpots) {
                int captCol = startCol + captSpot;
                // check if potential capture spots are actually on chess board
                if (captCol < 9 && captCol > 0) {
                    ChessPosition captPosition = new ChessPosition(newRow, captCol);
                    // check if an opponent's piece is in the capture spots
                    if (board.getPiece(captPosition) != null && board.getPiece(captPosition).getTeamColor() != currentPiece.getTeamColor()) {
                        // check if pawn gets promoted during capture
                        if (newRow == 1) {
                            for (PieceType promotion : promotions) {
                                ChessMove captMove = new ChessMove(myPosition, captPosition, promotion);
                                moves.add(captMove);
                            }
                        } else {
                            ChessMove captMove = new ChessMove(myPosition, captPosition, null);
                            moves.add(captMove);
                        }
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

        Collection<ChessMove> diagMoves = bishopMoves(board, myPosition);

        Collection<ChessMove> strtMoves = rookMoves(board, myPosition);

        HashSet<ChessMove> moves = new HashSet<>(diagMoves);

        moves.addAll(strtMoves);

        return moves;
    }

    /**
     * Calculates all positions a rook can move to
     * @param board ChessBoard object that tracks where pieces are and their team
     * @param myPosition ChessPosition object that helps us find current piece
     * @return HashSet of ChessPosition objects saying where rook can move to
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);

        HashSet<ChessMove> moves = new HashSet<>();

        // get start position info
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        List<Integer> negRows = Arrays.asList(-1,-2,-3,-4,-5,-6,-7);
        List<Integer> posRows = Arrays.asList(1,2,3,4,5,6,7);
        List<Integer> negCols = Arrays.asList(-1,-2,-3,-4,-5,-6,-7);
        List<Integer> posCols = Arrays.asList(1,2,3,4,5,6,7);

        for (int negRow : negRows) {
            int newRow = startRow + negRow;
            if (newRow < 9 && newRow > 0) {
                ChessPosition newPosition = new ChessPosition(newRow, startCol);
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    break;
                } else {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
                }
            }
        }

        for (int posRow : posRows) {
            int newRow = startRow + posRow;
            if (newRow < 9 && newRow > 0) {
                ChessPosition newPosition = new ChessPosition(newRow, startCol);
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    break;
                } else {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
                }
            }
        }

        for (int negCol : negCols) {
            int newCol = startCol + negCol;
            if (newCol < 9 && newCol > 0) {
                ChessPosition newPosition = new ChessPosition(startRow, newCol);
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    break;
                } else {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
                }
            }
        }

        for (int posCol : posCols) {
            int newCol = startCol + posCol;
            if (newCol < 9 && newCol > 0) {
                ChessPosition newPosition = new ChessPosition(startRow, newCol);
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        moves.add(newMove);
                    }
                    break;
                } else {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
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
