package chess;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
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
        // Get piece at startPosition and all of its moves
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> moveList = piece.pieceMoves(this.board, startPosition);

        // Find the king's position on the board
        ChessPosition kingPosition = this.findKingPosition(piece.getTeamColor());
        
        /* Iterate through each move and simulate it -> check if making that move gives
            any enemy pieces an opening to the king */
        HashSet<ChessMove> illegalMoves = new HashSet<>();
        for (ChessMove move : moveList) {
            boolean foundConflictingMove = false;
            ChessBoard simulatedBoard = this.board.clone();
            // Make the move
            simulatedBoard.addPiece(move.getStartPosition(), null);
            simulatedBoard.addPiece(move.getEndPosition(), piece);
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    /* Find an enemy piece and get all of the moves it can make after the original piece
                        simulates its move */
                    ChessPosition testPosition = new ChessPosition(row, col);
                    ChessPiece enemyPiece = simulatedBoard.getPiece(testPosition);
                    if (enemyPiece != null && 
                        enemyPiece.getTeamColor() != piece.getTeamColor()) {
                        Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(simulatedBoard, testPosition);
                        for (ChessMove enemyMove : enemyMoves) {
                            /* If the enemy piece can capture the king, then the move the original piece wants
                                to make is illegal */
                            if (enemyMove.getEndPosition().equals(kingPosition)) {
                                foundConflictingMove = true;
                                illegalMoves.add(move);
                            }
                        }
                    }
                    // If even one enemy piece can reach the king, then the move is illegal
                    // No need to check other enemy pieces
                    if (foundConflictingMove) break;
                }
                if (foundConflictingMove) break;
            }
        }

        // Remove all illegal moves from the movelist
        moveList.removeAll(illegalMoves);

        return moveList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece pieceToMove = this.board.getPiece(move.getStartPosition());
        Collection<ChessMove> validMoveList = this.validMoves(move.getStartPosition());

        // The move must move a valid piece
        if (!validMoveList.contains(move) || 
            this.teamTurn != pieceToMove.getTeamColor()) {
            throw new InvalidMoveException("Illegal move passed as parameter");
        }

        // Move the piece on the board
        this.board.addPiece(start, null);
        this.board.addPiece(end, pieceToMove);

        // If it's a pawn and on the end, promote it
        if (pieceToMove.getPieceType() == PieceType.PAWN &&
            ((pieceToMove.getTeamColor() == TeamColor.WHITE && end.getRow() == 8) ||
             (pieceToMove.getTeamColor() == TeamColor.BLACK && end.getRow() == 1))) {
            ChessPiece promotionPiece = new ChessPiece(this.teamTurn, move.getPromotionPiece());
            this.board.addPiece(end, promotionPiece);
        }

        // Switch whose turn it is
        if (this.teamTurn == TeamColor.WHITE) {
            this.teamTurn = TeamColor.BLACK;
        } else {
            this.teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = this.findKingPosition(teamColor);

        // Check opposite color pieces' moves
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                // Check if an opposite color piece sits on this position
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = this.board.getPiece(currentPosition);
                if (currentPiece == null || 
                    currentPiece.getTeamColor() == teamColor) {
                    continue;
                }

                // Get the piece's moves and check if any capture the king
                Collection<ChessMove> moveList = currentPiece.pieceMoves(board, currentPosition);
                for (ChessMove move : moveList) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }

        // No moves found that can capture the king
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }


    /**
     * Returns the position of the teamColor's king
     * 
     * @param teamColor the color of the team whose king needs to be found
     * @return the position of the king
     */
    private ChessPosition findKingPosition(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece testPiece = this.board.getPiece(new ChessPosition(row, col));
                if (testPiece != null &&
                    testPiece.getPieceType() == PieceType.KING &&
                    testPiece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(row, col);
                    break;
                }
            }
        }
        return kingPosition;
    }
}
