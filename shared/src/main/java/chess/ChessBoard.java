package chess;

import java.lang.StringBuilder;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] layout = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int x = position.getRow() - 1;
        int y = position.getColumn() - 1;
        this.layout[x][y] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int x = position.getRow() - 1;
        int y = position.getColumn() - 1;
        ChessPiece pieceAtPosition = this.layout[x][y];
        return pieceAtPosition;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece[][] newBoard = new ChessPiece[8][8];
        TeamColor color = TeamColor.BLACK;
        // Row 8 - r n b q k b n r
        newBoard[7][0] = new ChessPiece(color, PieceType.ROOK);
        newBoard[7][1] = new ChessPiece(color, PieceType.KNIGHT);
        newBoard[7][2] = new ChessPiece(color, PieceType.BISHOP);
        newBoard[7][3] = new ChessPiece(color, PieceType.QUEEN);
        newBoard[7][4] = new ChessPiece(color, PieceType.KING);
        newBoard[7][5] = new ChessPiece(color, PieceType.BISHOP);
        newBoard[7][6] = new ChessPiece(color, PieceType.KNIGHT);
        newBoard[7][7] = new ChessPiece(color, PieceType.ROOK);

        // Row 7 - p p p p p p p p
        for (int i = 0; i < 8; i++) {
            newBoard[6][i] = new ChessPiece(color, PieceType.PAWN);
        }

        // Row 2 - P P P P P P P P
        color = TeamColor.WHITE;
        for (int i = 0; i < 8; i++) {
            newBoard[1][i] = new ChessPiece(color, PieceType.PAWN);
        }

        // Row 1 - R N B Q K B N R
        newBoard[0][0] = new ChessPiece(color, PieceType.ROOK);
        newBoard[0][1] = new ChessPiece(color, PieceType.KNIGHT);
        newBoard[0][2] = new ChessPiece(color, PieceType.BISHOP);
        newBoard[0][3] = new ChessPiece(color, PieceType.QUEEN);
        newBoard[0][4] = new ChessPiece(color, PieceType.KING);
        newBoard[0][5] = new ChessPiece(color, PieceType.BISHOP);
        newBoard[0][6] = new ChessPiece(color, PieceType.KNIGHT);
        newBoard[0][7] = new ChessPiece(color, PieceType.ROOK);

        // Set instance variable
        this.layout = newBoard;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                output.append("|");
                ChessPiece currentPiece = this.layout[i][j];
                if (currentPiece == null) {
                    output.append(" ");
                    continue;
                }
                switch (currentPiece.getPieceType()) {
                    case PieceType.KING:
                        output.append("k");
                        break;
                    case PieceType.QUEEN:
                        output.append("q");
                        break;
                    case PieceType.BISHOP:
                        output.append("b");
                        break;
                    case PieceType.KNIGHT:
                        output.append("n");
                        break;
                    case PieceType.ROOK:
                        output.append("r");
                        break;
                    case PieceType.PAWN:
                        output.append("p");
                        break;
                    default:
                        throw new RuntimeException("Invalid piece");
                }
            }
            output.append("|\n");
        }
        return output.toString();
    }
}
