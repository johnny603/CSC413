package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Piece;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * The console game again, with a glyph set that is neither FEN letters nor
 * chess figures. Each square is two columns: {@code Kw} is a white king,
 * {@code Pb} is a black pawn, and {@code ··} is empty.
 */
public class NewGlyph implements BoardView {

    /**
     * Two-column names. Empty squares use the same width so the files stay
     * aligned; a one-column dot would shove every piece after it sideways.
     */
    static final PieceGlyphs GLYPHS = piece -> {
        if (piece == null) {
            return "··";
        }
        String kind = switch (piece.type()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };
        return kind + (piece.color() == Color.WHITE ? "w" : "b");
    };

    private final TextBoardRenderer renderer = new TextBoardRenderer(GLYPHS);
    private final PrintStream out;
    private final Scanner in;
    private final Color perspective;

    /** A glyph board reading from standard input, drawn from white's side. */
    public NewGlyph() {
        this(System.out, new Scanner(System.in), Color.WHITE);
    }

    /** Injectable streams and a choice of which side to draw the board from. */
    public NewGlyph(PrintStream out, Scanner in, Color perspective) {
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

    /** Exposed so a caller can see what one square looks like. */
    static String glyph(Piece piece) {
        return GLYPHS.glyphFor(piece);
    }
}
