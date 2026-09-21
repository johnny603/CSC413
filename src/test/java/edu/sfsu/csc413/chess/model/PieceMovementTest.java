package edu.sfsu.csc413.chess.model;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that each piece moves the way chess says it does.
 *
 * <p>These tests build the smallest position that exercises the rule in
 * question rather than playing a whole game to reach it. That is the habit
 * worth copying: a test that sets up exactly what it needs tells you precisely
 * what broke when it fails.
 */
class PieceMovementTest {

    /** The destination squares of the given piece, as algebraic strings. */
    private Set<String> destinationsFrom(Board board, String square) {
        Position from = Position.parse(square);
        return board.pieceAt(from).pseudoLegalMoves(board, from).stream()
                .map(move -> move.to().toString())
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("a knight in the centre reaches eight squares")
    void knightInCentre() {
        Board board = BoardFactory.empty();
        board.place(Position.parse("d4"), new Knight(Color.WHITE));

        assertEquals(Set.of("c6", "e6", "f5", "f3", "e2", "c2", "b3", "b5"),
                destinationsFrom(board, "d4"));
    }

    @Test
    @DisplayName("a knight in the corner reaches only two squares")
    void knightInCorner() {
        Board board = BoardFactory.empty();
        board.place(Position.parse("a1"), new Knight(Color.WHITE));

        assertEquals(Set.of("b3", "c2"), destinationsFrom(board, "a1"));
    }

    @Test
    @DisplayName("a knight jumps over pieces that would block a slider")
    void knightJumps() {
        Board board = BoardFactory.standard();
        // The g1 knight is fully hemmed in by its own pieces, yet still moves.
        assertEquals(Set.of("f3", "h3"), destinationsFrom(board, "g1"));
    }

    @Test
    @DisplayName("a rook slides until blocked, capturing an enemy but not a friend")
    void rookBlocking() {
        Board board = BoardFactory.empty();
        board.place(Position.parse("d4"), new Rook(Color.WHITE));
        board.place(Position.parse("d7"), new Pawn(Color.BLACK));   // capturable
        board.place(Position.parse("f4"), new Pawn(Color.WHITE));   // blocks

        Set<String> destinations = destinationsFrom(board, "d4");
        assertTrue(destinations.contains("d7"), "should capture the black pawn");
        assertFalse(destinations.contains("d8"), "must not slide past a capture");
        assertTrue(destinations.contains("e4"), "should reach the square before a friend");
        assertFalse(destinations.contains("f4"), "must not capture its own pawn");
        assertFalse(destinations.contains("g4"), "must not slide past a friend");
    }

    @Test
    @DisplayName("a queen covers the rook's and the bishop's directions together")
    void queenCombinesDirections() {
        Board board = BoardFactory.empty();
        board.place(Position.parse("d4"), new Queen(Color.WHITE));

        // 7 squares on the file + 7 on the rank + 13 on the two diagonals.
        assertEquals(27, destinationsFrom(board, "d4").size());
    }

    @Test
    @DisplayName("a pawn advances one square, or two from its starting rank")
    void pawnAdvance() {
        Board board = BoardFactory.empty();
        board.place(Position.parse("e2"), new Pawn(Color.WHITE));

        assertEquals(Set.of("e3", "e4"), destinationsFrom(board, "e2"));
    }

    @Test
    @DisplayName("a blocked pawn cannot advance at all, and cannot jump the blocker")
    void pawnBlocked() {
        Board board = BoardFactory.empty();
        board.place(Position.parse("e2"), new Pawn(Color.WHITE));
        board.place(Position.parse("e3"), new Pawn(Color.BLACK));

        assertTrue(destinationsFrom(board, "e2").isEmpty(),
                "a pawn may neither capture straight ahead nor jump over the blocker");
    }

    @Test
    @DisplayName("a pawn captures diagonally but not straight ahead")
    void pawnCaptures() {
        Board board = BoardFactory.empty();
        board.place(Position.parse("e4"), new Pawn(Color.WHITE));
        board.place(Position.parse("d5"), new Pawn(Color.BLACK));   // capturable
        board.place(Position.parse("f5"), new Pawn(Color.WHITE));   // own piece

        Set<String> destinations = destinationsFrom(board, "e4");
        assertEquals(Set.of("e5", "d5"), destinations);
    }

    @Test
    @DisplayName("a pawn reaching the last rank offers all four promotions")
    void pawnPromotes() {
        Board board = BoardFactory.empty();
        Position from = Position.parse("e7");
        board.place(from, new Pawn(Color.WHITE));

        List<Move> moves = board.pieceAt(from).pseudoLegalMoves(board, from);
        assertEquals(4, moves.size(), "queen, rook, bishop, knight");
        assertTrue(moves.stream().allMatch(Move::isPromotion));
        assertEquals(Set.of("e7e8q", "e7e8r", "e7e8b", "e7e8n"),
                moves.stream().map(Move::toString).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("a pawn attacks diagonally even where it cannot move")
    void pawnAttacksDifferFromMoves() {
        Board board = BoardFactory.empty();
        Position from = Position.parse("e4");
        Piece pawn = new Pawn(Color.WHITE);
        board.place(from, pawn);

        // It attacks the empty diagonals...
        assertTrue(pawn.attacks(board, from, Position.parse("d5")));
        assertTrue(pawn.attacks(board, from, Position.parse("f5")));
        // ...but not the square directly ahead, which it can move to.
        assertFalse(pawn.attacks(board, from, Position.parse("e5")));
    }
}
