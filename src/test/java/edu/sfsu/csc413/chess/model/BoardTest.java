package edu.sfsu.csc413.chess.model;

import edu.sfsu.csc413.chess.factory.PieceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The board stores which piece is on which square. That is the whole
 * specification, and every test here is a consequence of it.
 *
 * <p>At M1 the helper below said {@code new Piece(color, type)}; from M2 on
 * it goes through the factory, because {@code Piece} is abstract.
 */
class BoardTest {

    /** The one line in this file that knows how a piece is constructed. */
    private static Piece piece(Color color, PieceType type) {
        return PieceFactory.create(type, color);
    }

    @Test
    @DisplayName("a new board has nothing on any square")
    void newBoardIsEmpty() {
        Board board = new Board();
        for (int file = 0; file < Position.BOARD_SIZE; file++) {
            for (int rank = 0; rank < Position.BOARD_SIZE; rank++) {
                Position square = new Position(file, rank);
                assertNull(board.pieceAt(square));
                assertTrue(board.isEmpty(square));
            }
        }
    }

    @Test
    @DisplayName("a placed piece is found on that square and no other")
    void placeThenPieceAt() {
        Board board = new Board();
        Piece knight = piece(Color.WHITE, PieceType.KNIGHT);
        board.place(Position.parse("g1"), knight);

        assertSame(knight, board.pieceAt(Position.parse("g1")));
        assertFalse(board.isEmpty(Position.parse("g1")));
        assertNull(board.pieceAt(Position.parse("a7")),
                "g1 is file 6, rank 0 — a7 is what you get with file and rank swapped");
        assertNull(board.pieceAt(Position.parse("g2")));
    }

    @Test
    @DisplayName("placing on an occupied square replaces what was there")
    void placeReplaces() {
        Board board = new Board();
        Position e4 = Position.parse("e4");
        board.place(e4, piece(Color.WHITE, PieceType.PAWN));
        Piece queen = piece(Color.BLACK, PieceType.QUEEN);
        board.place(e4, queen);

        assertSame(queen, board.pieceAt(e4));
    }

    @Test
    @DisplayName("placing null clears the square")
    void placeNullClears() {
        Board board = new Board();
        Position d5 = Position.parse("d5");
        board.place(d5, piece(Color.WHITE, PieceType.BISHOP));
        board.place(d5, null);

        assertTrue(board.isEmpty(d5));
    }

    @Test
    @DisplayName("positionsOf lists exactly the squares holding that color")
    void positionsOfFindsOnlyThatColor() {
        Board board = new Board();
        board.place(Position.parse("e1"), piece(Color.WHITE, PieceType.KING));
        board.place(Position.parse("h1"), piece(Color.WHITE, PieceType.ROOK));
        board.place(Position.parse("e8"), piece(Color.BLACK, PieceType.KING));

        assertEquals(Set.of(Position.parse("e1"), Position.parse("h1")),
                Set.copyOf(board.positionsOf(Color.WHITE)));
        assertEquals(List.of(Position.parse("e8")), board.positionsOf(Color.BLACK));
        assertTrue(new Board().positionsOf(Color.WHITE).isEmpty());
    }

    @Test
    @DisplayName("an empty board's toString is eight empty ranks in FEN")
    void emptyBoardToStringIsEightEights() {
        assertEquals("8/8/8/8/8/8/8/8", new Board().toString());
    }

    @Test
    @DisplayName("toString is the FEN placement field: rank 8 first, digits for empty runs")
    void toStringIsFenPlacement() {
        Board board = new Board();
        board.place(Position.parse("e1"), piece(Color.WHITE, PieceType.KING));
        board.place(Position.parse("a1"), piece(Color.WHITE, PieceType.ROOK));
        board.place(Position.parse("e8"), piece(Color.BLACK, PieceType.KING));
        board.place(Position.parse("c7"), piece(Color.BLACK, PieceType.PAWN));

        assertEquals("4k3/2p5/8/8/8/8/8/R3K3", board.toString());
    }
}
