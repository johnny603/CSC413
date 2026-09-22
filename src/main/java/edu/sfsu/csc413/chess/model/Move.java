package edu.sfsu.csc413.chess.model;

/**
 * A single move: which piece went where, and what happened as a result.
 *
 * <p>A move records enough information to be <em>undone</em>. That is why
 * {@code captured} is stored here rather than being recomputed later — once the
 * move has been applied, the board no longer knows what used to stand on the
 * destination square. In Week 10 this record becomes the payload of the Command
 * pattern, and {@code captured} is exactly what makes {@code undo()} possible.
 *
 * <p>Fields other than {@code from}, {@code to}, and {@code moved} are optional:
 * {@code captured} and {@code promotesTo} are {@code null} when they do not
 * apply. Use the static factory methods below rather than the canonical
 * constructor; they read better at the call site and document intent.
 *
 * @param from        the square the piece left
 * @param to          the square the piece arrived on
 * @param moved       the piece that moved
 * @param captured    the piece removed by this move, or null for a quiet move
 * @param promotesTo  the type a pawn became, or null if this is not a promotion
 */

// records are basically immutable data carriers
public record Move(Position from, Position to, Piece moved, Piece captured, PieceType promotesTo) {

    /**
     * A move to an empty square.
     */
    public static Move quiet(Position from, Position to, Piece moved) {
        return new Move(from, to, moved, null, null);
    }

    /**
     * A move that removes an enemy piece from the destination square.
     */
    public static Move capture(Position from, Position to, Piece moved, Piece captured) {
        return new Move(from, to, moved, captured, null);
    }

    /**
     * A pawn reaching the far rank and becoming {@code promotesTo}.
     */
    public static Move promotion(Position from, Position to, Piece moved, Piece captured, PieceType promotesTo) {
        return new Move(from, to, moved, captured, promotesTo);
    }

    public boolean isCapture() {
        return captured != null;
    }

    public boolean isPromotion() {
        return promotesTo != null;
    }

    /**
     * This move in the long algebraic notation the console UI reads and writes,
     * e.g. {@code "e2e4"} or {@code "e7e8q"} for a promotion.
     */
    @Override
    public String toString() {
        // where it left + where it landed
        // consults the position method
        String algebraic = from.toString() + to.toString();
        if (isPromotion()) {
            algebraic += Character.toLowerCase(promotesTo.symbol());
        }
        return algebraic;
    }
}
