package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.*;

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
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, 0, 1, color));

        // East direction
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, 1, 0, color));

        // South direction
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, 0, -1, color));
        
        // West direction
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, -1, 0, color));

        return moveList;
    }
}
