package edu.sfsu.csc413.chess.model;

import java.util.List;

/**
 * The knight: two squares along one axis, one along the other, jumping over
 * anything in between.
 *
 * <p>The knight is the clearest illustration of why this design works. Its rule
 * is unlike any other piece's — it is the only one that ignores blocking
 * pieces — and yet it needs no special case anywhere else in the engine. It
 * simply answers the question every piece is asked.
 */
public class Knight extends Piece {

    /**
     * The eight L-shapes, as {file, rank} deltas.
     */
    private static final int[][] OFFSETS = { { 1, 2 }, { 2, 1 }, { 2, -1 }, { 1, -2 }, { -1, -2 }, { -2, -1 }, { -2, 1 }, { -1, 2 } };

    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        throw new UnsupportedOperationException("M2: implement Knight.pseudoLegalMoves");
    }
}
