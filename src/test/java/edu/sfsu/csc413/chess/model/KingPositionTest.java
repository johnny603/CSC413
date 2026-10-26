package edu.sfsu.csc413.chess.model;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.factory.PieceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@code Board.kingPosition}: where a side's king stands. Check detection
 * starts here, and the board is the class that knows where things are.
 */
class KingPositionTest {

    @Test
    @DisplayName("finds each king in the opening position")
    void findsEachKingAtStart() {
        Board board = BoardFactory.standard();
        assertEquals(Position.parse("e1"), board.kingPosition(Color.WHITE));
        assertEquals(Position.parse("e8"), board.kingPosition(Color.BLACK));
    }

    @Test
    @DisplayName("follows the king when it moves")
    void followsTheKing() {
        Board board = new Board();
        Piece king = PieceFactory.create(PieceType.KING, Color.WHITE);
        board.place(Position.parse("e1"), king);

        board.apply(Move.quiet(Position.parse("e1"), Position.parse("f2"), king));

        assertEquals(Position.parse("f2"), board.kingPosition(Color.WHITE));
    }

    @Test
    @DisplayName("is null when the side has no king")
    void nullWithoutAKing() {
        Board board = BoardFactory.fromFen("8/8/8/8/8/8/8/R3K3");
        assertNull(board.kingPosition(Color.BLACK));
    }
}
