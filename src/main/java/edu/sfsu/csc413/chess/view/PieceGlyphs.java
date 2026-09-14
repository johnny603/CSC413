package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Piece;

/**
 * How a view spells a piece.
 *
 * <p>An emoji board and a letters board disagree about exactly one thing: what
 * to draw in a square. So that one thing is what we made swappable, and
 * everything else — the layout, the flip, the file legend — is shared. Compare
 * that with the alternative of two whole rendering classes that happen to
 * duplicate the same nested loop.
 *
 * <p>This is a functional interface, so an implementation can be written as a
 * lambda. The two supplied below are the ones the course uses.
 */
@FunctionalInterface
public interface PieceGlyphs {

    /**
     * The text for one square.
     *
     * @param piece the piece standing there, or {@code null} if it is empty
     */
    String glyphFor(Piece piece);

    /**
     * FEN letters: uppercase for white, lowercase for black, {@code .} for an
     * empty square. This is the look the course starts with.
     */
    PieceGlyphs LETTERS = piece ->
            piece == null ? "." : String.valueOf(piece.symbol());

    /**
     * Unicode chess figures (U+2654–U+265F), with a middle dot for empty.
     *
     * <p>A note that matters more than it looks: these are <em>chess symbols</em>,
     * not emoji. They occupy one terminal column each, so the board stays
     * square. True emoji are double-width and shear every column out of
     * alignment — which is why this is the glyph set to copy when adding a
     * "prettier" board.
     */
    PieceGlyphs FIGURES = piece -> {
        if (piece == null) {
            return "·";
        }
        boolean white = piece.color() == Color.WHITE;
        return switch (piece.type()) {
            case KING -> white ? "♔" : "♚";
            case QUEEN -> white ? "♕" : "♛";
            case ROOK -> white ? "♖" : "♜";
            case BISHOP -> white ? "♗" : "♝";
            case KNIGHT -> white ? "♘" : "♞";
            case PAWN -> white ? "♙" : "♟";
        };
    };
}
