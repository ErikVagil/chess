package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class RookCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public RookCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

    /**
     * Calculates all the positions a rook piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * 
     * @param board the current game board
     * @param myPosition the position of the rook piece
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // North direction
        moveList.addAll(this.directionMoves(board, myPosition, 0, 1));

        // East direction
        moveList.addAll(this.directionMoves(board, myPosition, 1, 0));

        // South direction
        moveList.addAll(directionMoves(board, myPosition, 0, -1));
        
        // West direction
        moveList.addAll(directionMoves(board, myPosition, -1, 0));

        return moveList;
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
    private Collection<ChessMove> directionMoves(ChessBoard board, ChessPosition myPosition, int directionCol, int directionRow) {
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
                if (otherPiece.getTeamColor() != this.color) {
                    moveList.add(new ChessMove(myPosition, endPosition));
                }
                break; // Can't go past pieces
            }
        }

        return moveList;
    }
}
