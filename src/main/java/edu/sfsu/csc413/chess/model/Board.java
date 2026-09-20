package edu.sfsu.csc413.chess.model;
import java.util.ArrayList;
import java.util.List;


// A board HAS 64 tiles [8 * 8]
public class Board {
    private final Piece[][] squares =
            new Piece[Position.BOARD_SIZE][Position.BOARD_SIZE];


    // an empty board based on BoardTest
    public Board() {
        for (int file = 0; file < Position.BOARD_SIZE; file++) {
            for (int rank = 0; rank < Position.BOARD_SIZE; rank++) {
                squares[file][rank] = null;
            }
        }
    }

    // what is here? null if nothing
    public Piece pieceAt(Position position) {
        // map the piece to the square
        int file = position.file();
        int rank = position.rank();
        return squares[file][rank];
    }

    // convenience
    public boolean isEmpty(Position position) {
        // relate to pieceAt and check if the square has nothing in it
        int file = position.file();
        int rank = position.rank();
        return squares[file][rank] == null;
    }


    // put this here (null clears)
    public void place(Position position, Piece piece) {
        // store the piece in the array
        int file = position.file();
        int rank = position.rank();
        squares[file][rank] = piece;
    }


    // where are all of white's pieces?
    public List<Position> positionsOf(Color color) {
        List<Position> positions = new ArrayList<>();
        for (int file = 0; file < Position.BOARD_SIZE; file++) {
            for (int rank = 0; rank < Position.BOARD_SIZE; rank++) {
                Piece piece = squares[file][rank];
                if (piece != null) {
                    // Piece.color is private
                    if (piece.color() == color) {
                        Position newPosition = new Position(file, rank);
                        positions.add(newPosition);
                    }
                }
            }
        }
        return positions;
    }


    // one-line dump, for debugging
    @Override public String toString() {
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