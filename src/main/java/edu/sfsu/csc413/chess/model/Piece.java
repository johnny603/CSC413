package edu.sfsu.csc413.chess.model;

public class Piece {
    // remember color first, subclasses depend on order
    private final Color color;
    private final PieceType type;

    // constructor
    public Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
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