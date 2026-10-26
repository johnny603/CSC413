package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the move generator, now with the rule no single piece can
 * enforce: you may not leave your own king attacked.
 *
 * <p>The first five are M4's, unchanged; the filter must not break them. The
 * rest build positions from FEN, which is far easier to read than twenty
 * lines of {@code board.place(...)}. Each FEN has a comment saying what is
 * on the board.
 */
class MoveGeneratorTest {

    private static Set<String> notations(List<Move> moves) {
        return moves.stream().map(Move::toString).collect(Collectors.toSet());
    }

    private static Set<String> legal(String fen, Color side) {
        return notations(MoveGenerator.legalMoves(BoardFactory.fromFen(fen), side));
    }

    // ------------------------------------------------------------------
    // From M4: the loop still works.
    // ------------------------------------------------------------------

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
        // Neither king is ever exposed, so the filter removes nothing.
        Board board = BoardFactory.fromFen("4k2r/8/8/8/8/8/8/R3K3");

        Set<String> white = notations(MoveGenerator.legalMoves(board, Color.WHITE));
        Set<String> black = notations(MoveGenerator.legalMoves(board, Color.BLACK));

        assertEquals(15, white.size(), "white's moves: " + white);
        assertEquals(14, black.size(), "black's moves: " + black);
        assertTrue(white.stream().noneMatch(m -> m.startsWith("h8") || m.startsWith("e8")),
                "a white move that starts on a black piece: " + white);
    }

    @Test
    @DisplayName("Game and MoveGenerator agree, before and after a move")
    void gameAgreesWithTheGenerator() {
        // Compared against boards built here, not game.board(), so this
        // compiles whether board() returns a Board or a BoardView.
        Game game = new Game();
        assertEquals(legal(BoardFactory.START_FEN, Color.WHITE), notations(game.legalMoves()));

        game.play(game.findLegalMove("e2e4").orElseThrow());

        assertEquals(legal("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", Color.BLACK),
                notations(game.legalMoves()));
    }

    // ------------------------------------------------------------------
    // isAttacked: attacks, not moves.
    // ------------------------------------------------------------------

    @Test
    @DisplayName("a pawn attacks the diagonals ahead of it, not the square in front")
    void pawnAttacksDiagonallyNotForward() {
        // White pawn on e4, both kings far away.
        Board board = BoardFactory.fromFen("k7/8/8/8/4P3/8/8/7K");

        assertTrue(MoveGenerator.isAttacked(board, Position.parse("d5"), Color.WHITE));
        assertTrue(MoveGenerator.isAttacked(board, Position.parse("f5"), Color.WHITE));
        assertFalse(MoveGenerator.isAttacked(board, Position.parse("e5"), Color.WHITE),
                "a pawn moves to e5 but does not attack it");
    }

    @Test
    @DisplayName("a rook does not attack through a piece in its way")
    void blockedLineIsNotAttacked() {
        // White rook a1 behind its own pawn on a2.
        Board board = BoardFactory.fromFen("7k/8/8/8/8/8/P7/R6K");

        assertTrue(MoveGenerator.isAttacked(board, Position.parse("b1"), Color.WHITE));
        assertFalse(MoveGenerator.isAttacked(board, Position.parse("a8"), Color.WHITE));
    }

    // ------------------------------------------------------------------
    // isInCheck.
    // ------------------------------------------------------------------

    @Test
    @DisplayName("detects when a king is in check")
    void detectsCheck() {
        // Black king e8 faces a white rook on e1 down the open e-file.
        Board board = BoardFactory.fromFen("4k3/8/8/8/8/8/8/4R2K");

        assertTrue(MoveGenerator.isInCheck(board, Color.BLACK));
        assertFalse(MoveGenerator.isInCheck(board, Color.WHITE));
    }

    @Test
    @DisplayName("nobody is in check in the opening position")
    void noCheckAtStart() {
        Board board = BoardFactory.standard();
        assertFalse(MoveGenerator.isInCheck(board, Color.WHITE));
        assertFalse(MoveGenerator.isInCheck(board, Color.BLACK));
    }

    @Test
    @DisplayName("a side with no king is never in check")
    void noKingMeansNoCheck() {
        // A lone white rook. Partial positions like this are common in tests.
        Board board = BoardFactory.fromFen("8/8/8/8/8/8/8/R7");

        assertFalse(MoveGenerator.isInCheck(board, Color.BLACK));
        assertFalse(MoveGenerator.isInCheck(board, Color.WHITE));
    }

    // ------------------------------------------------------------------
    // legalMoves: the filter.
    // ------------------------------------------------------------------

    @Test
    @DisplayName("the legal moves are some of the pseudo-legal moves, never more")
    void legalIsASubsetOfPseudoLegal() {
        // The white knight on e2 is pinned to its king on e1 by the rook on e8.
        Board board = BoardFactory.fromFen("4r2k/8/8/8/8/8/4N3/4K3");

        Set<String> pseudo = notations(MoveGenerator.pseudoLegalMoves(board, Color.WHITE));
        Set<String> legal = notations(MoveGenerator.legalMoves(board, Color.WHITE));

        assertTrue(pseudo.containsAll(legal), "legal: " + legal + ", pseudo: " + pseudo);
        assertTrue(legal.size() < pseudo.size(), "the pin should remove the knight's moves");
    }

    @Test
    @DisplayName("a pinned piece may not move out of the pin")
    void pinnedPieceCannotMove() {
        // Same position: knight e2 pinned to king e1 by rook e8.
        Set<String> moves = legal("4r2k/8/8/8/8/8/4N3/4K3", Color.WHITE);
        assertTrue(moves.stream().noneMatch(m -> m.startsWith("e2")),
                "the knight cannot move without exposing its king: " + moves);
    }

    @Test
    @DisplayName("a king may not step onto a square the enemy attacks")
    void kingCannotStepIntoCheck() {
        // The black rook on a2 covers the whole second rank.
        Board board = BoardFactory.fromFen("7k/8/8/8/8/8/r7/4K3");

        Set<String> kingMoves = MoveGenerator.legalMoves(board, Color.WHITE).stream()
                .filter(move -> move.from().equals(Position.parse("e1")))
                .map(move -> move.to().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("d1", "f1"), kingMoves);
    }

    @Test
    @DisplayName("a king may not capture a piece that is defended")
    void kingCannotCaptureDefendedPiece() {
        // White king e1 in check from a black rook on e2, which the black rook
        // on e8 defends. The king must step aside, and only d1 and f1 are safe.
        Set<String> moves = legal("4r2k/8/8/8/8/8/4r3/4K3", Color.WHITE);
        assertEquals(Set.of("e1d1", "e1f1"), moves);
    }

    @Test
    @DisplayName("in check, only moves that answer the check are legal")
    void mustAnswerCheck() {
        // White king e1, black rook e8 checking. The white rook on a2 can block
        // on e2; the king can also step off the file.
        Set<String> moves = legal("4r3/8/8/8/8/8/R7/4K3", Color.WHITE);

        assertTrue(moves.contains("a2e2"), "blocking the check is legal: " + moves);
        assertTrue(moves.contains("e1d1") && moves.contains("e1f1"),
                "stepping off the file is legal: " + moves);
        assertFalse(moves.contains("a2a3"), "a move that ignores the check is not legal: " + moves);
    }

    @Test
    @DisplayName("asking for the legal moves leaves the board as it was")
    void legalMovesLeaveTheBoardAsFound() {
        // A position with captures available to both sides.
        Board board = BoardFactory.fromFen("r3k2r/8/8/3pP3/8/8/8/R3K2R");
        String before = board.toString();

        MoveGenerator.legalMoves(board, Color.WHITE);
        MoveGenerator.legalMoves(board, Color.BLACK);

        assertEquals(before, board.toString(), "every move tried must be taken back");
    }
}
