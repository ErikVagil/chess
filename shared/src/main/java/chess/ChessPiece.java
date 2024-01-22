package chess;

import java.util.Collection;
import java.util.HashSet;
import chess.pieces.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();
        switch (this.type) {
            case PieceType.KING:
                moveList = new KingCalculator().pieceMoves(board, myPosition);
                break;
            case PieceType.QUEEN:
                moveList = new QueenCalculator().pieceMoves(board, myPosition);
                break;
            case PieceType.BISHOP:
                moveList = new BishopCalculator().pieceMoves(board, myPosition);
                break;
            case PieceType.KNIGHT:
                moveList = new KnightCalculator().pieceMoves(board, myPosition);
                break;
            case PieceType.ROOK:
                moveList = new RookCalculator().pieceMoves(board, myPosition);
                break;
            case PieceType.PAWN:
                moveList = new PawnCalculator().pieceMoves(board, myPosition);
                break;
            default:
                throw new RuntimeException("Piece type does not exist");
        }
        return moveList;
    }

    @Override
    public boolean equals(Object o) {
        // Check if other object is this object
        if (o == this) {
            return true;
        }

        // Check if other object is a ChessPiece
        if (!(o instanceof ChessPiece)) {
            return false;
        }

        // Check if instance variables have the same values
        ChessPiece other = (ChessPiece)o;
        if (this.color == other.getTeamColor() && this.type == other.getPieceType()) {
            return true;
        }
        else {
            return false;
        }
    }
}
