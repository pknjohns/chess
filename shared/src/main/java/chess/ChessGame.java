package chess;

import java.util.*;

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
        turn = team;
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

        // check if there's a piece at startPosition
        if (gameBoard.getPiece(startPosition) != null) {
            // get piece at startPosition
            ChessPiece startPiece = gameBoard.getPiece(startPosition);
            // get color of startPiece
            TeamColor startClr = startPiece.getTeamColor();

            // make a copy of the gameBoard
            ChessBoard ogGameBoard = new ChessBoard(gameBoard);

            // initialize set to hold currentTeam's valid moves
            Collection<ChessMove> goodMoves = startPiece.pieceMoves(gameBoard, startPosition);

            // initialize set to hold bad moves that need to be removed from good moves
            Collection<ChessMove> badMoves = new ArrayList<>();

            // iterate through each move to make sure they're valid
            for (ChessMove move : goodMoves) {
                // make the move
                makeAnyMove(gameBoard, move);

                // check if making the move puts king in check
                if (isInCheck(startClr)) {
                    // if move puts kin gin check, add to badMove list
                    badMoves.add(move);
                }

                // reset gameBoard to way it was before the move was made
                gameBoard = new ChessBoard(ogGameBoard);
            }
            //remove all bad moves from good moves list
            goodMoves.removeAll(badMoves);
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
        if (currentPiece != null) {
            TeamColor clr = currentPiece.getTeamColor();

            ChessPosition end = move.getEndPosition();
            //ChessPiece endPiece = gameBoard.getPiece(end);

            // make sure it's the turn of the person trying to make the move
            if (getTeamTurn() == clr) {
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

                    // change whose turn it is after move is made
                    setTeamTurn(getOpponentsColor(clr));

                } else {
                    throw new InvalidMoveException("Invalid move: " + move);
                }
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

        // make king and find king's position and moves
        //ChessPiece king = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        ChessPosition kingPosition = findKing(teamColor);

        // find all the opponent's possible moves
        Collection<ChessMove> opponentMoves = findTeamMoves(getOpponentsColor(teamColor));

        // check if kingPosition is in opponentMoves
        for (ChessMove oMove : opponentMoves) {
            if (kingPosition.equals(oMove.getEndPosition())) {
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

        // See if king is in check
        // See if any of the team/s pieces have valid moves
        // if both are true then it's in checkmate
        return isInCheck(teamColor) && findValidTeamMoves(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        // See if king is NOT in check
        // see if there are any valid moves the team has
        // if king isn't in check and there aren't any valid moves, then stalemate
        return !isInCheck(teamColor) && findValidTeamMoves(teamColor).isEmpty();
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
     *
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
     *
     * function to find the positions of all a team's pieces on the board
     *
     * @param teamColor color of the team whose pieces we're looking for
     * @return collection containing the positions of all the team's pieces on the board
     */
    private Collection<ChessPosition> findTeamPositions(TeamColor teamColor) {
        HashSet<ChessPosition> teamPositions = new HashSet<>();

        // tracker to see if we've looked at all the team's pieces yet (doesn't work if team has lost pieces, but that's ok)
        int k = 0;
        // look at every row
        for (int i = 1; i < 9; i++) {
            // look at every column
            for (int j = 1; j < 9; j++) {
                // create new position
                ChessPosition newPosition = new ChessPosition(i, j);
                // get piece at newPosition
                ChessPiece newPiece = gameBoard.getPiece(newPosition);
                // if there actually is a piece there, and we haven't already found all the team's pieces
                if (newPiece != null && k < 16) {
                    // if the piece's color matches the team's color, add it to the collection and increase piece tracker
                    if (newPiece.getTeamColor() == teamColor) {
                        teamPositions.add(newPosition);
                        k++;
                    }
                }
            }
        }
        // return collection of positions of all team's pieces
        return teamPositions;
    }

    /**
     *
     * Gets all the moves the opponent can make
     * Don't need to check if they're valid moves
     * because it doesn't matter if they put themselves in check
     * when capturing current team's king because they will have already won
     *
     * @param teamColor team to get moves for
     * @return collection of ChessMoves the team can make
     */
    private Collection<ChessMove> findTeamMoves(TeamColor teamColor) {
        // initialize collection to hold all the team's possible moves
        HashSet<ChessMove> teamMoves = new HashSet<>();

        // look at each position of the team's pieces
        for (ChessPosition position : findTeamPositions(teamColor)) {
            // get the team's piece at that position
            ChessPiece teamPiece = gameBoard.getPiece(position);
            // add all the moves that piece can possibly make
            teamMoves.addAll(teamPiece.pieceMoves(gameBoard, position));
        }
        return teamMoves;
    }

    /**
     *
     * finds all the valid moves a team can make
     *
     * @param teamColor color of the team we want to find the valid moves for
     * @return collection of all valid moves the team can make
     */
    private Collection<ChessMove> findValidTeamMoves(TeamColor teamColor) {

        HashSet<ChessMove> validTeamMoves = new HashSet<>();

        // look at each position of the team's pieces
        for (ChessPosition position : findTeamPositions(teamColor)) {
            // add all the valid moves the piece at that location can make
            validTeamMoves.addAll(validMoves(position));
        }
        return validTeamMoves;
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

    /**
     *
     * Gets the color of the opponent (opposite team)
     *
     * @param clr color of current team
     * @return color of opponent
     */
    private TeamColor getOpponentsColor(TeamColor clr) {
        return TeamColor.values()[(clr.ordinal() + 1) % 2];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameBoard, chessGame.gameBoard) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, turn);
    }
}
