package edu.sfsu.csc413.chess.model;

import java.util.List;

/**
 * The bishop: slides any distance along the four diagonals.
 */
public class Bishop extends Piece {

    /**
     * The four diagonal directions, as {file, rank} deltas.
     */
    private static final int[][] DIRECTIONS = { { 1, 1 }, { 1, -1 }, { -1, -1 }, { -1, 1 } };

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        throw new UnsupportedOperationException("M2: implement Bishop.pseudoLegalMoves");
    }
}
