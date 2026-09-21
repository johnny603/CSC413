package edu.sfsu.csc413.chess.model;

import java.util.List;

/**
 * The king: one square in any of the eight directions.
 *
 * <p>Deliberately incomplete for now. Castling is a king move, but it also
 * moves a rook and depends on history (has either piece moved before?) and on
 * king safety (may not castle out of, through, or into check). None of that
 * belongs in a method whose only input is the current board. Castling arrives
 * in Week 15 as a planned extension — and the fact that it will require almost
 * no change to this class is the open/closed principle paying off.
 */
public class King extends Piece {

    /**
     * The eight surrounding squares, as {file, rank} deltas.
     */
    private static final int[][] OFFSETS = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 } };

    public King(Color color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        throw new UnsupportedOperationException("M2: implement King.pseudoLegalMoves");
    }
}
