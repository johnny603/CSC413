package edu.sfsu.csc413.chess.model;

// record header
public record Position(int file, int rank) {
    /** Files and ranks both run 0..7. */
    public static final int BOARD_SIZE = 8;

    /** True when these raw coordinates name a real square. */
    public static boolean isOnBoard(int file, int rank) {
        return file >= 0 && file < BOARD_SIZE && rank >= 0 && rank < BOARD_SIZE;
    }

    // fixed contract signatures for parse and offsetOrNull
    public static Position parse(String algebraic) {
        // Check length
        if (algebraic.length() != 2) {
            throw new IllegalArgumentException("String must be of length 2");
        }
        // Check format
        if (!Character.isDigit(algebraic.charAt(1))) {
            throw new IllegalArgumentException("Second character must be a digit");
        }
        if (!Character.isLowerCase(algebraic.charAt(0))) {
            throw new IllegalArgumentException("First character must be a lower case letter");
        }
        // Parse to zero based coordinates
        // Java represents characters as numeric Unicode values
        int parsedFile = algebraic.charAt(0) - 'a';
        int parsedRank = algebraic.charAt(1) - '1';
        return new Position(parsedFile, parsedRank);
    }


    public Position offsetOrNull(int fileDelta, int rankDelta) {
        // Use guard clauses for fail fast
        if (!(fileDelta >= 0 && fileDelta < BOARD_SIZE)) {
            return null;
        }
        if (!(rankDelta >= 0 && rankDelta < BOARD_SIZE)) {
            return null;
        }
        return new Position(fileDelta, rankDelta);
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



