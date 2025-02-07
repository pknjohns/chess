package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    // create board parameter for class
    private ChessBoard gameBoard = new ChessBoard();
    private TeamColor turn = TeamColor.WHITE;

    public ChessGame() {
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (turn == team) {
            turn = TeamColor.BLACK;
        } else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //throw new RuntimeException("Not implemented");

        // check if there's a piece at startPosition
        if (gameBoard.getPiece(startPosition) != null) {
            // get piece at startPosition
            ChessPiece startPiece = gameBoard.getPiece(startPosition);
            TeamColor clr = startPiece.getTeamColor();
            //TeamColor opponentClr = TeamColor.values()[(clr.ordinal() + 1) % 2];

            // make a copy of the gameBoard
            ChessBoard ogGameBoard = new ChessBoard(gameBoard);

            // initialize set to hold currentTeam's valid moves
            Collection<ChessMove> goodMoves = startPiece.pieceMoves(gameBoard, startPosition);

            // iterate through each move to make sure they're valid
            for (ChessMove move : goodMoves) {
                // make the move
                makeAnyMove(gameBoard, move);

                if (isInCheck(clr)) {
                    goodMoves.remove(move);
                }

                // reset gameBoard to way it was before the move was made
                gameBoard = new ChessBoard(ogGameBoard);
            }
            return goodMoves;
        } else {
            // if no piece is found at startPosition, return null
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //throw new RuntimeException("Not implemented");

        ChessPosition start = move.getStartPosition();
        ChessPiece currentPiece = gameBoard.getPiece(start);
        TeamColor clr = currentPiece.getTeamColor();

        ChessPosition end = move.getEndPosition();
        //ChessPiece endPiece = gameBoard.getPiece(end);

        // make sure it's the turn of the person trying to make the move
        if (turn == clr) {
            // if the attempted move is valid, make the move
            if (validMoves(start).contains(move)) {

                // check if there is a promotion piece/ currentPiece = pawn
                if (move.getPromotionPiece() != null) {
                    currentPiece = new ChessPiece(clr, move.getPromotionPiece());
                }

                // add/move piece to end/ new position
                gameBoard.addPiece(end, currentPiece);
                // remove piece from start position
                gameBoard.addPiece(start, null);

//                // check if end position is empty
//                if (endPiece == null) {
//                don't think I need to do this^^^ because validMoves/ pieceMoves already calculate if I can capture there or not
//                }
            } else {
                throw new InvalidMoveException("Invalid move: " + move);
            }
        } else {
            throw new InvalidMoveException("Invalid move: " + move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //throw new RuntimeException("Not implemented");

        // should also do this after piece at startPosition has been moved in case the king was moved
        ChessPosition kingPosition = findKing(teamColor);

        // find opponent's color
        TeamColor opponentClr = TeamColor.values()[(teamColor.ordinal() + 1) % 2];

        // find all the opponent's possible moves
        Collection<ChessMove> opponentMoves = findTeamMoves(opponentClr);

        // check if kingPosition is in opponentMoves
        for (ChessMove oMove : opponentMoves) {
            ChessPosition opponentEndPosition = oMove.getEndPosition();
            if (kingPosition.equals(opponentEndPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //throw new RuntimeException("Not implemented");
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //throw new RuntimeException("Not implemented");
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = new ChessBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    //------------------------------------------------------------------------------------------------
    // Custom Methods
    //------------------------------------------------------------------------------------------------

    /**
     * Finds the position of a team's king
     *
     * @param teamColor team to look for king
     * @return position of king if found, otherwise [0][0] i.e. [1][1]
     */
    private ChessPosition findKing(TeamColor teamColor) {

        // iterate through each row of board
        for (int i = 1; i < 9; i++) {

            // iterate through each column of board
            for (int j = 1; j < 9; j++) {

                // look at [row][col] on board
                ChessPosition searchPosition = new ChessPosition(i, j);

                // get piece from that [row][col]
                ChessPiece searchPiece = gameBoard.getPiece(searchPosition);

                // check if there's actually a piece there
                if (searchPiece != null) {

                    // get piece's color and type
                    TeamColor searchClr = searchPiece.getTeamColor();
                    ChessPiece.PieceType searchType = searchPiece.getPieceType();

                    // check if the piece is a king and the right clr
                    if (searchType == ChessPiece.PieceType.KING && searchClr == teamColor) {
                        return searchPosition;
                    }
                }
            }
        }
        // dummy position to return if king isn't found (which should never happen)
        // this should really throw an error but didn't know how to implement it
        return new ChessPosition(1,1);
    }

    /**
     * Gets all the moves the opponent can make
     * Don't need to check if they're valid moves because if other team puts themselves in check then opponent will just win, so doesn't matter if they put themselves in check or not
     *
     * @param teamColor team to get moves for
     * @return collection of ChessMoves the team can make
     */
    private Collection<ChessMove> findTeamMoves(TeamColor teamColor) {
        HashSet<ChessMove> teamMoves = new HashSet<>();

        // tracker to see if we've looked at all the team's pieces yet
        int k = 0;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition newPosition = new ChessPosition(i,j);
                ChessPiece newPiece = gameBoard.getPiece(newPosition);
                if (newPiece != null && k < 16) {
                    if (newPiece.getTeamColor() == teamColor) {
                        Collection<ChessMove> pieceMove = newPiece.pieceMoves(gameBoard, newPosition); //validMoves(newPosition)
                        teamMoves.addAll(pieceMove);
                        k++;
                    }
                }
            }
        }
        return teamMoves;
    }

    /**
     *
     * makes a move on a chessboard without checking if it's legal
     *
     * @param board board to make the move on
     * @param move move to make on the board
     */
    private void makeAnyMove(ChessBoard board, ChessMove move) {

        ChessPosition start = move.getStartPosition();
        ChessPiece currentPiece = board.getPiece(start);
        TeamColor clr = currentPiece.getTeamColor();

        ChessPosition end = move.getEndPosition();
        //ChessPiece endPiece = board.getPiece(end);

        // check if there is a promotion piece/ currentPiece = pawn
        if (move.getPromotionPiece() != null) {
            currentPiece = new ChessPiece(clr, move.getPromotionPiece());
        }

        // add/move piece to end/ new position
        board.addPiece(end, currentPiece);
        // remove piece from start position
        board.addPiece(start, null);
    }
}
