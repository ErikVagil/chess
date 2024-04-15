package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.*;
import chess.ChessGame.TeamColor;

public class CalculatorFactory {
    /**
     * Returns a ChessMove in a certain (row, column) direction with checks to see if it's valid
     * 
     * @param board the current game board
     * @param myPosition the position of the chess piece
     * @param directionCol the number of steps in the column direction
     * @param directionRow the number of steps in the row direction
     * @return a validated move in the inputted direction
     */
    public static ChessMove moveInDirection(ChessBoard board, ChessPosition myPosition, int directionCol, int directionRow, TeamColor team) {
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
            if (otherPiece.getTeamColor() != team) {
                return new ChessMove(myPosition, endPosition);
            }
            return null;
        }
    }

    /**
     * Returns all the moves a piece can make in a (row, column) direction
     * 
     * @param board the current game board
     * @param myPosition the position of the chess piece
     * @param directionCol the number of steps in the column direction
     * @param directionRow the number of steps in the row direction
     * @return a collection of chess moves
     */
    public static Collection<ChessMove> directionMoves(ChessBoard board, ChessPosition myPosition, int directionCol, int directionRow, TeamColor team) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int offsetCol = 0;
        int offsetRow = 0;
        while (true) {
            offsetCol += directionCol;
            offsetRow += directionRow;

            // Check for bounds
            if ((col + offsetCol > 8 || col + offsetCol < 1) || (row + offsetRow > 8 || row + offsetRow < 1)) {
                break;
            }

            // Check for empty space
            ChessPosition endPosition = new ChessPosition(row + offsetRow, col + offsetCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (otherPiece == null) {
                // Empty space
                moveList.add(new ChessMove(myPosition, endPosition));
            } else {
                // Hit a piece
                if (otherPiece.getTeamColor() != team) {
                    moveList.add(new ChessMove(myPosition, endPosition));
                }
                break; // Can't go past pieces
            }
        }

        return moveList;
    }
}
