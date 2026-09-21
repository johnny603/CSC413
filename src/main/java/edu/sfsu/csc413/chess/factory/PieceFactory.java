package edu.sfsu.csc413.chess.factory;

import edu.sfsu.csc413.chess.model.Bishop;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.King;
import edu.sfsu.csc413.chess.model.Knight;
import edu.sfsu.csc413.chess.model.Pawn;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Queen;
import edu.sfsu.csc413.chess.model.Rook;

/**
 * Creates pieces from a type and color, or from a FEN letter.
 *
 * <p>This is the Factory pattern, and it earns its place by answering a
 * question the rest of the engine would otherwise have to answer repeatedly:
 * "given that I want a black rook, which constructor do I call?" Concentrating
 * that knowledge here — at the boundary where we turn data into objects — keeps
 * it out of the engine, which never needs to know that {@code Queen} has a
 * constructor at all.
 *
 * <p><strong>One honest caveat.</strong> You will find a second switch over
 * piece types in {@code Board.createPromoted}, which builds the piece a pawn
 * promotes into. Ideally this class would be the only place; it is not. The
 * comment on that method explains why we accept the duplication rather than add
 * a dependency from {@code model} to {@code factory}. Notice that the
 * {@code switch} below has no {@code default} branch, so adding a seventh
 * {@link PieceType} breaks the build here — but <em>not</em> there, because that
 * one throws on the cases it does not handle. That asymmetry is exactly the kind
 * of thing duplication costs you.
 *
 * <p>We study this properly in Week 9.
 */
public final class PieceFactory {

    private PieceFactory() {
        // Static-only utility; there is no reason to instantiate it.
    }

    /** Creates a piece of the given type and color. */
    public static Piece create(PieceType type, Color color) {
        return switch (type) {
            case PAWN -> new Pawn(color);
            case KNIGHT -> new Knight(color);
            case BISHOP -> new Bishop(color);
            case ROOK -> new Rook(color);
            case QUEEN -> new Queen(color);
            case KING -> new King(color);
        };
    }

    /**
     * Creates a piece from its FEN letter: uppercase is white, lowercase black.
     *
     * @throws IllegalArgumentException if the letter names no piece
     */
    public static Piece fromSymbol(char letter) {
        Color color = Character.isUpperCase(letter) ? Color.WHITE : Color.BLACK;
        return create(PieceType.fromSymbol(letter), color);
    }
}
