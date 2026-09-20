package edu.sfsu.csc413.chess.model;
import java.util.ArrayList;
import java.util.List;

// A board HAS 64 tiles [8 * 8]
public class Board {
    private final Piece[][] squares =
            new Piece[Position.BOARD_SIZE][Position.BOARD_SIZE];

    // An empty board based on BoardTest
    public Board() {
    }

    // What is here? null if nothing
    public Piece pieceAt(Position position) {
        return squares[position.file()][position.rank()];
    }

    // Convenience
    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    // Put this here (null clears)
    public void place(Position position, Piece piece) {
        squares[position.file()][position.rank()] = piece;
    }

    // Where are all of the specified color's pieces?
    public List<Position> positionsOf(Color color) {
        List<Position> positions = new ArrayList<>();

        for (int file = 0; file < Position.BOARD_SIZE; file++) {
            for (int rank = 0; rank < Position.BOARD_SIZE; rank++) {
                Piece piece = squares[file][rank];
                // avoid nested if statements
                if (piece != null && piece.color() == color) {
                    positions.add(new Position(file, rank));
                }
            }
        }

        return positions;
    }

    // One-line dump, for debugging
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int emptyCount = 0;

        // FEN is rank, file
        for (int rank = Position.BOARD_SIZE - 1; rank >= 0; rank--) {
            for (int file = 0; file < Position.BOARD_SIZE; file++) {
                Piece piece = squares[file][rank];

                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        sb.append(emptyCount);
                        emptyCount = 0;
                    }

                    sb.append(piece.symbol());
                }
            }

            // Handle empty squares at the end of the rank
            if (emptyCount > 0) {
                sb.append(emptyCount);
                emptyCount = 0;
            }

            // Separate ranks
            if (rank > 0) {
                sb.append("/");
            }
        }

        return sb.toString();
    }
}