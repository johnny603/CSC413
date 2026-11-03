package edu.sfsu.csc413.chess.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Tests for turning typed text into player actions.
 *
 * <p>These are worth reading as an example of how much easier a small, focused
 * class is to test than the same logic buried in a loop. Parsing used to happen
 * inside the controller's {@code while}, where reaching it from a test meant
 * faking a console.
 */
class PlayerActionTest {

    @Test
    @DisplayName("recognises the three commands")
    void recognisesCommands() {
        assertInstanceOf(PlayerAction.Undo.class, PlayerAction.parse("undo"));
        assertInstanceOf(PlayerAction.ShowMoves.class, PlayerAction.parse("moves"));
        assertInstanceOf(PlayerAction.Quit.class, PlayerAction.parse("quit"));
    }

    @Test
    @DisplayName("'exit' is another way of saying 'quit'")
    void exitIsQuit() {
        assertInstanceOf(PlayerAction.Quit.class, PlayerAction.parse("exit"));
    }

    @Test
    @DisplayName("commands are case-insensitive and tolerate stray spaces")
    void forgivesCaseAndSpaces() {
        assertInstanceOf(PlayerAction.Undo.class, PlayerAction.parse("UNDO"));
        assertInstanceOf(PlayerAction.Quit.class, PlayerAction.parse("  Quit  "));
    }

    @Test
    @DisplayName("anything else is taken as an attempted move")
    void otherwiseAMove() {
        PlayerAction action = PlayerAction.parse("e2e4");
        assertEquals(new PlayerAction.Move("e2e4"), action);
    }

    @Test
    @DisplayName("a typo becomes a move, and the controller reports it as illegal")
    void typoBecomesAMove() {
        // Deliberate: we do not want a separate "unknown command" concept.
        assertEquals(new PlayerAction.Move("qiut"), PlayerAction.parse("qiut"));
    }

    @Test
    @DisplayName("empty input and null are handled without blowing up")
    void handlesEmptyInput() {
        assertEquals(new PlayerAction.Move(""), PlayerAction.parse(""));
        assertEquals(new PlayerAction.Move(""), PlayerAction.parse(null));
    }
}
