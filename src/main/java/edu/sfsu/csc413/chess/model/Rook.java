package edu.sfsu.csc413.chess.model;

import java.util.List;

/**
 * The rook: slides any distance along ranks and files.
 */
public class Rook extends Piece {

    /**
     * The four straight directions, as {file, rank} deltas.
     */
    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public Rook(Color color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        // Rook can "slide" in 4 directions listed
        return slidingMoves(board, from, DIRECTIONS);
    }
}
