package edu.sfsu.csc413.chess;

// import extra views if you create more
import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.GameController;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;

/**
 * Entry point.
 *
 * <p>At M0 this does nothing but prove the toolchain works. It grows into the
 * real launcher as the engine appears underneath it.
 */
public final class Main {


    public static void main(String[] args) {
        Game game = new Game();
        BoardView view = chooseView(args);

        GameController controller = new GameController(game, view);
        controller.start();
    }

    // To add another view later, create a class that implements BoardView
    // and add another command-line option here.
    private static BoardView chooseView(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("emoji")) {
            return new EmojiView();
        }

        return new ConsoleView();
    }

    private Main() {
    }
}
