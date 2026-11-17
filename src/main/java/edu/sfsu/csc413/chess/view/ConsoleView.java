package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Displays the game as text and reads moves from the keyboard.
 *
 * <p>Notice what this class does <em>not</em> do: it never decides whether a
 * move is legal, never applies one, and never works out whether the game is
 * over. It draws what it is given and reports what the player typed. All the
 * rules live behind {@link Game}. Keeping that line clean is the whole point of
 * the exercise — a view that starts making rules decisions is the most common
 * way student projects turn into an unmaintainable tangle.
 *
 * <p>Compare this class with {@link EmojiView}. Read them side by side and you
 * will find they differ in exactly <em>one</em> expression: the glyph table
 * handed to {@link TextBoardRenderer}. Every other line — {@code render},
 * {@code showMessage}, {@code start}, {@code nextAction}, even the prompt
 * string — is identical. That is what "the same model can support different
 * views" means in practice.
 *
 * <p><strong>Yes, this is duplication, and we are choosing to keep it.</strong>
 * The DRY fix is obvious and worth doing as an exercise: one {@code TextView}
 * class taking a {@link PieceGlyphs} in its constructor, and both of these files
 * disappear. Do that and you will have removed about forty repeated lines.
 *
 * <p>What you would also remove is the thing this pair is here to show. The
 * claim the architecture makes is that a whole new user interface costs one
 * class and one line in {@code Main}. Two files you can hold side by side prove
 * that claim in a way a single parameterised class does not — the reader sees
 * the seam instead of being told about it. That is a teaching decision, not an
 * engineering one, and in code shipped for any other purpose the refactor would
 * win. Knowing <em>which</em> kind of code you are writing is the actual skill.
 */
public class ConsoleView implements BoardView {

    private final TextBoardRenderer renderer =
            new TextBoardRenderer(PieceGlyphs.LETTERS);
    private final PrintStream out;
    private final Scanner in;
    private final Color perspective;

    /** A view reading from standard input, drawn from white's side. */
    public ConsoleView() {
        this(System.out, new Scanner(System.in), Color.WHITE);
    }

    /** Injectable streams, so tests can drive the view without a terminal. */
    public ConsoleView(PrintStream out, Scanner in, Color perspective) {
        this.out = out;
        this.in = in;
        this.perspective = perspective;
    }

    @Override
    public void render(Game game) {
        out.println();
        out.println(renderer.render(game.board(), perspective));
        out.println();
        out.println(game.sideToMove() + " to move  |  " + game.status());
    }

    @Override
    public void showMessage(String message) {
        out.println(message);
    }

    /**
     * A console is happy to be asked what to do next, so it simply lets the
     * controller drive. An event-driven view would instead show its window and
     * return from here immediately.
     */
    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    @Override
    public PlayerAction nextAction(Game game) {
        out.print("Your move (e.g. e2e4, 'undo', 'moves', 'quit'): ");
        out.flush();
        // End of input (a piped file running out, or Ctrl-D) means the player
        // is done, so it is treated exactly like typing "quit".
        return PlayerAction.parse(in.hasNextLine() ? in.nextLine() : "quit");
    }
}
