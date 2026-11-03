package edu.sfsu.csc413.chess.engine;

/**
 * How a game currently stands.
 *
 * <p>The distinction that trips students up: <em>check</em> is not a terminal
 * state — the game continues — whereas checkmate and stalemate are. And the
 * only difference between those two is whether the side to move is in check:
 * both mean "no legal moves", but one is a loss and the other a draw.
 */
public enum GameStatus {
    /** Play continues normally. */
    IN_PROGRESS,
    /** The side to move is in check but has at least one legal reply. */
    CHECK,
    /** The side to move is in check and has no legal move. They lose. */
    CHECKMATE,
    /** The side to move is not in check but has no legal move. Draw. */
    STALEMATE;

    /** True when the game has ended. */
    public boolean isGameOver() {
        return this == CHECKMATE || this == STALEMATE;
    }
}
