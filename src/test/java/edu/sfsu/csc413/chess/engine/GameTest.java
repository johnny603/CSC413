package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the game: turn order, history, and undo. */
class GameTest {

    /** Plays a sequence of moves given in algebraic notation. */
    private void play(Game game, String... notations) {
        for (String notation : notations) {
            Move move = game.findLegalMove(notation).orElseThrow(
                    () -> new AssertionError("Expected " + notation + " to be legal"));
            game.play(move);
        }
    }

    @Test
    @DisplayName("white has twenty moves at the start")
    void twentyMovesAtStart() {
        // Sixteen pawn moves (one and two squares for each of eight pawns)
        // and four knight moves. Nothing else can move through its own pawns.
        assertEquals(20, new Game().legalMoves().size());
    }

    @Test
    @DisplayName("white moves first, and the turn alternates")
    void turnsAlternate() {
        Game game = new Game();
        assertEquals(Color.WHITE, game.sideToMove());

        play(game, "e2e4");
        assertEquals(Color.BLACK, game.sideToMove());

        play(game, "e7e5");
        assertEquals(Color.WHITE, game.sideToMove());
    }

    @Test
    @DisplayName("playing a move moves the piece on the board")
    void playMovesThePiece() {
        Game game = new Game();
        play(game, "e2e4");

        assertTrue(game.board().isEmpty(Position.parse("e2")));
        assertEquals(PieceType.PAWN, game.board().pieceAt(Position.parse("e4")).type());
    }

    @Test
    @DisplayName("rejects a move that is not legal")
    void rejectsIllegalMove() {
        Game game = new Game();
        // e2e5 is three squares — no pawn does that.
        assertTrue(game.findLegalMove("e2e5").isEmpty());

        Move fabricated = Move.quiet(Position.parse("e2"), Position.parse("e5"),
                game.board().pieceAt(Position.parse("e2")));
        assertThrows(IllegalArgumentException.class, () -> game.play(fabricated));
    }

    @Test
    @DisplayName("undo restores the position, the turn, and the history")
    void undoRestoresEverything() {
        Game game = new Game();
        play(game, "e2e4", "e7e5");
        assertEquals(2, game.history().size());

        Optional<Move> undone = game.undoLastMove();

        assertTrue(undone.isPresent());
        assertEquals("e7e5", undone.get().toString());
        assertEquals(Color.BLACK, game.sideToMove(), "the turn goes back to black");
        assertEquals(1, game.history().size());
        assertEquals(PieceType.PAWN, game.board().pieceAt(Position.parse("e7")).type());
        assertTrue(game.board().isEmpty(Position.parse("e5")));
    }

    @Test
    @DisplayName("undo restores a captured piece")
    void undoRestoresCapture() {
        Game game = new Game();
        play(game, "e2e4", "d7d5", "e4d5");   // white pawn takes on d5

        assertEquals(Color.WHITE, game.board().pieceAt(Position.parse("d5")).color());

        game.undoLastMove();

        assertEquals(Color.BLACK, game.board().pieceAt(Position.parse("d5")).color(),
                "the captured black pawn is back on d5");
        assertEquals(PieceType.PAWN, game.board().pieceAt(Position.parse("e4")).type());
    }

    @Test
    @DisplayName("undo on a fresh game reports that there is nothing to undo")
    void undoWithNoHistory() {
        assertTrue(new Game().undoLastMove().isEmpty());
    }

    @Test
    @DisplayName("a game can start from an arbitrary position")
    void startFromFen() {
        Game game = new Game(BoardFactory.fromFen("4k3/8/8/8/8/8/4P3/4K3"), Color.WHITE);

        // Four king steps (e2 is blocked by its own pawn) plus the pawn's two
        // advances. The black king on e8 is too far away to matter.
        assertEquals(Set.of("e1d1", "e1d2", "e1f1", "e1f2", "e2e3", "e2e4"),
                game.legalMoves().stream().map(Move::toString)
                        .collect(Collectors.toSet()));
    }
}
