package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class KnightCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public KnightCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

    /**
     * Calculates all the positions a knight piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * 
     * @param board the current game board
     * @param myPosition the position of the knight piece
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // NNE direction
        ChessMove move = this.moveInDirection(board, myPosition, 1, 2);
        if (move != null) moveList.add(move);

        // NEE direction
        move = this.moveInDirection(board, myPosition, 2, 1);
        if (move != null) moveList.add(move);

        // SEE direction
        move = this.moveInDirection(board, myPosition, 2, -1);
        if (move != null) moveList.add(move);

        // SSE direction
        move = this.moveInDirection(board, myPosition, 1, -2);
        if (move != null) moveList.add(move);

        // SSW direction
        move = this.moveInDirection(board, myPosition, -1, -2);
        if (move != null) moveList.add(move);

        // SWW direction
        move = this.moveInDirection(board, myPosition, -2, -1);
        if (move != null) moveList.add(move);

        // NWW direction
        move = this.moveInDirection(board, myPosition, -2, 1);
        if (move != null) moveList.add(move);
        
        // NNW direction
        move = this.moveInDirection(board, myPosition, -1, 2);
        if (move != null) moveList.add(move);

        return moveList;
    }

    /**
     * Returns a ChessMove in a certain (row, column) direction with checks to see if it's valid
     * 
     * @param board the current game board
     * @param myPosition the position of the chess piece
     * @param directionCol the number of steps in the column direction
     * @param directionRow the number of steps in the row direction
     * @return a validated move in the inputted direction
     */
    private ChessMove moveInDirection(ChessBoard board, ChessPosition myPosition, int directionCol, int directionRow) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check for bounds
        if ((col + directionCol > 8 || col + directionCol < 1) || (row + directionRow > 8 || row + directionRow < 1)) {
            return null;
        }

        // Check for empty space
        ChessPosition endPosition = new ChessPosition(row + directionRow, col + directionCol);
        ChessPiece otherPiece = board.getPiece(endPosition);
        if (otherPiece == null) {
            // Empty space
            return new ChessMove(myPosition, endPosition);
        } else {
            // Hit a piece
            if (otherPiece.getTeamColor() != this.color) {
                return new ChessMove(myPosition, endPosition);
            }
            return null;
        }
    }
}
