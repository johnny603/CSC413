package edu.sfsu.csc413.chess.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A piece knows what it is — its color and its type — and how to print itself.
 */
class PieceTest {

    /** The one line in this file that knows how a piece is constructed. */
    private static Piece piece(Color color, PieceType type) {
        return new Piece(color, type);
    }

    @Test
    @DisplayName("a white piece prints as its uppercase letter")
    void whiteSymbolIsUppercase() {
        assertEquals('K', piece(Color.WHITE, PieceType.KING).symbol());
        assertEquals('N', piece(Color.WHITE, PieceType.KNIGHT).symbol());
    }

    @Test
    @DisplayName("a black piece prints as its lowercase letter")
    void blackSymbolIsLowercase() {
        assertEquals('k', piece(Color.BLACK, PieceType.KING).symbol());
        assertEquals('p', piece(Color.BLACK, PieceType.PAWN).symbol());
    }

    @Test
    @DisplayName("a piece knows its color and type, and toString is its symbol")
    void accessorsAndToString() {
        Piece rook = piece(Color.BLACK, PieceType.ROOK);
        assertEquals(Color.BLACK, rook.color());
        assertEquals(PieceType.ROOK, rook.type());
        assertEquals("r", rook.toString());
    }
}
