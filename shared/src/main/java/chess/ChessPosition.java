package chess;

import java.lang.StringBuilder;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append((char) (col + 96));
        output.append(row);
        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        // Check if other object is this object
        if (o == this) {
            return true;
        }

        // Check if other object is a ChessPosition
        if (!(o instanceof ChessPosition)) {
            return false;
        }

        // Check if instance variables have the same values
        ChessPosition other = (ChessPosition)o;
        if (this.row == other.getRow() && this.col == other.getColumn()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.row;
        hash = 31 * hash + this.col;
        return hash;
    }
}
