package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Position;

/**
 * Turns a board into lines of text.
 *
 * <p>This code used to live in {@code Board.toString()}, and moving it here is
 * the whole point of the exercise. Which end of the board is at the top, what
 * an empty square looks like, whether there is a file legend along the bottom —
 * none of those are facts about chess. They are decisions about a
 * <em>display</em>, and a display that wants to make them differently should
 * not have to change the model to do it.
 *
 * <p>The payoff is immediate and concrete: showing the board from black's point
 * of view is now a matter of passing {@link Color#BLACK} to
 * {@link #render(Board, Color)}. Before the move it would have meant editing
 * {@code Board} — a model class — to change how a screen looks.
 */
public class TextBoardRenderer {

    private final PieceGlyphs glyphs;

    public TextBoardRenderer(PieceGlyphs glyphs) {
        this.glyphs = glyphs;
    }

    /** The board as text, seen from white's side. */
    public String render(Board board) {
        return render(board, Color.WHITE);
    }

    /**
     * The board as text, seen from {@code perspective}'s side of the table.
     *
     * <p>Note that flipping inverts <em>both</em> axes. Turning the board around
     * moves rank 1 to the top and also moves the a-file to the right; flipping
     * only the ranks is the classic bug, and it shows up as the king and queen
     * appearing to swap places.
     */
    public String render(Board board, Color perspective) {
        boolean flipped = perspective == Color.BLACK;
        StringBuilder text = new StringBuilder();

        for (int row = 0; row < Position.BOARD_SIZE; row++) {
            // Row 0 is the top line of the display. White sees rank 8 there;
            // black, sitting opposite, sees rank 1.
            int rank = flipped ? row : Position.BOARD_SIZE - 1 - row;
            text.append(rank + 1).append(' ');

            for (int column = 0; column < Position.BOARD_SIZE; column++) {
                int file = flipped ? Position.BOARD_SIZE - 1 - column : column;
                if (column > 0) {
                    text.append(' ');
                }
                text.append(glyphs.glyphFor(board.pieceAt(new Position(file, rank))));
            }
            text.append('\n');
        }

        text.append(' ');
        for (int column = 0; column < Position.BOARD_SIZE; column++) {
            int file = flipped ? Position.BOARD_SIZE - 1 - column : column;
            text.append(' ').append((char) ('a' + file));
        }
        return text.toString();
    }
}
