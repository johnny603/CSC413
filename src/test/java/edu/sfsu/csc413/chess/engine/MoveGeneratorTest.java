package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the generation loop in its new home.
 *
 * <p>At M4 these say nothing about king safety; that is M5, and this file
 * grows then. What they check is that the loop moved without changing, and
 * that {@code Game} and {@code MoveGenerator} agree.
 */
class MoveGeneratorTest {

    private static Set<String> notations(List<Move> moves) {
        return moves.stream().map(Move::toString).collect(Collectors.toSet());
    }

    @Test
    @DisplayName("the opening position has exactly twenty legal moves")
    void openingHasTwentyMoves() {
        Board board = BoardFactory.standard();
        assertEquals(20, MoveGenerator.legalMoves(board, Color.WHITE).size(),
                "sixteen pawn moves and four knight moves");
    }

    @Test
    @DisplayName("black has twenty at the start as well")
    void blackAlsoHasTwentyAtStart() {
        assertEquals(20, MoveGenerator.legalMoves(BoardFactory.standard(), Color.BLACK).size());
    }

    @Test
    @DisplayName("an empty board has no moves for either side")
    void emptyBoardHasNoMoves() {
        Board board = new Board();
        assertTrue(MoveGenerator.legalMoves(board, Color.WHITE).isEmpty());
        assertTrue(MoveGenerator.legalMoves(board, Color.BLACK).isEmpty());
    }

    @Test
    @DisplayName("generates moves for the given colour only")
    void asksOnlyTheGivenColour() {
        // White: rook a1, king e1. Black: rook h8, king e8. Nothing else.
        Board board = BoardFactory.fromFen("4k2r/8/8/8/8/8/8/R3K3");

        Set<String> white = notations(MoveGenerator.legalMoves(board, Color.WHITE));
        Set<String> black = notations(MoveGenerator.legalMoves(board, Color.BLACK));

        // Rook: seven up the file, three along the rank. King: five squares.
        assertEquals(15, white.size(), "white's moves: " + white);
        // Rook: seven down the file, two along the rank. King: five squares.
        assertEquals(14, black.size(), "black's moves: " + black);
        assertTrue(white.stream().noneMatch(m -> m.startsWith("h8") || m.startsWith("e8")),
                "a white move that starts on a black piece: " + white);
    }

    @Test
    @DisplayName("this week, legal and pseudo-legal are the same list")
    void legalIsPseudoLegalUntilM5() {
        Board board = BoardFactory.standard();
        assertEquals(notations(MoveGenerator.pseudoLegalMoves(board, Color.WHITE)),
                notations(MoveGenerator.legalMoves(board, Color.WHITE)));
    }

    @Test
    @DisplayName("Game and MoveGenerator agree, before and after a move")
    void gameAgreesWithTheGenerator() {
        Game game = new Game();
        assertEquals(notations(MoveGenerator.legalMoves(game.board(), Color.WHITE)),
                notations(game.legalMoves()));

        game.play(game.findLegalMove("e2e4").orElseThrow());

        assertEquals(notations(MoveGenerator.legalMoves(game.board(), Color.BLACK)),
                notations(game.legalMoves()));
    }
}
