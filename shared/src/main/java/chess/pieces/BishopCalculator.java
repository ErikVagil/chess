package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class BishopCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public BishopCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // North-east direction
        moveList.addAll(this.directionMoves(board, myPosition, 1, 1));

        // South-east direction
        moveList.addAll(this.directionMoves(board, myPosition, 1, -1));

        // South-west direction
        moveList.addAll(directionMoves(board, myPosition, -1, -1));
        
        // North-west direction
        moveList.addAll(directionMoves(board, myPosition, -1, 1));

        return moveList;
    }

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
