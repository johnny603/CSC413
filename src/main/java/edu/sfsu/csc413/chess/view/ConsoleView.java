package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleView implements BoardView {

    // Use the LETTERS glyph set
    private final TextBoardRenderer renderer =
            new TextBoardRenderer(PieceGlyphs.LETTERS);
    // Print stream for output
    private final PrintStream out;
    // Scanner for input
    private final Scanner in;
    // Color perspective
    private final Color perspective;

    public ConsoleView() {
        this(System.out, new Scanner(System.in), Color.WHITE);
    }

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

    @Override
    public void start(GameController controller) {
        controller.runLoop();
    }

    // Get the next action from the user
    @Override
    public PlayerAction nextAction(Game game) {
        out.print("Your move (e.g. e2e4, 'undo', 'moves', 'quit'): ");
        out.flush();
        return PlayerAction.parse(
                in.hasNextLine() ? in.nextLine() : "quit"
        );
    }
}
