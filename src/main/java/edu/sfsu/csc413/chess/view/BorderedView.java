package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Position;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Color;

import java.io.PrintStream;
import java.util.Scanner;

public class BorderedView implements BoardView {
    // Use the FIGURES glyph set
    private final PieceGlyphs glyphs = PieceGlyphs.FIGURES;
    private final PrintStream out;
    private final Scanner in;

    public BorderedView() {
        this(System.out, new Scanner(System.in));
    }

    public BorderedView(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void render(Game game) {
        Board board = game.board();

        // Determine viewing orientation based on the side to move
        boolean whiteView = game.sideToMove() == Color.WHITE;

        int startRank = whiteView ? 7 : 0;
        int endRank = whiteView ? -1 : 8;
        int rankStep = whiteView ? -1 : 1;

        int startFile = whiteView ? 0 : 7;
        int endFile = whiteView ? 8 : -1;
        int fileStep = whiteView ? 1 : -1;

        out.println();

        // Top border
        out.print("  ┌");
        for (int file = 0; file < 8; file++) {
            out.print("───");
            out.print(file == 7 ? "┐" : "┬");
        }
        out.println();

        // Render each rank
        for (int rank = startRank; rank != endRank; rank += rankStep) {

            out.print((rank + 1) + " │");

            // Render each square
            for (int file = startFile; file != endFile; file += fileStep) {
                Piece piece = board.pieceAt(new Position(file, rank));
                String glyph = glyphs.glyphFor(piece);

                out.print(" " + glyph + " │");
            }

            out.println();

            // Separator between ranks
            if (rank != (whiteView ? 0 : 7)) {
                out.print("  ├");

                for (int file = 0; file < 8; file++) {
                    out.print("───");
                    out.print(file == 7 ? "┤" : "┼");
                }

                out.println();
            }
        }

        // Bottom border
        out.print("  └");
        for (int file = 0; file < 8; file++) {
            out.print("───");
            out.print(file == 7 ? "┘" : "┴");
        }
        out.println();

        // File labels
        out.print("   ");
        for (int file = startFile; file != endFile; file += fileStep) {
            out.print(" " + (char) ('a' + file) + "  ");
        }

        out.println();
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

        return PlayerAction.parse(
                in.hasNextLine() ? in.nextLine() : "quit"
        );
    }
}
