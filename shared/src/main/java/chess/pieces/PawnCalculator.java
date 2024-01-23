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
            // Empty space, check for promotion
            if ((this.color == TeamColor.WHITE && endPosition.getRow() == 8) ||
                (this.color == TeamColor.BLACK && endPosition.getRow() == 1)) {
                moveList.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                moveList.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                moveList.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                moveList.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
            } else {
                moveList.add(new ChessMove(myPosition, endPosition));
            }
        }
        return moveList;
    }

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
                if (board.getPiece(endPosition) != null && 
                    board.getPiece(endPosition).getTeamColor() == TeamColor.BLACK) {
                    // Check for promotion
                    if ((this.color == TeamColor.WHITE && endPosition.getRow() == 8) ||
                        (this.color == TeamColor.BLACK && endPosition.getRow() == 1)) {
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                    } else {
                        moveList.add(new ChessMove(myPosition, endPosition));
                    }
                }
            }

            // Check NE attack
            // Check bounds
            if ((col + 1 <= 8) && (row + 1 <= 8)) {
                // Check if opposite color piece occupies space
                ChessPosition endPosition = new ChessPosition(row + 1, col + 1);
                if (board.getPiece(endPosition) != null && 
                    board.getPiece(endPosition).getTeamColor() == TeamColor.BLACK) {
                    // Check for promotion
                    if ((this.color == TeamColor.WHITE && endPosition.getRow() == 8) ||
                        (this.color == TeamColor.BLACK && endPosition.getRow() == 1)) {
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                    } else {
                        moveList.add(new ChessMove(myPosition, endPosition));
                    }
                }
            }
        } else {
            // Check SW attack
            // Check bounds
            if ((col - 1 >= 1) && (row - 1 >= 1)) {
                // Check if opposite color piece occupies space
                ChessPosition endPosition = new ChessPosition(row - 1, col - 1);
                if (board.getPiece(endPosition) != null && 
                    board.getPiece(endPosition).getTeamColor() == TeamColor.WHITE) {
                    // Check for promotion
                    if ((this.color == TeamColor.WHITE && endPosition.getRow() == 8) ||
                        (this.color == TeamColor.BLACK && endPosition.getRow() == 1)) {
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                    } else {
                        moveList.add(new ChessMove(myPosition, endPosition));
                    }
                }
            }

            // Check SE attack
            // Check bounds
            if ((col + 1 <= 8) && (row - 1 >= 1)) {
                // Check if opposite color piece occupies space
                ChessPosition endPosition = new ChessPosition(row - 1, col + 1);
                if (board.getPiece(endPosition) != null && 
                    board.getPiece(endPosition).getTeamColor() == TeamColor.WHITE) {
                    // Check for promotion
                    if ((this.color == TeamColor.WHITE && endPosition.getRow() == 8) ||
                        (this.color == TeamColor.BLACK && endPosition.getRow() == 1)) {
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                        moveList.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                    } else {
                        moveList.add(new ChessMove(myPosition, endPosition));
                    }
                }
            }
        }

        return moveList;
    }
}
