package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * A console board with a box around it, drawn from the side that is about to
 * move. After each move the board turns around, because that side has changed.
 * The rules stay in {@link Game}; this class only draws and reads a line.
 */
public class BorderedView implements BoardView {

    private final TextBoardRenderer renderer =
            new TextBoardRenderer(PieceGlyphs.FIGURES);
    private final PrintStream out;
    private final Scanner in;

    /** A bordered board reading from standard input. */
    public BorderedView() {
        this(System.out, new Scanner(System.in));
    }

    /** Injectable streams, so the view can be driven without a terminal. */
    public BorderedView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        out.println();
        out.println(box(renderer.render(game.board(), game.sideToMove())));
        out.println();
        out.println(game.sideToMove() + " to move  |  " + game.status());
        out.println("(board is drawn from " + game.sideToMove() + "'s side)");
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

    /** Puts a one-cell frame around a multi-line board. */
    static String box(String board) {
        String[] lines = board.split("\n", -1);
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, line.length());
        }
        StringBuilder framed = new StringBuilder();
        framed.append('┌').append("─".repeat(width + 2)).append("┐\n");
        for (String line : lines) {
            framed.append("│ ").append(line);
            framed.append(" ".repeat(width - line.length()));
            framed.append(" │\n");
        }
        framed.append('└').append("─".repeat(width + 2)).append('┘');
        return framed.toString();
    }
}
