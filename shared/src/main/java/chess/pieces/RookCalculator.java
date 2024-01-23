package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class RookCalculator {
    private ChessGame.TeamColor color;

    public RookCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

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

    private Collection<ChessMove> directionMoves(ChessBoard board, ChessPosition myPosition, int directionX, int directionY) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        int offsetX = 0;
        int offsetY = 0;
        while (true) {
            offsetX += directionX;
            offsetY += directionY;

            // Check for bounds
            if ((x + offsetX > 8 || x + offsetX < 1) || (y + offsetY > 8 || y + offsetY < 1)) {
                break;
            }

            // Check for empty space
            ChessPosition endPosition = new ChessPosition(x + offsetX, y + offsetY);
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
