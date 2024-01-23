package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class QueenCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public QueenCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // Queen is basically a bishop and a rook combined
        Calculator bCalculator = new BishopCalculator(this.color);
        moveList.addAll(bCalculator.pieceMoves(board, myPosition));
        
        Calculator rCalculator = new RookCalculator(this.color);
        moveList.addAll(rCalculator.pieceMoves(board, myPosition));

        return moveList;
    }
}
