package edu.sfsu.csc413.chess.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The six piece types and the letters they print as.
 *
 * <p>The letters are not arbitrary: they are the ones FEN and algebraic
 * notation use, and the FEN parser in a later milestone reads them back.
 */
class PieceTypeTest {

    @Test
    @DisplayName("each type carries its FEN letter, uppercase")
    void symbolsAreFenLetters() {
        assertEquals('P', PieceType.PAWN.symbol());
        assertEquals('N', PieceType.KNIGHT.symbol(), "N, because the king already has K");
        assertEquals('B', PieceType.BISHOP.symbol());
        assertEquals('R', PieceType.ROOK.symbol());
        assertEquals('Q', PieceType.QUEEN.symbol());
        assertEquals('K', PieceType.KING.symbol());
    }

    @Test
    @DisplayName("fromSymbol is the inverse of symbol, in either case")
    void fromSymbolAcceptsEitherCase() {
        for (PieceType type : PieceType.values()) {
            assertEquals(type, PieceType.fromSymbol(type.symbol()));
            assertEquals(type, PieceType.fromSymbol(Character.toLowerCase(type.symbol())));
        }
    }

    @Test
    @DisplayName("fromSymbol rejects a letter that names no piece")
    void fromSymbolRejectsUnknown() {
        assertThrows(IllegalArgumentException.class, () -> PieceType.fromSymbol('X'));
        assertThrows(IllegalArgumentException.class, () -> PieceType.fromSymbol('.'));
    }
}
