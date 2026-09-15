package edu.sfsu.csc413.chess.model;

// A board HAS 64 tiles [8 * 8]
public class Board {
    private final Piece[][] squares =
            new Piece[Position.BOARD_SIZE][Position.BOARD_SIZE];


    // an empty board
    public Board() {
        return 0;
    }

    // what is here? null if nothing
    public Piece pieceAt(Position position) {
        return null;
    }

    // convenience
    public boolean isEmpty(Position position) {
        return false;
    }


    // put this here (null clears)
    public void place(Position position, Piece piece) {
        return null;
    }


    // where are all of white's pieces?
    public List<Position> positionsOf(Color color) {
        return null;
    }


    // one-line dump, for debugging
    @Override public String toString() {
        return null;
    }

}