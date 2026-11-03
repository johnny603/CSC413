package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.PieceType;

/**
 * Anything that can display a game to a player and collect their decisions.
 *
 * <p>The interface exists so that {@link GameController} depends on the
 * <em>idea</em> of a display rather than on the console specifically. We name
 * this properly in Week 11 as the V in MVC.
 *
 * <p><strong>The one subtle method is {@link #start(GameController)}.</strong>
 * A console view and a window disagree about who is in charge. A console is
 * happy to be asked "what now?" over and over in a loop — it will simply wait
 * for someone to type. A window is not: it sits there doing nothing until the
 * user clicks, and the click arrives on a thread we do not control. Forcing
 * both into one shape is what breaks graphical views. So instead the view is
 * handed the controller and told "go", and each view runs the game the way its
 * own input naturally works:
 *
 * <ul>
 *   <li>a view that can wait for input (console, file, network) calls
 *       {@link GameController#runLoop()} and lets the controller drive;
 *   <li>a view that reacts to events (Swing, web) sets itself up, arranges for
 *       user actions to reach {@link GameController#handle}, and returns
 *       immediately.
 * </ul>
 *
 * <p>Said in one line: <em>a console view is happy to be asked; a window has to
 * be told.</em>
 */
public interface BoardView {

    /** Draws the current position. Called by the controller after every change. */
    void render(Game game);

    /** Shows a message to the player — an error, a prompt, a result. */
    void showMessage(String message);

    /**
     * Hands control to the view, which then runs the game using whatever input
     * style it has. Returns when the game is over or the player has quit.
     */
    void start(GameController controller);

    /**
     * Asks what the player wants to do next.
     *
     * <p>Only views that drive from {@link GameController#runLoop()} need this.
     * An event-driven view never has it called, and inherits this default.
     * Throwing rather than returning something harmless is deliberate: if a
     * Swing view ever reaches this line, there is a bug we want to see
     * immediately, rather than a window that silently freezes.
     */
    default PlayerAction nextAction(Game game) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " is event-driven, so it does not"
                        + " answer nextAction(). Did it call runLoop() by mistake?");
    }

    /**
     * Asks what a promoting pawn should become.
     *
     * <p>The default answers "queen", which is the right choice in the
     * overwhelming majority of real games. A view only overrides this if it
     * wants to offer the choice — a console gets it from the notation instead
     * ({@code e7e8q}), so it leaves this alone.
     */
    default PieceType promotionChoice(Game game) {
        return PieceType.QUEEN;
    }

    /** Releases whatever the view holds — a window, a stream, a socket. */
    default void close() {
    }
}
