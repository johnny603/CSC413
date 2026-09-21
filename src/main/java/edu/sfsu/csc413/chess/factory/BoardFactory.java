package edu.sfsu.csc413.chess.factory;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Position;

/**
 * Builds boards: the standard opening position, or any position described in
 * FEN notation.
 *
 * <p>Keeping setup here rather than in {@link Board}'s constructor means the
 * board class stays focused on being a grid, and it means tests can construct
 * whatever position they need to exercise a rule. That second point matters
 * more than it sounds: the ability to write "here is a position, assert what
 * happens next" is what makes the rules testable at all.
 */
public final class BoardFactory {

    /** FEN for the standard starting position. */
    public static final String START_FEN =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private BoardFactory() {
    }

    /** A board set up for a new game. */
    public static Board standard() {
        return fromFen(START_FEN);
    }

    /** An empty board, for tests that place only the pieces they care about. */
    public static Board empty() {
        return new Board();
    }

    /**
     * Builds a board from FEN (Forsyth-Edwards Notation).
     *
     * <p>FEN describes a position as eight rank descriptions separated by
     * {@code /}, starting from rank 8. A letter is a piece; a digit is that
     * many empty squares. Only this first field is read here — the side to move
     * and castling rights belong to {@code Game}, not to the board.
     *
     * @throws IllegalArgumentException if the FEN is malformed
     */
    public static Board fromFen(String fen) {
        if (fen == null || fen.isBlank()) {
            throw new IllegalArgumentException("FEN is empty");
        }
        String placement = fen.trim().split("\\s+")[0];
        String[] ranks = placement.split("/");
        if (ranks.length != Position.BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "FEN needs " + Position.BOARD_SIZE + " ranks, got " + ranks.length);
        }

        Board board = new Board();
        for (int i = 0; i < ranks.length; i++) {
            // FEN lists rank 8 first, but our rank 0 is rank 1, so we count down.
            int rank = Position.BOARD_SIZE - 1 - i;
            int file = 0;
            for (char symbol : ranks[i].toCharArray()) {
                if (Character.isDigit(symbol)) {
                    file += symbol - '0';
                } else {
                    if (file >= Position.BOARD_SIZE) {
                        throw new IllegalArgumentException(
                                "Too many squares in FEN rank: " + ranks[i]);
                    }
                    board.place(new Position(file, rank), PieceFactory.fromSymbol(symbol));
                    file++;
                }
            }
            if (file != Position.BOARD_SIZE) {
                throw new IllegalArgumentException(
                        "FEN rank does not fill 8 squares: " + ranks[i]);
            }
        }
        return board;
    }
}
