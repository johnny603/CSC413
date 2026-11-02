package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the controller drives a game correctly — through a view that is
 * not the console.
 *
 * <p>This class is the proof that the view seam is real. {@link RecordingView}
 * is a complete {@link BoardView} that never prints anything and never reads a
 * keyboard, and the controller works with it unchanged. If a graphical view can
 * be substituted here, it can be substituted in {@code Main} too.
 */
class GameControllerTest {

    /** A view that replays a script of actions and remembers what it was told. */
    private static class RecordingView implements BoardView {
        private final Deque<PlayerAction> script = new ArrayDeque<>();
        final List<String> messages = new ArrayList<>();
        int renderCount;
        boolean closed;

        RecordingView(String... typed) {
            for (String line : typed) {
                script.add(PlayerAction.parse(line));
            }
        }

        @Override
        public void render(Game game) {
            renderCount++;
        }

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void start(GameController controller) {
            controller.runLoop();
        }

        @Override
        public PlayerAction nextAction(Game game) {
            // Running out of script means the player walked away.
            return script.isEmpty() ? new PlayerAction.Quit() : script.poll();
        }

        @Override
        public void close() {
            closed = true;
        }

        /** True if any message contains the given text. */
        boolean said(String fragment) {
            return messages.stream().anyMatch(m -> m.contains(fragment));
        }
    }

    @Test
    @DisplayName("drives a full game to checkmate without any console involved")
    void playsFoolsMate() {
        Game game = new Game();
        RecordingView view = new RecordingView("f2f3", "e7e5", "g2g4", "d8h4");

        new GameController(game, view).start();

        assertEquals(GameStatus.CHECKMATE, game.status());
        assertTrue(view.said("Checkmate"), view.messages.toString());
        assertTrue(view.said("WHITE wins") || view.said("BLACK wins"),
                "announces a winner: " + view.messages);
        assertTrue(view.closed, "the view is closed when the game ends");
    }

    @Test
    @DisplayName("names the winner correctly — black mates white in Fool's Mate")
    void namesTheWinner() {
        RecordingView view = new RecordingView("f2f3", "e7e5", "g2g4", "d8h4");
        new GameController(new Game(), view).start();

        assertTrue(view.said("Checkmate. BLACK wins."), view.messages.toString());
    }

    @Test
    @DisplayName("reports an illegal move without ending the game")
    void reportsIllegalMove() {
        Game game = new Game();
        RecordingView view = new RecordingView("e2e5", "quit");

        new GameController(game, view).start();

        assertTrue(view.said("Not a legal move: 'e2e5'"), view.messages.toString());
        assertTrue(game.history().isEmpty(), "nothing was played");
    }

    @Test
    @DisplayName("an Undo action takes back a move — no typing required")
    void undoTakesBackAMove() {
        Game game = new Game();
        RecordingView view = new RecordingView("e2e4", "undo", "quit");

        new GameController(game, view).start();

        assertTrue(game.history().isEmpty(), "the move was taken back");
    }

    @Test
    @DisplayName("undo with nothing to undo says so")
    void undoOnEmptyHistory() {
        RecordingView view = new RecordingView("undo", "quit");
        new GameController(new Game(), view).start();

        assertTrue(view.said("Nothing to undo."), view.messages.toString());
    }

    @Test
    @DisplayName("a button-built action works the same as a typed one")
    void actionsNeedNotComeFromText() {
        Game game = new Game();
        RecordingView view = new RecordingView();   // empty script
        GameController controller = new GameController(game, view);

        // This is exactly what a Swing click handler does: build the action
        // directly, with no string anywhere. No controller change was needed.
        controller.handle(new PlayerAction.Move("e2e4"));

        assertEquals(1, game.history().size());
        assertEquals("e2e4", game.history().get(0).toString());
    }

    @Test
    @DisplayName("quitting stops the game and closes the view")
    void quitStops() {
        Game game = new Game();
        RecordingView view = new RecordingView("quit");

        new GameController(game, view).start();

        assertTrue(view.said("Goodbye."), view.messages.toString());
        assertTrue(view.closed);
        assertFalse(game.isGameOver(), "quitting is not the same as losing");
    }

    @Test
    @DisplayName("the view is redrawn after each action")
    void redrawsAfterEachAction() {
        RecordingView view = new RecordingView("e2e4", "e7e5", "quit");
        new GameController(new Game(), view).start();

        // One opening render, then one per move. Quit returns before redrawing.
        assertEquals(3, view.renderCount, "opening + two moves");
    }

    @Test
    @DisplayName("a promoting move typed without its letter quietly becomes a queen")
    void promotionDefaultsToQueen() {
        // White pawn on e7 with the promotion square free. The console never
        // overrides promotionChoice(), so it inherits the "queen" default and
        // behaves exactly as it did before the seam existed.
        Game game = new Game(BoardFactory.fromFen("k7/4P3/8/8/8/8/8/4K3"), Color.WHITE);
        RecordingView view = new RecordingView();

        new GameController(game, view).handle(new PlayerAction.Move("e7e8"));

        assertEquals(PieceType.QUEEN,
                game.board().pieceAt(Position.parse("e8")).type());
        assertEquals("e7e8q", game.history().get(0).toString());
    }

    @Test
    @DisplayName("a view that offers a choice gets the piece it asked for")
    void promotionChoiceIsHonoured() {
        Game game = new Game(BoardFactory.fromFen("k7/4P3/8/8/8/8/8/4K3"), Color.WHITE);

        // What a Swing view does with a dialog — here, always underpromote.
        RecordingView view = new RecordingView() {
            @Override
            public PieceType promotionChoice(Game g) {
                return PieceType.KNIGHT;
            }
        };

        new GameController(game, view).handle(new PlayerAction.Move("e7e8"));

        assertEquals(PieceType.KNIGHT,
                game.board().pieceAt(Position.parse("e8")).type());
    }

    @Test
    @DisplayName("the explicit notation still wins over the default")
    void explicitPromotionLetterStillWorks() {
        Game game = new Game(BoardFactory.fromFen("k7/4P3/8/8/8/8/8/4K3"), Color.WHITE);
        RecordingView view = new RecordingView();

        new GameController(game, view).handle(new PlayerAction.Move("e7e8r"));

        assertEquals(PieceType.ROOK,
                game.board().pieceAt(Position.parse("e8")).type());
    }
}
