package edu.sfsu.csc413.chess.model;
import java.util.List;


public abstract class Piece {
    // remember color first, subclasses depend on order
    private final Color color;
    private final PieceType type;

    // constructor
    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public abstract List<Move> pseudoLegalMoves(Board board, Position from);

    // Pawn would Override this as it attacks differently
    /** True if this piece could capture an enemy standing on {@code target}. */
    public boolean attacks(Board board, Position from, Position target) {
        for (Move move : pseudoLegalMoves(board, from)) {
            if (move.to().equals(target)) {
                return true;
            }
        }
        return false;
    }

    protected List<Move> slidingMoves(Board board, Position from, int[][] directions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected List<Move> steppingMoves(Board board, Position from, int[][] offsets) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // accessors
    public Color color() { return color; }
    public PieceType type() { return type; }
    public char symbol() {
        char letter = type.symbol();
        return color == Color.WHITE
                ? letter : Character.toLowerCase(letter);
    }

    // toString
    @Override
    public String toString() { return String.valueOf(symbol()); }
}

