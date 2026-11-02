package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * The same game as {@link ConsoleView}, drawn with Unicode chess figures.
 *
 * <p>This class is the demonstration the course is built around. Read it and
 * note what is <em>not</em> here: no rules, no move legality, no board layout,
 * and no change to {@link GameController}, which does not know this class
 * exists. A whole second user interface costs one small class and one line in
 * {@code Main}.
 *
 * <p>It also gets something for free that the model could never have offered:
 * pass {@link Color#BLACK} and the board is drawn from black's side of the
 * table. That is a view decision, made in the view.
 *
 * <p><strong>On the duplication between this class and {@link ConsoleView}.</strong>
 * They are the same file apart from one expression — the glyph table on the
 * field below. That is a DRY violation we are keeping deliberately, so that the
 * two can be read side by side and the seam is visible rather than described.
 * The reasoning is written out in full on {@link ConsoleView}; the refactor that
 * removes it (a single {@code TextView} taking a {@link PieceGlyphs}) is a good
 * exercise once you have seen why the pair exists.
 */
public class EmojiView implements BoardView {

    private final TextBoardRenderer renderer =
            new TextBoardRenderer(PieceGlyphs.FIGURES);
    private final PrintStream out;
    private final Scanner in;
    private final Color perspective;

    /** A figures view reading from standard input, drawn from white's side. */
    public EmojiView() {
        this(System.out, new Scanner(System.in), Color.WHITE);
    }

    /** Injectable streams and a choice of which side to draw the board from. */
    public EmojiView(PrintStream out, Scanner in, Color perspective) {
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

    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    @Override
    public PlayerAction nextAction(Game game) {
        out.print("Your move (e.g. e2e4, 'undo', 'moves', 'quit'): ");
        out.flush();
        return PlayerAction.parse(in.hasNextLine() ? in.nextLine() : "quit");
    }
}
