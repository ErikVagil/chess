package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class KingCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public KingCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // North direction
        ChessMove move = this.moveInDirection(board, myPosition, 0, 1);
        if (move != null) moveList.add(move);

        // North-east direction
        move = this.moveInDirection(board, myPosition, 1, 1);
        if (move != null) moveList.add(move);

        // East direction
        move = this.moveInDirection(board, myPosition, 1, 0);
        if (move != null) moveList.add(move);

        // South-east direction
        move = this.moveInDirection(board, myPosition, 1, -1);
        if (move != null) moveList.add(move);

        // South direction
        move = this.moveInDirection(board, myPosition, 0, -1);
        if (move != null) moveList.add(move);

        // South-west direction
        move = this.moveInDirection(board, myPosition, -1, -1);
        if (move != null) moveList.add(move);

        // West direction
        move = this.moveInDirection(board, myPosition, -1, 0);
        if (move != null) moveList.add(move);
        
        // North-west direction
        move = this.moveInDirection(board, myPosition, -1, 1);
        if (move != null) moveList.add(move);

        return moveList;
    }

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
