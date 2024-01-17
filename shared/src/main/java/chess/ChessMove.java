package chess;

import java.lang.StringBuilder;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition start, end;
    private ChessPiece.PieceType promoPieceType;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promoPieceType = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promoPieceType;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("ChessMove(");
        output.append(this.start);
        output.append(", ");
        output.append(this.end);
        output.append(", ");
        output.append(this.promoPieceType);
        output.append(")");
        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        // Check if other object is this object
        if (o == this) {
            return true;
        }

        // Check if other object is a ChessMove
        if (!(o instanceof ChessMove)) {
            return false;
        }

        // Check if instance variables have the same values
        ChessMove other = (ChessMove)o;
        if (this.start.equals(other.getStartPosition()) && 
            this.end.equals(other.getEndPosition()) && 
            this.promoPieceType == other.getPromotionPiece()) {
            return true;
        }
        else {
            return false;
        }
    }
}
