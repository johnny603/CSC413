package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Position;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * A console board drawn in receding ranks, so the near edge is the side about
 * to move. The class is {@code ThreeDView} rather than {@code 3DView}: a Java
 * name cannot start with a digit. The command-line word is still {@code 3d}.
 *
 * <p>Nothing here decides a chess rule. The picture is built from
 * {@link Game#board()} and {@link Game#sideToMove()}, and the typed line is
 * handed to the controller as a {@link PlayerAction}.
 */
public class ThreeDView implements BoardView {

    private final PrintStream out;
    private final Scanner in;

    /** A 3D board reading from standard input. */
    public ThreeDView() {
        this(System.out, new Scanner(System.in));
    }

    /** Injectable streams, so the view can be driven without a terminal. */
    public ThreeDView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        out.println();
        out.println(draw(game.board(), game.sideToMove()));
        out.println();
        out.println(game.sideToMove() + " to move  |  " + game.status());
        out.println("(near edge is " + game.sideToMove() + ")");
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

    /**
     * Farther ranks are indented. The bottom edge, with no indent, is the
     * side to move, sitting closest to the player.
     */
    static String draw(Board board, Color perspective) {
        boolean flipped = perspective == Color.BLACK;
        int size = Position.BOARD_SIZE;
        StringBuilder text = new StringBuilder();
        for (int row = 0; row < size; row++) {
            int rank = flipped ? row : size - 1 - row;
            int indent = (size - 1 - row) * 2;
            text.append(" ".repeat(indent + 1));
            text.append('+');
            for (int column = 0; column < size; column++) {
                text.append("---+");
            }
            text.append('\n');
            text.append(" ".repeat(indent));
            text.append('/');
            for (int column = 0; column < size; column++) {
                int file = flipped ? size - 1 - column : column;
                String glyph = PieceGlyphs.FIGURES.glyphFor(
                        board.pieceAt(new Position(file, rank)));
                text.append(' ').append(glyph).append(" /");
            }
            text.append("  ").append(rank + 1).append('\n');
        }
        text.append('+');
        for (int column = 0; column < size; column++) {
            text.append("---+");
        }
        text.append('\n');
        text.append(' ');
        for (int column = 0; column < size; column++) {
            int file = flipped ? size - 1 - column : column;
            text.append("  ").append((char) ('a' + file)).append(' ');
        }
        return text.toString();
    }
}
