package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.*;

public class KingCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public KingCalculator(ChessGame.TeamColor pieceColor) {
        color = pieceColor;
    }

    /**
     * Calculates all the positions a king piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * 
     * @param board the current game board
     * @param myPosition the position of the king piece
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // North direction
        ChessMove move = CalculatorFactory.moveInDirection(board, myPosition, 0, 1, color);
        if (move != null) moveList.add(move);

        // North-east direction
        move = CalculatorFactory.moveInDirection(board, myPosition, 1, 1, color);
        if (move != null) moveList.add(move);

        // East direction
        move = CalculatorFactory.moveInDirection(board, myPosition, 1, 0, color);
        if (move != null) moveList.add(move);

        // South-east direction
        move = CalculatorFactory.moveInDirection(board, myPosition, 1, -1, color);
        if (move != null) moveList.add(move);

        // South direction
        move = CalculatorFactory.moveInDirection(board, myPosition, 0, -1, color);
        if (move != null) moveList.add(move);

        // South-west direction
        move = CalculatorFactory.moveInDirection(board, myPosition, -1, -1, color);
        if (move != null) moveList.add(move);

        // West direction
        move = CalculatorFactory.moveInDirection(board, myPosition, -1, 0, color);
        if (move != null) moveList.add(move);
        
        // North-west direction
        move = CalculatorFactory.moveInDirection(board, myPosition, -1, 1, color);
        if (move != null) moveList.add(move);

        return moveList;
    }

    
}
