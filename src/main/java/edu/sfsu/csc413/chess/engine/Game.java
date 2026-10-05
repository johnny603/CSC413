package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * One game of chess: the board, whose turn it is, and the moves played so far.
 *
 * <p>Everything outside the engine — the console view, the controller,
 * eventually an AI — talks to this class and to nothing behind it. The view
 * never applies a move to a {@link Board} directly. That single rule is what
 * will let us swap the console for a graphical UI later without changing a
 * line of the rules.
 *
 * <p>{@code Game} is built by composition: it <em>has</em> a board, a history,
 * and a side to move, and it owns all three. Nothing else holds them.
 */
public class Game {

    // has-a board
    private final Board board;

    // has-a move history
    private final List<Move> history = new ArrayList<>();

    // has-a side to move
    private Color sideToMove;

    /**
     * Starts a game from the standard opening position.
     */
    public Game() {
        this(BoardFactory.standard(), Color.WHITE);
    }

    /**
     * Starts a game from a given position — used by tests and FEN loading.
     */
    public Game(Board board, Color sideToMove) {
        this.board = board;
        this.sideToMove = sideToMove;
    }

    public Board board() {
        return board;
    }

    public Color sideToMove() {
        return sideToMove;
    }

    /**
     * The moves played so far, oldest first.
     *
     * <p>Returns a copy. The list {@code Game} keeps is private, and handing it
     * out would let any caller rewrite the game's past.
     */
    public List<Move> history() {
        return List.copyOf(history);
    }

    /**
     * Every move the side to move may play right now.
     *
     * <p>At M3 "legal" means the right colour and the right geometry: every
     * pseudo-legal move of every piece belonging to {@link #sideToMove()}. King
     * safety arrives in M5 and tightens this method without changing its name
     * or its callers.
     */
    // call the moveGenerator
    public List<Move> legalMoves() {
        List<Move> moves = MoveGenerator.pseudoLegalMoves(board, sideToMove);

        return moves;
    }

    /**
     * Finds the legal move matching notation such as {@code "e2e4"} or
     * {@code "e7e8q"}, if there is one.
     *
     * <p>Returning an {@link Optional} rather than null or an exception says
     * plainly that "no such move" is an ordinary outcome here — the player
     * simply typed something they cannot play — and it makes the caller deal
     * with that case.
     */
    public Optional<Move> findLegalMove(String notation) {
        // check the shape of the string
        if (notation.length() != 4 && notation.length() != 5) {
            return Optional.empty();
        }
        // compare move with its toString using .equals()
        for (Move move : legalMoves()) {
            if (move.toString().equals(notation)) {
                return Optional.of(move);
            }
        }
        return Optional.empty();
    }

    /**
     * Plays a move and passes the turn.
     *
     * @throws IllegalArgumentException if the move is not currently legal —
     *         a programming error, since callers should choose from
     *         {@link #legalMoves()}
     */
    public void play(Move move) {
        if (!legalMoves().contains(move)) {
            throw new IllegalArgumentException("Illegal move: " + move);
        }
        board.apply(move);
        history.add(move);
        sideToMove = sideToMove.opposite();
    }

    /**
     * Takes back the most recent move, returning it if there was one.
     *
     * <p>Undo costs us almost nothing because {@link Move} already records what
     * was captured. In Week 10 this becomes the Command pattern proper.
     */
    // get the last move using the list interface
    // then remove it
    public Optional<Move> undoLastMove() {
        // check if the history list is empty
        if (history.isEmpty()) {
            return Optional.empty();
        }

        // get the last move and remove it from history
        Move move = history.get(history.size() - 1);
        history.remove(history.size() - 1);

        // undo move from board and return to opposite color
        board.undo(move);
        sideToMove = sideToMove.opposite();

        // return the move undone using the return type
        return Optional.of(move);
    }
}
