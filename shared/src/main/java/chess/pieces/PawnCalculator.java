package chess.pieces;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

public class PawnCalculator implements Calculator {
    private ChessGame.TeamColor color;

    public PawnCalculator(ChessGame.TeamColor pieceColor) {
        this.color = pieceColor;
    }

    /**
     * Calculates all the positions a pawn piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * 
     * @param board the current game board
     * @param myPosition the position of the pawn piece
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();

        // Check if first move
        if ((this.color == TeamColor.WHITE && myPosition.getRow() == 2) ||
            (this.color == TeamColor.BLACK && myPosition.getRow() == 7)) {
            moveList.addAll(this.regularMoves(board, myPosition, true));
        }
        // Regular move forward
        moveList.addAll(this.regularMoves(board, myPosition, false));

        // Check for attacks
        moveList.addAll(this.attackMoves(board, myPosition));

        return moveList;
    }

    /**
     * Returns a collection of the forward moves a pawn can make
     * 
     * @param board the current game board
     * @param myPosition the position of the pawn piece
     * @param isFirstMove whether or not it's the pawn's first move
     * @return a collection of chess moves
     */
    private Collection<ChessMove> regularMoves(ChessBoard board, ChessPosition myPosition, boolean isFirstMove) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int stepsForward = isFirstMove ? 2 : 1;
        if (this.color == TeamColor.BLACK) stepsForward = -stepsForward;

        // Check for bounds
        if (row + stepsForward > 8) {
            return moveList;
        }

        // Check for empty space
        ChessPosition endPosition = new ChessPosition(row + stepsForward, col);
        ChessPiece otherPiece = board.getPiece(endPosition);
        // Can't go through piece on first move
        if (isFirstMove && 
            ((this.color == TeamColor.WHITE && board.getPiece(new ChessPosition(row + 1, col)) != null) ||
             (this.color == TeamColor.BLACK && board.getPiece(new ChessPosition(row - 1, col)) != null))) {
            return moveList;
        }
        if (otherPiece == null) {
            addMove(myPosition, endPosition, moveList);
        }
        return moveList;
    }

    /**
     * Returns a collection of the attacking/capturing moves a pawn can make
     * 
     * @param board the current game board
     * @param myPosition the position of the pawn piece
     * @return a collection of chess moves
     */
    private Collection<ChessMove> attackMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveList = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        
        if (this.color == TeamColor.WHITE) {
            // Check NW attack
            // Check bounds
            if ((col - 1 >= 1) && (row + 1 <= 8)) {
                // Check if opposite color piece occupies space
                ChessPosition endPosition = new ChessPosition(row + 1, col - 1);
                if (canAttack(board, endPosition, TeamColor.BLACK)) {
                    addMove(myPosition, endPosition, moveList);
                }
            }

            // Check NE attack
            // Check bounds
            if ((col + 1 <= 8) && (row + 1 <= 8)) {
                // Check if opposite color piece occupies space
                ChessPosition endPosition = new ChessPosition(row + 1, col + 1);
                if (canAttack(board, endPosition, TeamColor.BLACK)) {
                    addMove(myPosition, endPosition, moveList);
                }
            }
        } else {
            // Check SW attack
            // Check bounds
            if ((col - 1 >= 1) && (row - 1 >= 1)) {
                // Check if opposite color piece occupies space
                ChessPosition endPosition = new ChessPosition(row - 1, col - 1);
                if (canAttack(board, endPosition, TeamColor.WHITE)) {
                    addMove(myPosition, endPosition, moveList);
                }
            }

            // Check SE attack
            // Check bounds
            if ((col + 1 <= 8) && (row - 1 >= 1)) {
                // Check if opposite color piece occupies space
                ChessPosition endPosition = new ChessPosition(row - 1, col + 1);
                if (canAttack(board, endPosition, TeamColor.WHITE)) {
                    addMove(myPosition, endPosition, moveList);
                }
            }
        }

        return moveList;
    }

    private boolean canAttack(ChessBoard board, ChessPosition endPosition, TeamColor oppositeTeam) {
        return board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() == oppositeTeam;
    }

    private boolean canPromote(ChessPosition endPosition) {
        return this.color == TeamColor.WHITE && endPosition.getRow() == 8 || this.color == TeamColor.BLACK && endPosition.getRow() == 1;
    }

    private void addMove(ChessPosition myPosition, ChessPosition endPosition, Collection<ChessMove> moveList) {
        // Check for promotion
        if (canPromote(endPosition)) {
            moveList.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
            moveList.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
            moveList.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
            moveList.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
        } else {
            moveList.add(new ChessMove(myPosition, endPosition));
        }
    }
}
