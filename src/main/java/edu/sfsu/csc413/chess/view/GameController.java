package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;

/**
 * Connects the player's decisions to the game: carry out what was asked, then
 * ask the view to redraw.
 *
 * <p>This is the C in MVC. It is the only class that knows about both the
 * {@link Game} and the {@link BoardView}. It holds no game state of its own:
 * the model owns the position, the view owns the display, and the controller
 * owns only the conversation between them.
 *
 * <p>When you are done, this class mentions no {@code Scanner}, no
 * {@code System.out}, no window, and none of the words "undo", "moves", or
 * "quit". Those belong to the view and to {@link PlayerAction#parse}.
 */
public class GameController {

    private final Game game;
    private final BoardView view;

    public GameController(Game game, BoardView view) {
        this.game = game;
        this.view = view;
    }

    /** The game being played, so a view can read it in order to draw it. */
    public Game game() {
        return game;
    }

    /**
     * Greets the player, draws the opening position, and hands control to the
     * view with {@link BoardView#start}.
     */
    public void start() {
       view.showMessage("Greetings from CSC413!");
       view.render(game);
       view.start(this);
    }

    /**
     * The ask-and-handle loop, for views that can wait for input.
     *
     * <p>Asks the view for the next action and handles it, until the player
     * quits or the game is over. A console view calls this from its
     * {@code start}; an event-driven view never calls it.
     */

    // have a switch, we would want to have the game run first
    private boolean running = true;
    public void runLoop() {
        // while the game is not over, run the game
        while (running && !game.isGameOver()) {
            PlayerAction action = view.nextAction(game);
            handle(action);
        }
    }

    /**
     * Carries out one player action and redraws.
     *
     * <p>Every route into the game goes through here: {@link #runLoop()} calls
     * it, and so will a button's click handler in M7. The handout lists what
     * each of the four actions does and what the player is told.
     */
    public void handle(PlayerAction action) {
        // switch between 4 actions, use a switch expression to avoid fallthrough
        switch (action) {
            case PlayerAction.Move move -> {

            }
            case PlayerAction.Undo undo -> {

            }
            case PlayerAction.ShowMoves showMoves -> {

            }
            case PlayerAction.Quit quit -> {
                // retrieve from runLoop implementation
                running = false;
            }
        }
    }
}
