package edu.sfsu.csc413.chess.model;

// record header
public record Position(int file, int rank) {
    /** Files and ranks both run 0..7. */
    public static final int BOARD_SIZE = 8;

    /** True when these raw coordinates name a real square. */
    public static boolean isOnBoard(int file, int rank) {
        return file >= 0 && file < BOARD_SIZE && rank >= 0 && rank < BOARD_SIZE;
    }

    // fixed contract signatures
    public static Position parse(String algebraic) {
        throw new UnsupportedOperationException("M0b: your turn");
    }

    public Position offsetOrNull(int fileDelta, int rankDelta) {
        throw new UnsupportedOperationException("M0b: your turn");
    }

    // compact constructor
    public Position {
        if (!isOnBoard(file, rank)) {
            throw new IllegalArgumentException(
                    "Position off board: file=" + file + ", rank=" + rank);
        }
    }

    // toString
    @Override
    public String toString() {
        return "" + (char) ('a' + file) + (char) ('1' + rank);
    }
}



