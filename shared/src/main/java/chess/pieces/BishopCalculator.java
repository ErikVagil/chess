package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.*;

public class BishopCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public BishopCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

    /**
     * Calculates all the positions a bishop piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * 
     * @param board the current game board
     * @param myPosition the position of the bishop piece
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // North-east direction
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, 1, 1, color));

        // South-east direction
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, 1, -1, color));

        // South-west direction
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, -1, -1, color));
        
        // North-west direction
        moveList.addAll(CalculatorFactory.directionMoves(board, myPosition, -1, 1, color));

        return moveList;
    }
}
