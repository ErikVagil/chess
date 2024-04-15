package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.*;

public class KnightCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public KnightCalculator(ChessGame.TeamColor pieceColor) {
        color = pieceColor;
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
        ChessMove move = CalculatorFactory.moveInDirection(board, myPosition, 1, 2, color);
        if (move != null) moveList.add(move);

        // NEE direction
        move = CalculatorFactory.moveInDirection(board, myPosition, 2, 1, color);
        if (move != null) moveList.add(move);

        // SEE direction
        move = CalculatorFactory.moveInDirection(board, myPosition, 2, -1, color);
        if (move != null) moveList.add(move);

        // SSE direction
        move = CalculatorFactory.moveInDirection(board, myPosition, 1, -2, color);
        if (move != null) moveList.add(move);

        // SSW direction
        move = CalculatorFactory.moveInDirection(board, myPosition, -1, -2, color);
        if (move != null) moveList.add(move);

        // SWW direction
        move = CalculatorFactory.moveInDirection(board, myPosition, -2, -1, color);
        if (move != null) moveList.add(move);

        // NWW direction
        move = CalculatorFactory.moveInDirection(board, myPosition, -2, 1, color);
        if (move != null) moveList.add(move);
        
        // NNW direction
        move = CalculatorFactory.moveInDirection(board, myPosition, -1, 2, color);
        if (move != null) moveList.add(move);

        return moveList;
    }
}
